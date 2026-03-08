# Lab 4: Implementing Native Hybrid Search

## 🎯 Learning Objectives

By the end of this lab, you will:
- Implement native Redis hybrid retrieval in the service layer
- Understand how text and vector signals are fused server-side
- Switch controller execution from manual hybrid to native hybrid
- Compare relevance and latency behavior with the previous approach

#### 🕗 Estimated Time: 20 minutes

## 🏗️ What You're Building

In this lab, you'll move from app-orchestrated manual search composition to native Redis hybrid search.

This includes:
- **`SearchService.nativeHybridSearch(...)`** implementation
- **Controller route switch** to native hybrid
- **Hybrid weighting (`alpha`)** configuration for vector/text balance

### Architecture Overview

![search.png](images/search.png)

## 📋 Prerequisites Check

Before starting, ensure you have:

- [ ] Completed Lab 3 successfully
- [ ] Embeddings generated for movies
- [ ] Backend and Redis running

## 🚀 Setup Instructions

### Step 1: Implement `nativeHybridSearch(...)`

Open `src/main/java/io/redis/movies/searcher/core/service/SearchService.java`.

In this branch, `nativeHybridSearch(...)` returns `null`.

Implement it to:
- Build query embedding
- Execute Redis OM `hybridSearch(...)` with:
  - text query
  - `Movie$.TITLE` text field
  - query vector
  - `Movie$.PLOT_EMBEDDING` vector field
  - alpha weighting (for example `0.7f`)
- Return `ResultType.HYBRID`

### Step 2: Switch Controller to Native Hybrid

Open `src/main/java/io/redis/movies/searcher/core/controller/SearchController.java`.

Change method call from:

```java
searchService.manualHybridSearch(query, limit)
```

to:

```java
searchService.nativeHybridSearch(query, limit)
```

### Step 3: Build and Run

```bash
./mvnw clean package
./mvnw spring-boot:run
```

## 🧪 Testing Your Implementation

### API Test

```bash
curl "http://localhost:8081/search?query=space%20ship"
```

Response should include:
- `resultType: "HYBRID"`
- `matchedMovies` list

### UI Validation

1. Open `http://localhost:8080/redis-movies-searcher`
2. Try lexical queries and semantic queries
3. Observe result consistency and response timing label

### Compare With Manual Behavior

Optionally, temporarily switch back to manual method to compare outputs and latency.

## 🎨 Understanding the Code

### 1. Native Hybrid Search
- Redis combines text relevance and vector similarity internally
- Reduces client-side orchestration and merging logic

### 2. `alpha` Weighting
- Controls balance between vector score and text score
- Higher values bias toward semantic similarity

### 3. Controller Routing
- Single switch point makes strategy transition explicit for the workshop

## 🔍 What's Still Missing?

At this stage, hybrid retrieval is native, but:
- ❌ Prompt embedding calls are not cached
- ❌ Repeated semantic prompts still recompute embeddings each time

## 🐛 Troubleshooting

<details>
<summary>`nativeHybridSearch` returns no matches</summary>

Verify embeddings exist (`plotEmbedding`) and index includes vector field with correct dimension.
</details>

<details>
<summary>Runtime error around vector type conversion</summary>

Ensure the vector format expected by the query API is used consistently (`float[]` or converted bytes where required by method signature).
</details>

<details>
<summary>Result type still shows manual path behavior</summary>

Confirm `SearchController` is calling `nativeHybridSearch(...)`.
</details>

## 🎉 Lab Completion

Congratulations. You now have:
- ✅ Native hybrid search implemented
- ✅ API switched to native retrieval path
- ✅ Cleaner server-side search composition

## ➡️ Next Steps

Proceed to [Lab 5: Caching Prompt Embedding](../../tree/lab-5-starter/README.md)

- Switch to the next branch:

```bash
git checkout lab-5-starter
```
