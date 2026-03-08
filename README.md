# Lab 3: Implementing Embedding Creation

## 🎯 Learning Objectives
By the end of this lab, you will:
- Implement startup embedding regeneration for existing movie records
- Use Redis scanning and batched persistence to backfill vectors
- Wire startup execution with `CommandLineRunner`
- Validate that `plotEmbedding` is generated for previously imported data
- Improve semantic retrieval behavior for manual hybrid search

#### 🕗 Estimated Time: 20 minutes

## 🏗️ What You're Building
In this lab, you'll implement the embedding backfill pipeline that prepares existing movies for vector search.

This includes:
- **`MovieRepository` usage** for batch read/write
- **`MovieService.regenerateMissingEmbeddings()`** implementation
- **Startup hook** in `RedisMoviesSearcher` to trigger regeneration

### Architecture Overview
![search.png](images/search.png)

## 📋 Prerequisites Check
Before starting, ensure you have:
- [ ] Completed Lab 2 successfully
- [ ] Dataset imported into Redis (`movie:*` keys exist)
- [ ] Backend runs locally
- [ ] `movie_index` already created

## 🚀 Setup Instructions
### Step 1: Implement startup hook
Open `src/main/java/io/redis/movies/searcher/RedisMoviesSearcher.java`.

In this branch, `loadData(...)` returns `null`. Replace it with a `CommandLineRunner` that calls:
```java
movieService.regenerateMissingEmbeddings();
```

### Step 2: Implement embedding regeneration
Open `src/main/java/io/redis/movies/searcher/core/service/MovieService.java`.

In this branch, `regenerateMissingEmbeddings()` is TODO.

Implement logic to:
- Scan `movie:*` keys from Redis
- Resolve integer IDs
- Load documents in batches
- Keep only movies that:
  - have non-blank `plot`
  - have missing `plotEmbedding`
- Save batches via `movieRepository.saveAll(...)`
- Log progress and completion stats

### Step 3: Rebuild and run
```bash
./mvnw clean package
./mvnw spring-boot:run
```

Watch startup logs for embedding regeneration progress.

## 🧪 Testing Your Implementation
### Check backfill progress in logs
Expect logs similar to:
- `Scanning for movies with missing embeddings...`
- `Found X movie keys in Redis`
- `Regenerated embeddings: ...`

### Verify vector field exists
```bash
redis-cli JSON.GET movie:1 $.plotEmbedding
```
You should see a float array once embedding is created.

### Semantic query check
```bash
curl "http://localhost:8081/search?query=dude%20who%20teaches%20rock"
```
Results should improve once embeddings exist.

## 🎨 Understanding the Code
### 1. `MovieService`
- Handles backfill for already imported records
- Uses batched processing for scalability
- Persists updates to trigger embedding generation

### 2. `CommandLineRunner` in `RedisMoviesSearcher`
- Ensures regeneration runs automatically on startup
- Keeps workshop flow reproducible

### 3. Why this matters
- Lab 2 imported data quickly
- This lab makes that same data vector-searchable without reimport

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
