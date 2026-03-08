package io.redis.movies.searcher.core.service;

import org.springframework.data.util.Pair;
import com.redis.om.spring.search.stream.EntityStream;
import io.redis.movies.searcher.core.domain.*;
import io.redis.movies.searcher.core.dto.MovieDTO;
import com.redis.om.spring.vectorize.Embedder;
import io.redis.movies.searcher.core.repository.KeywordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final EntityStream entityStream;
    private final Embedder embedder;
    private final KeywordRepository keywordRepository;

    public SearchService(EntityStream entityStream, Embedder embedder, KeywordRepository keywordRepository) {
        this.entityStream = entityStream;
        this.embedder = embedder;
        this.keywordRepository = keywordRepository;
    }

    public Pair<List<MovieDTO>, ResultType> manualHybridSearch(String query, Integer limit) {
        logger.info("Received query: {}", query);
        logger.info("-------------------------");
        final int resultLimit = (limit == null) ? DEFAULT_RESULT_LIMIT : limit;

        // Execute FTS search
        var ftsSearchStartTime = System.currentTimeMillis();
        List<Movie> ftsMovies = entityStream.of(Movie.class)
                .filter(Movie$.TITLE.eq(query).or(Movie$.TITLE.containing(query)))
                .limit(resultLimit)
                .sorted(Comparator.comparing(Movie::getTitle))
                .collect(Collectors.toList());

        var ftsSearchEndTime = System.currentTimeMillis();
        logger.info("FTS search took {} ms", ftsSearchEndTime - ftsSearchStartTime);

        // Convert FTS results to DTOs
        List<MovieDTO> ftsMovieDTOs = convertToDTOs(ftsMovies);

        // If FTS results are sufficient, return them immediately
        if (ftsMovies.size() >= resultLimit) {
            return Pair.of(ftsMovieDTOs, ResultType.FTS);
        }

        // Create the embedding query
        var embeddingStartTime = System.currentTimeMillis();
        float[] queryAsFloatVector = getQueryAsVector(query);
        byte[] queryAsVector = convertToByteArray(queryAsFloatVector);
        var embeddingEndTime = System.currentTimeMillis();
        logger.info("Embedding took {} ms", embeddingEndTime - embeddingStartTime);

        // Execute VSS search
        var vssSearchStartTime = System.currentTimeMillis();
        List<Movie> vssMovies = entityStream.of(Movie.class)
                .filter(Movie$.PLOT_EMBEDDING.knn(resultLimit, queryAsVector))
                .limit(resultLimit)
                .sorted(Movie$._PLOT_EMBEDDING_SCORE)
                .collect(Collectors.toList());
        var vssSearchEndTime = System.currentTimeMillis();
        logger.info("VSS search took {} ms", vssSearchEndTime - vssSearchStartTime);

        // Combine results
        LinkedHashMap<Integer, Movie> uniqueMoviesMap = new LinkedHashMap<>();
        ftsMovies.forEach(movie -> uniqueMoviesMap.put(movie.getId(), movie));
        vssMovies.forEach(movie -> uniqueMoviesMap.putIfAbsent(movie.getId(), movie));

        // Limit and convert combined results to DTOs
        List<Movie> uniqueMovies = uniqueMoviesMap.values().stream()
                .limit(resultLimit)
                .collect(Collectors.toList());

        return Pair.of(convertToDTOs(uniqueMovies), ftsMovies.isEmpty() ? ResultType.VSS : ResultType.HYBRID);
    }

    public Pair<List<MovieDTO>, ResultType> nativeHybridSearch(String query, Integer limit) {
        logger.info("Received query: {}", query);
        logger.info("-------------------------");
        final int resultLimit = (limit == null) ? DEFAULT_RESULT_LIMIT : limit;

        // Create the embedding for the query
        var embeddingStartTime = System.currentTimeMillis();
        float[] queryAsVector = getQueryAsVectorUsingKeyword(query);
        var embeddingEndTime = System.currentTimeMillis();
        logger.info("Embedding took {} ms", embeddingEndTime - embeddingStartTime);

        var hybridSearchStartTime = System.currentTimeMillis();
        List<Movie> movies = entityStream.of(Movie.class)
                .hybridSearch(
                        query,                    // text query
                        Movie$.TITLE,             // text field to search
                        queryAsVector,            // query embedding as float[]
                        Movie$.PLOT_EMBEDDING,    // vector field to search
                        0.7f                      // alpha: 70% vector, 30% text
                )
                .limit(resultLimit)
                .collect(Collectors.toList());

        ResultType resultType = ResultType.HYBRID;
        var hybridSearchEndTime = System.currentTimeMillis();

        logger.info("Hybrid search took {} ms", hybridSearchEndTime - hybridSearchStartTime);
        logger.info("Found {} movies", movies.size());

        return Pair.of(convertToDTOs(movies), resultType);
    }

    private float[] getQueryAsVector(String query) {
        if (!embedder.isReady()) {
            throw new IllegalStateException("Embedder is not ready.");
        }

        List<float[]> embeddings = embedder.getTextEmbeddingsAsFloats(List.of(query), Movie$.PLOT);
        if (embeddings.isEmpty() || embeddings.getFirst() == null) {
            throw new IllegalStateException("Failed to create embedding for query.");
        }

        return embeddings.getFirst();
    }

    private byte[] convertToByteArray(float[] values) {
        ByteBuffer buffer = ByteBuffer
                .allocate(values.length * Float.BYTES)
                .order(ByteOrder.LITTLE_ENDIAN);

        for (float value : values) {
            buffer.putFloat(value);
        }

        return buffer.array();
    }

    private float[] getQueryAsVectorUsingKeyword(String query) {
        return entityStream.of(Keyword.class)
                .filter(Keyword$.VALUE.containing(query))
                .findFirst()
                .map(Keyword::getEmbedding)
                .orElseGet(() -> keywordRepository.save(new Keyword(query)).getEmbedding());
    }

    private List<MovieDTO> convertToDTOs(List<Movie> movies) {
        return movies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MovieDTO convertToDTO(Movie movie) {
        return new MovieDTO(
                movie.getTitle(),
                movie.getYear(),
                movie.getPlot(),
                movie.getRating(),
                movie.getActors().toArray(new String[0])
        );
    }

    private static final Integer DEFAULT_RESULT_LIMIT = 4;
}
