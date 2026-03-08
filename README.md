# Lab 3: Implementing Embedding Creation

## 🎯 Learning Objectives

By the end of this lab, you will:
- Implement startup embedding regeneration for existing movie records
- Use Redis scanning and batched persistence to backfill vectors
- Wire startup execution with `CommandLineRunner`
- Validate that `plotEmbedding` is generated for previously imported data
- Enable strong VSS/manual-hybrid behavior for semantic queries

#### 🕗 Estimated Time: 20 minutes

## 🏗️ What You're Building

In this lab, you'll implement the embedding backfill pipeline that prepares existing movies for vector search.

This includes:
- **`MovieRepository` usage** for batch read/write
- **`MovieService.regenerateMissingEmbeddings()`** implementation
- **Startup Hook** in `RedisMoviesSearcher` to trigger regeneration

### Architecture Overview

![search.png](images/search.png)

## 📋 Prerequisites Check

Before starting, ensure you have:

- [ ] Completed Lab 2 successfully
- [ ] Dataset imported into Redis (`movie:*` keys exist)
- [ ] Backend runs locally
- [ ] `movie_index` already created

## 🚀 Setup Instructions

### Step 1: Implement Startup Hook

Open `src/main/java/io/redis/movies/searcher/RedisMoviesSearcher.java`.

In this branch, `loadData(...)` returns `null`. Replace it with a `CommandLineRunner` that calls:

```java
movieService.regenerateMissingEmbeddings();
```

### Step 2: Implement Embedding Regeneration

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

### Step 3: Rebuild and Run

```bash
./mvnw clean package
./mvnw spring-boot:run
```

Watch startup logs for embedding regeneration progress.

## 🧪 Testing Your Implementation

### Check Backfill Progress in Logs

Expect logs similar to:
- "Scanning for movies with missing embeddings..."
- "Found X movie keys in Redis"
- "Regenerated embeddings: ..."

### Verify Vector Field Exists

```bash
redis-cli JSON.GET movie:1 $.plotEmbedding
```

You should see a float array once embedding is created.

### Semantic Query Check

```bash
curl "http://localhost:8081/search?query=dude%20who%20teaches%20rock"
```

Results should improve now that embeddings exist.

## 🎨 Understanding the Code

### 1. `MovieService`
- Handles backfill for already imported records
- Uses batched processing for scalability
- Delegates vector generation via model annotations + persistence

### 2. `CommandLineRunner` in `RedisMoviesSearcher`
- Ensures startup regeneration runs automatically
- Keeps local workshop flow reproducible

### 3. Why this matters
- Data import (Lab 2) loads records quickly
- This lab makes those records vector-searchable without reimport

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

Large batch sizes or first-time full backfill can take time. Review logs and tune batch size if needed.
</details>

<details>
<summary>JSON path returns null for plotEmbedding</summary>

Verify application startup completed and embedding regeneration did not fail in logs.
</details>

## 🎉 Lab Completion

Congratulations. You now have:
- ✅ Startup embedding backfill implemented
- ✅ Existing movies enriched with vector embeddings
- ✅ Better semantic retrieval foundation

## ➡️ Next Steps

Proceed to [Lab 4: Implementing Native Hybrid Search](../../tree/lab-4-starter/README.md)

- Switch to the next branch:

```bash
git checkout lab-4-starter
```
