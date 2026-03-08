package io.redis.movies.searcher;

import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import io.redis.movies.searcher.core.service.MovieService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableRedisDocumentRepositories
public class RedisMoviesSearcher {

    public static void main(String[] args) {
        SpringApplication.run(RedisMoviesSearcher.class, args);
    }

    @Bean
    CommandLineRunner loadData(MovieService movieService) {
        // Make sure to implement the call to regenerate
        // the embeddings during the application startup
        return null;
    }

}
