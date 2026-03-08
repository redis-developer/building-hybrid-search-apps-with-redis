package io.redis.movies.searcher.core.service;

import io.redis.movies.searcher.core.domain.Movie;
import io.redis.movies.searcher.core.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MovieService {

    private static final Logger log = LoggerFactory.getLogger(MovieService.class);
    private static final String KEY_PREFIX = "movie:";

    private final MovieRepository movieRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public MovieService(MovieRepository movieRepository,
                        RedisTemplate<String, Object> redisTemplate) {
        this.movieRepository = movieRepository;
        this.redisTemplate = redisTemplate;
    }

    public void regenerateMissingEmbeddings() {
        log.info("Scanning for movies with missing embeddings...");
        Instant startTime = Instant.now();

        // Phase 1: Scan for all movie keys using SCAN command
        List<Integer> movieIds = new ArrayList<>(10000);
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(KEY_PREFIX + "*")
                .count(1000)
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                String idStr = key.substring(KEY_PREFIX.length());
                try {
                    movieIds.add(Integer.parseInt(idStr));
                } catch (NumberFormatException e) {
                    log.warn("Skipping invalid key: {}", key);
                }
            }
        }

        log.info("Found {} movie keys in Redis", movieIds.size());

        // Phase 2: Load, filter, and save in bounded concurrent batches
        final int batchSize = 200;
        final int estimatedTotal = movieIds.size();
        final int maxWorkers = 2;

        AtomicInteger processedCounter = new AtomicInteger(0);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> inFlight = new ArrayList<>(maxWorkers);

            for (int i = 0; i < movieIds.size(); i += batchSize) {
                List<Integer> batchIds = movieIds.subList(i, Math.min(i + batchSize, movieIds.size()));

                List<Movie> moviesNeedingEmbeddings = new ArrayList<>();
                movieRepository.findAllById(batchIds).forEach(movie -> {
                    if (movie.getPlot() != null && !movie.getPlot().isBlank()
                            && movie.getPlotEmbedding() == null) {
                        moviesNeedingEmbeddings.add(movie);
                    }
                });

                if (moviesNeedingEmbeddings.isEmpty()) {
                    continue;
                }

                List<Movie> batch = new ArrayList<>(moviesNeedingEmbeddings);

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        movieRepository.saveAll(batch);

                        int total = processedCounter.addAndGet(batch.size());
                        int previousMilestone = (total - batch.size()) / 1000;
                        int currentMilestone = total / 1000;
                        if (currentMilestone > previousMilestone) {
                            double percentComplete = (total * 100.0) / estimatedTotal;
                            log.info("Regenerated embeddings: ~{}% ({} movies processed)",
                                    String.format("%.1f", percentComplete), total);
                        }
                    } catch (Exception ex) {
                        log.error("Error saving batch: {}", ex.getMessage(), ex);
                    }
                }, executor);

                inFlight.add(future);

                // Bound concurrency: wait every maxWorkers tasks
                if (inFlight.size() >= maxWorkers) {
                    CompletableFuture.allOf(inFlight.toArray(new CompletableFuture[0])).join();
                    inFlight.clear();
                }
            }

            if (!inFlight.isEmpty()) {
                CompletableFuture.allOf(inFlight.toArray(new CompletableFuture[0])).join();
            }
        }

        int totalProcessed = processedCounter.get();
        if (totalProcessed == 0) {
            log.info("No movies with missing embeddings found.");
            return;
        }

        Duration duration = Duration.between(startTime, Instant.now());
        double seconds = duration.toMillis() / 1000.0;
        log.info("Embedding regeneration complete: {} movies processed in {} seconds",
                totalProcessed, String.format("%.2f", seconds));
    }
}
