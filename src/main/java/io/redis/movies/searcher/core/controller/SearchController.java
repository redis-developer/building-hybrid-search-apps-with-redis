package io.redis.movies.searcher.core.controller;

import io.redis.movies.searcher.core.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam(required = true) String query,
            @RequestParam(required = false) Integer limit
    ) {
        // Make sure to change the method call here from the current
        // manualHybridSearch() to use nativeHybridSearch() instead
        var matchedMovies = searchService.manualHybridSearch(query, limit);
        return Map.of(
                "resultType", matchedMovies.getSecond().name(),
                "matchedMovies", matchedMovies.getFirst()
        );
    }
}
