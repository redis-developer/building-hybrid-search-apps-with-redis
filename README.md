# Lab 3: Implementing Embedding Creation

## 🎯 Learning Objectives
By the end of this lab, you will:
- Implement the new startup `loadData()` hook in `RedisMoviesSearcher`
- Understand the role of `MovieService` and `MovieRepository` for embedding backfill
- Implement `regenerateMissingEmbeddings()` using Redis SCAN + batched persistence
- Understand why first startup can be slower, and why later startups are faster
- Enable vector search behavior by ensuring movies have `plotEmbedding`

#### 🕗 Estimated Time: 25 minutes

## 🏗️ What You're Building
In this lab, you'll add a startup pipeline that backfills embeddings for previously imported movies.

This includes:
- **`loadData()` in `RedisMoviesSearcher`** to trigger regeneration on startup
- **`MovieService.regenerateMissingEmbeddings()`** implementation
- **`MovieRepository` usage** (`findAllById`, `saveAll`) to process movie batches

### Architecture Overview
![search.png](images/search.png)

## 📋 Prerequisites Check
Before starting, confirm the checklist for the setup option you selected:

### Option 1: GitHub Codespaces
- [ ] Codespace is running for this repository
- [ ] Ports `8080`, `8081`, `5540`, and `6379` are forwarded
- [ ] Lab 2 completed in this Codespace environment

### Option 2: Dev Containers locally
- [ ] Project opened in your IDE Dev Container
- [ ] Containers are healthy and ports are available/forwarded
- [ ] Lab 2 completed in this Dev Container environment

### Option 3: Local development
- [ ] Local Docker environment is running
- [ ] Lab 2 completed locally
- [ ] Redis, frontend, and backend services are up

### Lab-specific requirements
- [ ] Dataset imported into Redis (`movie:*` keys exist)
- [ ] `movie_index` already created

## 🚀 Setup Instructions
> 💡 For GitHub Codespaces and Dev Containers, use forwarded URLs from the Ports panel for browser access.  
> From workspace terminals, prefer service DNS names (for example, `redis-database`) when connecting to Redis.

### Step 1: Implement startup hook
Open `src/main/java/io/redis/movies/searcher/RedisMoviesSearcher.java`.

In this branch, `loadData(...)` returns `null`.

Replace this:
```java
@Bean
CommandLineRunner loadData(MovieService movieService) {
    // Make sure to implement the call to regenerate
    // the embeddings during the application startup
    return null;
}
```

With this:
```java
@Bean
CommandLineRunner loadData(MovieService movieService) {
    return args -> {
        movieService.regenerateMissingEmbeddings();
    };
}
```

### Step 2: Implement embedding regeneration
Open `src/main/java/io/redis/movies/searcher/core/service/MovieService.java`.

You will see a new service class in this lab: `MovieService`.

Its responsibility is to:
- Scan Redis for `movie:*` keys
- Load movies in batches
- Persist only movies missing `plotEmbedding` so embeddings are generated

Also note `MovieRepository` (`src/main/java/io/redis/movies/searcher/core/repository/MovieRepository.java`), which provides the persistence operations used by this flow.

Now replace this:
```java
public void regenerateMissingEmbeddings() {
    // Implement this method to generate embeddings during startup
}
```

With this:
```java
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
```

### Step 3: Rebuild and run
If you are using **Local development**, run:

```bash
./mvnw clean package
./mvnw spring-boot:run
```

If you are using **GitHub Codespaces** or **Dev Containers**, run the same command from the workspace terminal.

Important behavior:
- On first run after Lab 2, startup can take longer because the app scans Redis and generates missing embeddings.
- On later runs, if embeddings are already present, startup is much faster.
- This backfill is required for vector search to return meaningful results.

## 🧪 Testing Your Implementation
### Check backfill progress in logs
Expect logs similar to:
- `Scanning for movies with missing embeddings...`
- `Found X movie keys in Redis`
- `Regenerated embeddings: ...`
- `No movies with missing embeddings found.`

### Verify embeddings in Redis Insight
1. Open Redis Insight (`http://localhost:5540` or forwarded URL)
2. Connect to `redis-database:6379` (Codespaces/Dev Containers) or your local Redis endpoint
3. Browse `movie:*` documents
4. Open a movie and verify `plotEmbedding` now exists

### Semantic query check
```bash
curl "http://localhost:8081/search?query=dude%20who%20teaches%20rock"
```
With embeddings generated, vector search can contribute results for semantic queries.

## 🎨 Understanding the Code
### 1. `MovieService`
- Handles embedding backfill for already imported records
- Uses `MovieRepository` to load and save in batches
- Persists only movies missing `plotEmbedding`

### 2. `CommandLineRunner` in `RedisMoviesSearcher`
- Ensures regeneration runs automatically on startup
- Keeps workshop flow reproducible

### 3. Why this matters
- Lab 2 imported data quickly
- This lab makes that same data vector-searchable without reimport
- Without this step, vector search has no embeddings to compare against

## 🔍 What's Still Missing?
At this stage, the app supports manual hybrid behavior with embeddings, but:
- ❌ Native hybrid implementation is not active yet
- ❌ Prompt embedding cache-aside is not active yet

## 🐛 Troubleshooting
<details>
<summary>No embeddings are generated</summary>

Check that movies contain non-empty `plot` values and missing `plotEmbedding` before startup.
</details>

<details>
<summary>Startup feels slow</summary>

Initial full backfill can take time. Review logs and tune batch settings if needed.
</details>

<details>
<summary>`plotEmbedding` stays null</summary>

Verify startup completed and regeneration did not fail in logs.
</details>

## 🎉 Lab Completion
Congratulations. You now have:
- ✅ Startup embedding backfill implemented
- ✅ Existing movies enriched with vector embeddings
- ✅ Strong semantic retrieval foundation

## ➡️ Next Steps
Proceed to [Lab 4: Implementing Native Hybrid Search](../../tree/lab-4-starter/README.md)

```bash
git checkout lab-4-starter
```
