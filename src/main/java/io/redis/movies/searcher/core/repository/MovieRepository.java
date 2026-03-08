package io.redis.movies.searcher.core.repository;

import com.redis.om.spring.repository.RedisDocumentRepository;
import io.redis.movies.searcher.core.domain.Movie;

public interface MovieRepository extends RedisDocumentRepository<Movie, Integer> {
}
