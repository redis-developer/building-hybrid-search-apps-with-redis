# Lab 4: Implementing Native Hybrid Search

## 🎯 Learning Objectives
By the end of this lab, you will:
- Implement native Redis hybrid retrieval in the service layer
- Understand how lexical and vector signals are fused server-side
- Switch controller execution from manual hybrid to native hybrid
- Compare relevance and latency behavior with the previous approach

#### 🕗 Estimated Time: 25 minutes

## 🏗️ What You're Building
In this lab, you'll move from app-orchestrated manual search composition to native Redis hybrid search.

This includes:
- **`SearchService.nativeHybridSearch(...)`** implementation
- **Controller route switch** to native hybrid
- **Hybrid weighting (`alpha`)** configuration for text/vector balance

### Architecture Overview
![search.png](images/search.png)

## 📋 Prerequisites Check
Before starting, confirm the checklist for the setup option you selected:

### Option 1: GitHub Codespaces
- [ ] Codespace is running for this repository
- [ ] Ports `8080`, `8081`, `5540`, and `6379` are forwarded
- [ ] Lab 3 completed in this Codespace environment

### Option 2: Dev Containers locally
- [ ] Project opened in your IDE Dev Container
- [ ] Containers are healthy and ports are available/forwarded
- [ ] Lab 3 completed in this Dev Container environment

### Option 3: Local development
- [ ] Local Docker environment is running
- [ ] Lab 3 completed locally
- [ ] Redis, frontend, and backend services are up

### Lab-specific requirements
- [ ] Embeddings already generated for movie records
- [ ] Backend and Redis running

## 🚀 Setup Instructions
> 💡 For GitHub Codespaces and Dev Containers, use forwarded URLs from the Ports panel for browser access.  
> From workspace terminals, prefer service DNS names (for example, `redis-database`) when connecting to Redis.

### Step 1: Implement `nativeHybridSearch(...)`
Open `src/main/java/io/redis/movies/searcher/core/service/SearchService.java`.

In this branch, `nativeHybridSearch(...)` returns `null`.

Replace this:
```java
public Pair<List<MovieDTO>, ResultType> nativeHybridSearch(String query, Integer limit) {
    // Implement this method to leverage native hybrid search
    return null;
}
```

With this:
```java
public Pair<List<MovieDTO>, ResultType> nativeHybridSearch(String query, Integer limit) {
    logger.info("Received query: {}", query);
    logger.info("-------------------------");
    final int resultLimit = (limit == null) ? DEFAULT_RESULT_LIMIT : limit;

    // Create the embedding for the query
    var embeddingStartTime = System.currentTimeMillis();
    float[] queryAsVector = getQueryAsVector(query);
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
```

What this code does:
- Creates a query embedding once with `getQueryAsVector(query)`.
- Runs native Redis hybrid search with both text (`Movie$.TITLE`) and vector (`Movie$.PLOT_EMBEDDING`) signals.
- Uses `alpha = 0.7f` to weight semantic relevance more than lexical relevance.
- Returns `ResultType.HYBRID` and keeps the same DTO mapping pipeline.

### Step 2: Switch controller to native hybrid
Open `src/main/java/io/redis/movies/searcher/core/controller/SearchController.java`.

Replace this:
```java
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
```

With this:
```java
@GetMapping("/search")
public Map<String, Object> search(
        @RequestParam(required = true) String query,
        @RequestParam(required = false) Integer limit
) {
    var matchedMovies = searchService.nativeHybridSearch(query, limit);
    return Map.of(
            "resultType", matchedMovies.getSecond().name(),
            "matchedMovies", matchedMovies.getFirst()
    );
}
```

What this code does:
- Switches the `/search` endpoint from manual merge logic to native hybrid logic.
- Preserves the response contract, so the frontend continues to work without changes.

### Step 3: Build and run
If you are using **Local development**, run:

```bash
./mvnw clean package
./mvnw spring-boot:run
```

If you are using **GitHub Codespaces** or **Dev Containers**, run the same command from the workspace terminal.

## 🧪 Testing Your Implementation
### 1. Keep backend running
Use the backend process started in Step 3 of Setup Instructions.

### 2. API tests
Run a lexical-heavy query:
```bash
curl "http://localhost:8081/search?query=star&limit=5"
```

Run a semantic-style query:
```bash
curl "http://localhost:8081/search?query=dude%20who%20teaches%20rock&limit=5"
```

Both responses should include:
- `resultType: "HYBRID"`
- a non-empty `matchedMovies` list

### 3. Check backend logs
For each request, validate logs include:
- `Embedding took ... ms`
- `Hybrid search took ... ms`
- `Found ... movies`

### 4. UI validation
1. Open `http://localhost:8080/redis-movies-searcher`
2. Search for `star`
3. Search for `dude who teaches rock`
4. Confirm no browser errors and consistent results

### 5. Optional comparison
Temporarily switch `SearchController` back to `manualHybridSearch(...)` to compare latency/relevance, then restore `nativeHybridSearch(...)`.

## 🎨 Understanding the Code
### 1. Native hybrid search
- Redis combines lexical relevance and vector similarity internally
- Reduces app-side orchestration and merge logic

### 2. `alpha` weighting
- Controls balance between vector score and lexical score
- Higher values bias toward semantic similarity

### 3. Controller routing
- One switch point makes strategy transition explicit for the workshop

## 🔍 What's Still Missing?
At this stage, hybrid retrieval is native, but:
- ❌ Prompt embeddings are not cached yet
- ❌ Repeated semantic prompts still recompute embeddings

## 🐛 Troubleshooting
<details>
<summary>`nativeHybridSearch` returns no matches</summary>

Verify embeddings exist (`plotEmbedding`) and index has the correct vector field configuration.
</details>

<details>
<summary>Vector conversion/runtime errors</summary>

Ensure vector conversion matches API expectations (`float[]` vs bytes where required).
</details>

<details>
<summary>Result type still behaves like manual path</summary>

Confirm `SearchController` is calling `nativeHybridSearch(...)`.
</details>

## 🎉 Lab Completion
Congratulations. You now have:
- ✅ Native hybrid search implemented
- ✅ API switched to native retrieval path
- ✅ Cleaner server-side search composition

## ➡️ Next Steps
Proceed to [Lab 5: Caching Prompt Embedding](../../tree/lab-5-starter/README.md)

```bash
git checkout lab-5-starter
```
