# Lab 5: Caching Prompt Embedding

## 🎯 Learning Objectives

By the end of this lab, you will:
- Implement a cache-aside strategy for prompt embeddings
- Use a `Keyword` document model to persist query embeddings
- Reduce repeated embedding generation for recurring prompts
- Route native hybrid embedding creation through a keyword lookup flow

#### 🕗 Estimated Time: 15 minutes

## 🏗️ What You're Building

In this final lab, you'll add prompt-embedding reuse so repeated queries become faster and cheaper.

This includes:
- **`Keyword` Entity + Repository** for cached embeddings
- **`getQueryAsVectorUsingKeyword(...)`** implementation
- **Native hybrid path update** to use keyword-backed embedding lookup

### Architecture Overview

![search.png](images/search.png)

## 📋 Prerequisites Check

Before starting, ensure you have:

- [ ] Completed Lab 4 successfully
- [ ] Native hybrid search path working
- [ ] Backend and Redis running with imported movie data

## 🚀 Setup Instructions

### Step 1: Review New Domain Components

This branch already includes:
- `Keyword` domain class
- `KeywordRepository`

They are introduced to support embedding cache-aside.

### Step 2: Implement Keyword-based Embedding Lookup

Open `src/main/java/io/redis/movies/searcher/core/service/SearchService.java`.

In this branch, implement:

```java
private float[] getQueryAsVectorUsingKeyword(String query)
```

Expected behavior:
- Search for an existing keyword containing the query
- If present, return stored embedding
- If absent, persist a new `Keyword(query)` and return generated embedding

### Step 3: Switch Native Path to Use Cache-aside Method

Still in `SearchService`, update native search flow from:

```java
float[] queryAsVector = getQueryAsVector(query);
```

to:

```java
float[] queryAsVector = getQueryAsVectorUsingKeyword(query);
```

### Step 4: Build and Run

```bash
./mvnw clean package
./mvnw spring-boot:run
```

## 🧪 Testing Your Implementation

### Repeated Query Test

Run the same semantic query multiple times:

```bash
curl "http://localhost:8081/search?query=dude%20who%20teaches%20rock"
curl "http://localhost:8081/search?query=dude%20who%20teaches%20rock"
```

The second run should avoid recomputing embedding via cache-aside behavior.

### Verify Cached Keywords

Inspect Redis for keyword entries (via Redis Insight or CLI scan).

In Redis Insight:
1. Open `http://localhost:5540`
2. Browse keys for keyword documents
3. Confirm embeddings are stored alongside query text

### API Behavior Check

Response should still return hybrid results while using cached prompt vectors.

## 🎨 Understanding the Code

### 1. Cache-aside Pattern
- Read-through from cache first
- On miss, compute + write-back
- Return newly computed value

### 2. `Keyword` Model
- Stores prompt text and corresponding embedding
- Enables reuse for recurring prompts

### 3. Why this matters
- Reduces embedding calls for repeated searches
- Lowers latency and external model dependency pressure

## 🐛 Troubleshooting

<details>
<summary>Keyword entries are never created</summary>

Confirm `getQueryAsVectorUsingKeyword(...)` is invoked from `nativeHybridSearch(...)`.
</details>

<details>
<summary>Hybrid results changed unexpectedly</summary>

Check that cached embeddings are generated from the same embedder and dimensions as movie plot embeddings.
</details>

<details>
<summary>Repository wiring errors</summary>

Verify `KeywordRepository` is in a package scanned by Spring and `@EnableRedisDocumentRepositories` is active.
</details>

## 🎉 Lab Completion

Congratulations. You now have:
- ✅ Native hybrid search with prompt-embedding cache-aside
- ✅ Reusable keyword embedding store in Redis
- ✅ The complete workshop implementation

## 🏁 Next Steps

You can now switch to the full reference branch:

```bash
git checkout workshop-complete
```

Or return to the workshop index:

```bash
git checkout main
```
