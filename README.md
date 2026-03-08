# Lab 1: Get the Search Up and Running

## 🎯 Learning Objectives

By the end of this lab, you will:
- Run the full workshop stack locally (frontend, backend, Redis, Redis Insight)
- Understand the baseline search architecture used across the workshop
- Create the RediSearch index manually for the first run
- Validate the API and UI are working even before loading data
- Understand the initial search path (`manualHybridSearch`) and core domain model

#### 🕗 Estimated Time: 20 minutes

## 🏗️ What You're Building

In this foundational lab, you'll deploy the base movie search application and prepare Redis for queries.

This includes:
- **Frontend (NGINX)**: Search UI for movie lookup testing
- **Spring Boot API**: `/search` endpoint for query execution
- **Redis 8 + Query Engine**: Storage and indexing layer
- **Initial Search Strategy**: Manual hybrid method already implemented in the codebase

### Architecture Overview

![search.png](images/search.png)

## 📋 Prerequisites Check

Before starting, ensure you have:

- [ ] Java 21+
- [ ] Maven 3.9+
- [ ] Docker up and running
- [ ] Git configured

If you are using GitHub Codespaces, make sure ports `8080`, `8081`, `6379`, and `5540` are forwarded.

## 🚀 Setup Instructions

### Step 1: Start Infrastructure Services

```bash
docker compose up -d redis-database redis-insight rhs-frontend
```

### Step 2: Create the `movie_index` in Redis

Run the command below once:

```bash
redis-cli FT.CREATE movie_index ON JSON PREFIX 1 "movie:" SCHEMA \
  $.title AS title TEXT WEIGHT 1.0 \
  $.year AS year NUMERIC SORTABLE \
  $.plot AS plot TEXT WEIGHT 1.0 \
  $.releaseDate AS releaseDate TAG \
  $.rating AS rating NUMERIC SORTABLE \
  $.actors[*] AS actors TAG \
  $.plotEmbedding AS plotEmbedding VECTOR FLAT 6 TYPE FLOAT32 DIM 384 DISTANCE_METRIC COSINE
```

### Step 3: Start the Backend

```bash
./mvnw spring-boot:run
```

### Step 4: Open the UI

Use:

- App UI: http://localhost:8080/redis-movies-searcher
- API: http://localhost:8081/search?query=star
- Redis Insight: http://localhost:5540

## 🧪 Testing Your Setup

### API Reachability Test

```bash
curl "http://localhost:8081/search?query=star"
```

You should get a JSON response with `resultType` and `matchedMovies`.

### UI Verification

1. Open `http://localhost:8080/redis-movies-searcher`
2. Type any search query
3. Confirm the UI makes requests and renders response rows (empty or populated)

### Redis Verification

```bash
redis-cli FT.INFO movie_index
```

Confirm the index exists and fields are registered.

## 🎨 Understanding the Code

### 1. `SearchController`
- Exposes `GET /search`
- Delegates query handling to the search service

### 2. `SearchService`
- Uses `manualHybridSearch(...)` in this phase
- Contains FTS + fallback VSS orchestration logic

### 3. `Movie` and `ResultType`
- `Movie` defines the document model and vector-enabled fields
- `ResultType` communicates which strategy produced results

## 🔍 What's Still Missing?

At this stage, the app is operational, but:
- ❌ No movie dataset is loaded
- ❌ No embeddings are generated for existing movie records
- ❌ Native Redis hybrid search path is not active yet
- ❌ Prompt embedding cache-aside is not implemented yet

## 🐛 Troubleshooting

<details>
<summary>"Connection refused" on port 8081</summary>

Ensure `./mvnw spring-boot:run` is running and completed startup.
</details>

<details>
<summary>UI loads but searches fail</summary>

Verify backend is available at `http://localhost:8081/search` and CORS allows your UI origin.
</details>

<details>
<summary>Index creation fails</summary>

If index already exists, run:

```bash
redis-cli FT.DROPINDEX movie_index
```

Then execute the `FT.CREATE` command again.
</details>

## 🎉 Lab Completion

Congratulations. You now have:
- ✅ Running UI, API, Redis, and Redis Insight
- ✅ A valid Redis index for movie search
- ✅ A baseline application ready for data ingestion

## ➡️ Next Steps

Proceed to [Lab 2: Importing Data into Redis](../../tree/lab-2-starter/README.md)

- Switch to the next branch:

```bash
git checkout lab-2-starter
```
