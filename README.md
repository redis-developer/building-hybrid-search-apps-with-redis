# Lab 5: Caching Prompt Embedding

## 🎯 Learning Objectives
By the end of this lab, you will:
- Implement a cache-aside strategy for prompt embeddings
- Understand the role of the new `Keyword` document class
- Understand the role of `KeywordRepository` in persistence
- Reuse cached prompt vectors in the native hybrid search path
- Reduce repeated embedding generation for recurring prompts

#### 🕗 Estimated Time: 10 minutes

## 🏗️ What You're Building
In this final lab, you'll add prompt embedding reuse so repeated queries become faster and cheaper.

This includes:
- `Keyword` domain class to store prompt text + embedding vectors
- `KeywordRepository` to persist and retrieve cached prompt embeddings
- `getQueryAsVectorUsingKeyword(...)` implementation in `SearchService`
- Native hybrid path update to use keyword-based embedding lookup

## 📋 Prerequisites Check
Before starting, confirm the checklist for the setup option you selected:

### Option 1: GitHub Codespaces
- [ ] Codespace is running for this repository
- [ ] Ports `8080`, `8081`, `5540`, and `6379` are forwarded
- [ ] Lab 4 completed in this Codespace environment

### Option 2: Dev Containers locally
- [ ] Project opened in your IDE Dev Container
- [ ] Containers are healthy and ports are available/forwarded
- [ ] Lab 4 completed in this Dev Container environment

### Option 3: Local development
- [ ] Local Docker environment is running
- [ ] Lab 4 completed locally
- [ ] Redis, frontend, and backend services are up

### Lab-specific requirements
- [ ] Native hybrid search path already working
- [ ] Backend and Redis running with imported movie data

## 🚀 Setup Instructions
> 💡 If you are using either GitHub Codespaces or Dev Containers, you must use the forwarded URL from the Ports panel for proper access. Also, you may use the sidecar service DNS names from the workspace terminal when needed, such as using `redis-database` to access Redis.
> ![port-mappings.png](images/port-mappings.png)

### Step 1: Inspect the new classes
This lab introduces new components:

- `src/main/java/io/redis/movies/searcher/core/domain/Keyword.java`
- `src/main/java/io/redis/movies/searcher/core/repository/KeywordRepository.java`

What they do:
- `Keyword` is a class that stores prompt text in `value` and its vector in `embedding`.
- `@Vectorize` on `value` populates `embedding` when a new `Keyword` is saved.
- `KeywordRepository` gives CRUD implementation to the Keyword entity.

Also note in `SearchService` constructor that `KeywordRepository` is now injected for cache-aside behavior.

### Step 2: Implement keyword-based embedding lookup
Open `src/main/java/io/redis/movies/searcher/core/service/SearchService.java`.

Replace this:
```java
private float[] getQueryAsVectorUsingKeyword(String query) {
    // Implement this method so it can use the Keyword class
    return null;
}
```

With this:
```java
private float[] getQueryAsVectorUsingKeyword(String query) {
    return entityStream.of(Keyword.class)
            .filter(Keyword$.VALUE.containing(query))
            .findFirst()
            .map(Keyword::getEmbedding)
            .orElseGet(() -> keywordRepository.save(new Keyword(query)).getEmbedding());
}
```

What this code does:
- Checks Redis first for an existing keyword whose `value` matches the query.
- On cache hit, returns the stored `embedding`. The returned value is reused.
- On cache miss, saves `new Keyword(query)` and returns the newly generated embedding.

> 💡 Workshop note: this implementation uses `Keyword$.VALUE.containing(query)` for simplicity.  
> In production, prefer an exact normalized cache key (for example, lowercase + trimmed input) to avoid partial-match collisions.

### Step 3: Route native hybrid to the cache-aside method
Still in `SearchService`, update `nativeHybridSearch(...)`.

Replace this:
```java
// Make sure to change the method call here to
// invoke the getQueryAsVectorUsingKeyword instead
float[] queryAsVector = getQueryAsVector(query);
```

With this:
```java
float[] queryAsVector = getQueryAsVectorUsingKeyword(query);
```

What this code does:
- Switches native hybrid query embedding creation from always-generate to cache-aside lookup.
- Keeps the rest of native hybrid retrieval logic unchanged.

### Step 4: Build and run
If you are using **Local development**, run:

```bash
./mvnw clean package
./mvnw spring-boot:run
```

If you are using **GitHub Codespaces** or **Dev Containers**, run the same command from the workspace terminal.

## 🧪 Testing Your Implementation
### 1. Keep backend running
Use the backend process started in Step 4 of Setup Instructions.

### 2. API repeated-query test
Run the same semantic query twice:

```bash
curl "http://localhost:8081/search?query=dude%20who%20teaches%20rock&limit=5"
curl "http://localhost:8081/search?query=dude%20who%20teaches%20rock&limit=5"
```

Both responses should include:
- `resultType: "HYBRID"`
- a non-empty `matchedMovies` list

### 3. Redis Insight verification (cache entries)
1. Open Redis Insight (`http://localhost:5540` or forwarded URL)
2. Connect to `redis-database:6379` (Codespaces/Dev Containers) or your local Redis endpoint
3. Browse `keyword:*` keys and open one document
4. Confirm the document contains `value` and `embedding`
5. Confirm `keyword_index` exists

### 4. Validate cache growth with a new prompt
Run a different query:

```bash
curl "http://localhost:8081/search?query=space%20adventure%20crew&limit=5"
```

Then check Redis Insight again and confirm a new `keyword:*` record is created.

### 5. UI validation
1. Open `http://localhost:8080/redis-movies-searcher`
2. Search for `dude who teaches rock`
3. Search for `space adventure crew`
4. Confirm results render with no browser errors

## 🎨 Understanding the Code
### 1. `Keyword` class
- Represents cached prompt embeddings as Redis documents
- Uses `@Vectorize(destination = "embedding")` to generate vectors automatically

### 2. `KeywordRepository`
- Persists new keywords on cache miss
- Enables retrieving previously stored prompt embeddings

### 3. Cache-aside flow in `SearchService`
- Read from keyword cache first
- On miss, compute and store by saving a new `Keyword`
- Return the vector to native hybrid search

## 🐛 Troubleshooting
<details>
<summary>Keyword entries are never created</summary>

Confirm `nativeHybridSearch(...)` calls `getQueryAsVectorUsingKeyword(...)`.
</details>

<details>
<summary>Hybrid results changed unexpectedly</summary>

Verify keyword vectors and movie vectors use the same dimension/model setup.
</details>

<details>
<summary>Repository wiring errors</summary>

Ensure `KeywordRepository` is under scanned packages and `@EnableRedisDocumentRepositories` is active.
</details>

## 🎉 Lab Completion
Congratulations. You now have:
- ✅ Native hybrid search with prompt-embedding cache-aside
- ✅ Reusable keyword embedding store in Redis
- ✅ The complete workshop implementation

## 🏁 Next Steps
You can switch to the full reference branch:
```bash
git checkout workshop-complete
```

Or return to the workshop index:
```bash
git checkout main
```
