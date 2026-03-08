# Lab 2: Importing Data into Redis

## 🎯 Learning Objectives

By the end of this lab, you will:
- Understand the movie dataset shape used by this workshop
- Implement a RIOT import script for Redis JSON documents
- Load data into Redis under the `movie:*` keyspace
- Validate imported records and index readiness in Redis Insight
- Run initial FTS searches against real data

#### 🕗 Estimated Time: 15 minutes

## 🏗️ What You're Building

In this lab, you'll make the dataset ingestion pipeline operational.

This includes:
- **RIOT Import Script**: `data/import-movies.sh`
- **JSON Mapping Rules**: Transform source payload to the `Movie` model shape
- **Searchable Dataset**: Documents that can be queried through the API and Redis Insight

### Architecture Overview

![search.png](images/search.png)

## 📋 Prerequisites Check

Before starting, ensure you have:

- [ ] Completed Lab 1 successfully
- [ ] `movie_index` already created in Redis
- [ ] Redis and backend running
- [ ] RIOT installed and available in your shell

## 🚀 Setup Instructions

### Step 1: Open the Import Script

File:

- `data/import-movies.sh`

In this branch, the script still contains a TODO placeholder.

### Step 2: Implement the RIOT Import Command

Replace the TODO with:

```bash
riot file-import \
    --var counter="new java.lang.Integer(1)" \
    --proc id="#counter++" \
    --proc plot="info.plot" \
    --proc releaseDate="info.release_date" \
    --proc rating="info.rating" \
    --proc actors="info.actors != null ? remove('info').actors : null" \
    movies.json json.set --keyspace movie --key id
```

### Step 3: Run the Import

```bash
cd data
chmod +x import-movies.sh
./import-movies.sh
cd ..
```

## 🧪 Testing Your Import

### Validate Key Count

```bash
redis-cli --scan --pattern "movie:*" | wc -l
```

You should see a non-zero count (thousands of records).

### Inspect a Sample Record

```bash
redis-cli JSON.GET movie:1
```

Confirm fields like `title`, `plot`, `actors`, and `rating` exist.

### Search Verification (FTS)

```bash
curl "http://localhost:8081/search?query=Tom%20Hanks"
```

You should get movie matches from the imported data.

### Redis Insight Check

1. Open `http://localhost:5540`
2. Connect to `redis-database:6379`
3. Browse `movie:*` keys
4. Run a query with `FT.SEARCH movie_index "@title:star" LIMIT 0 5`

## 🎨 Understanding the Code

### 1. `import-movies.sh`
- Uses RIOT to transform and insert JSON documents
- Normalizes source fields into API/domain expectations

### 2. `create-index.sh`
- Defines schema expected by the API search layer
- Includes vector field configuration for upcoming labs

### 3. Search Path at this Stage
- API uses manual search path already present
- Most useful behavior now is lexical FTS because embeddings are not regenerated yet

## 🔍 What's Still Missing?

At this stage, the app has data, but:
- ❌ Existing records still need embedding backfill for VSS/hybrid quality
- ❌ Native hybrid path is not active
- ❌ Prompt embedding cache-aside is not active

## 🐛 Troubleshooting

<details>
<summary>"riot: command not found"</summary>

Install RIOT and verify with:

```bash
riot --version
```
</details>

<details>
<summary>Imported data is not searchable</summary>

Confirm `movie_index` exists and matches JSON paths:

```bash
redis-cli FT.INFO movie_index
```
</details>

<details>
<summary>Script runs but inserts zero documents</summary>

Ensure you execute from `data/` directory so `movies.json` resolves correctly.
</details>

## 🎉 Lab Completion

Congratulations. You now have:
- ✅ RIOT import implemented
- ✅ Movie dataset loaded in Redis JSON
- ✅ Search endpoint returning results from real data

## ➡️ Next Steps

Proceed to [Lab 3: Implementing Embedding Creation](../../tree/lab-3-starter/README.md)

- Switch to the next branch:

```bash
git checkout lab-3-starter
```
