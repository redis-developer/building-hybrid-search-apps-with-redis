package io.redis.movies.searcher;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRedisDocumentRepositories
public class RedisMoviesSearcher {

    public static void main(String[] args) {
        SpringApplication.run(RedisMoviesSearcher.class, args);
    }

}
