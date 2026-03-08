package io.redis.movies.searcher.core.repository;

import com.redis.om.spring.repository.RedisDocumentRepository;
import io.redis.movies.searcher.core.domain.Keyword;

public interface KeywordRepository extends RedisDocumentRepository<Keyword, String> {
}
