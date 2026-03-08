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

### Step 2: Switch controller to native hybrid
Open `src/main/java/io/redis/movies/searcher/core/controller/SearchController.java`.

Change from:
```java
searchService.manualHybridSearch(query, limit)
```
To:
```java
searchService.nativeHybridSearch(query, limit)
```

### Step 3: Build and run
```bash
./mvnw clean package
./mvnw spring-boot:run
```

## 🧪 Testing Your Implementation
### API test
```bash
curl "http://localhost:8081/search?query=space%20ship"
```
Response should include:
- `resultType: "HYBRID"`
- `matchedMovies` list

### UI validation
1. Open `http://localhost:8080/redis-movies-searcher`
2. Try lexical and semantic queries
3. Observe consistency and response timing label

### Compare with manual behavior
Optionally switch back to manual temporarily and compare output/latency.

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
