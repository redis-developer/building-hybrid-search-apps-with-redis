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
### Step 1: Review new domain components
This branch already includes:
- `Keyword` domain class
- `KeywordRepository`

These support embedding cache-aside behavior.

### Step 2: Implement keyword-based embedding lookup
Open `src/main/java/io/redis/movies/searcher/core/service/SearchService.java`.

In this branch, implement:
```java
private float[] getQueryAsVectorUsingKeyword(String query)
```

Expected behavior:
- Search for an existing keyword containing the query
- If present, return stored embedding
- If absent, persist `new Keyword(query)` and return generated embedding

### Step 3: Switch native path to use cache-aside
Still in `SearchService`, update from:
```java
float[] queryAsVector = getQueryAsVector(query);
```
To:
```java
float[] queryAsVector = getQueryAsVectorUsingKeyword(query);
```

### Step 4: Build and run
```bash
./mvnw clean package
./mvnw spring-boot:run
```

## 🧪 Testing Your Implementation
### Repeated query test
Run the same query multiple times:
```bash
curl "http://localhost:8081/search?query=dude%20who%20teaches%20rock"
curl "http://localhost:8081/search?query=dude%20who%20teaches%20rock"
```
The second run should avoid recomputing the embedding.

### Verify cached keywords
Inspect Redis for keyword documents.

In Redis Insight:
1. Open `http://localhost:5540`
2. Browse keys for keyword records
3. Confirm embeddings are stored with query text

### API behavior check
Response should still return hybrid results while using cached prompt vectors.

## 🎨 Understanding the Code
### 1. Cache-aside pattern
- Read cache first
- On miss, compute and write back
- Return generated value

### 2. `Keyword` model
- Stores prompt text and associated embedding
- Enables reuse for recurring prompts

### 3. Why this matters
- Reduces embedding calls for repeated searches
- Improves latency and lowers external dependency pressure

## 🐛 Troubleshooting
<details>
<summary>Keyword entries are never created</summary>

Confirm `getQueryAsVectorUsingKeyword(...)` is invoked from `nativeHybridSearch(...)`.
</details>

<details>
<summary>Hybrid results changed unexpectedly</summary>

Verify cached vectors use the same embedding model and dimensions as movie vectors.
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
