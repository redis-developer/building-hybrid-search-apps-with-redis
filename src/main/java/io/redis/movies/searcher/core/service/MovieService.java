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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MovieService {

    private static final Logger log = LoggerFactory.getLogger(MovieService.class);

    private final MovieRepository movieRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public MovieService(MovieRepository movieRepository,
                        RedisTemplate<String, Object> redisTemplate) {
        this.movieRepository = movieRepository;
        this.redisTemplate = redisTemplate;
    }

    public void regenerateMissingEmbeddings() {
        // Implement this method to generate embeddings during startup
    }

    private static final String KEY_PREFIX = "movie:";
}
