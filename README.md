# Lab 2: Importing Data into Redis

## 🎯 Learning Objectives
By the end of this lab, you will:
- Understand the movie dataset shape used in this workshop
- Implement a RIOT import script for Redis JSON documents
- Load data into Redis under the `movie:*` keyspace
- Validate imported records and index readiness in Redis Insight
- Run initial FTS searches against real data

#### 🕗 Estimated Time: 10 minutes

## 🏗️ What You're Building
In this lab, you'll make the data ingestion pipeline operational.

This includes:
- **RIOT Import Script**: `data/import-movies.sh`
- **JSON Mapping Rules**: transform source payload into the `Movie` model shape
- **Searchable Dataset**: records queryable via API and Redis Insight

### Architecture Overview
![search.png](images/search.png)

## 📋 Prerequisites Check
Before starting, confirm the checklist for the setup option you selected:

### Option 1: GitHub Codespaces
- [ ] Codespace is running for this repository
- [ ] Ports `8080`, `8081`, `5540`, and `6379` are forwarded
- [ ] Lab 1 completed in this Codespace environment

### Option 2: Dev Containers locally
- [ ] Project opened in your IDE Dev Container
- [ ] Containers are healthy and ports are available/forwarded
- [ ] Lab 1 completed in this Dev Container environment

### Option 3: Local development
- [ ] Local Docker environment is running
- [ ] Lab 1 completed locally
- [ ] Redis, frontend, and backend services are up

### Lab-specific requirements
- [ ] `movie_index` already created in Redis
- [ ] RIOT available in your shell (`riot --version`)

## 🚀 Setup Instructions
> 💡 For GitHub Codespaces and Dev Containers, use forwarded URLs from the Ports panel for browser access.  
> From workspace terminals, prefer service DNS names (for example, `redis-database`) when connecting to Redis.

### Step 1: Inspect `movies.json`
Open `data/movies.json` and review the shape of incoming data.

Pay attention to nested fields under `info`, for example:
- `info.plot`
- `info.release_date`
- `info.rating`
- `info.actors`

This is important because the import command maps nested fields into the target Redis JSON structure.

### Step 2: Implement the import script with RIOT
Open `data/import-movies.sh`.

In this branch, the script still contains a TODO placeholder. Replace it with:
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

What this command does:
- `file-import`: reads records from `movies.json`
- `--var counter` + `--proc id`: creates incremental IDs for keys
- `--proc plot/releaseDate/rating/actors`: extracts and maps nested `info.*` fields
- `json.set --keyspace movie --key id`: stores each record as Redis JSON under `movie:<id>`

During storage, each movie becomes a JSON document in Redis, ready for indexing/search using the structure expected by the app.

### Step 3: Run the import
If you are using **Local development**, run:

```bash
cd data
chmod +x import-movies.sh
./import-movies.sh
cd ..
```

If you are using **GitHub Codespaces** or **Dev Containers**, run the same command from the workspace terminal.

## 🧪 Testing Your Import
### API verification (FTS on title)
```bash
curl "http://localhost:8081/search?query=star"
```
You should get movie matches based on title terms from imported data.

### Redis Insight verification
1. Open Redis Insight (`http://localhost:5540` or forwarded URL)
2. Connect to `redis-database:6379`
3. Browse `movie:*` and confirm there are many imported records
4. Open one sample document and validate fields (`title`, `plot`, `actors`, `rating`)
5. Verify `movie_index` exists in indexes view
6. Run: `FT.SEARCH movie_index "@title:star" LIMIT 0 5` from Redis Insight and confirm results

## 🎨 Understanding the Code
### 1. `import-movies.sh`
- Uses RIOT to transform and insert JSON documents
- Normalizes fields into domain/API expectations

### 2. `create-index.sh`
- Defines schema used by the search layer
- Includes vector field configuration for upcoming labs

### 3. Search path at this stage
- API still uses the manual path
- Most useful behavior now is lexical FTS because embeddings are not backfilled yet

## 🔍 What's Still Missing?
At this stage, the app has data, but:
- ❌ Existing records still need embedding backfill
- ❌ Native hybrid path is not active
- ❌ Prompt embedding cache-aside is not active

## 🐛 Troubleshooting
<details>
<summary>`riot: command not found`</summary>

Install RIOT and verify:
```bash
riot --version
```
</details>

<details>
<summary>Imported data is not searchable</summary>

Confirm in Redis Insight that:
- `movie_index` exists
- `movie:*` keys were created
- document fields match index paths
</details>

<details>
<summary>Script runs but inserts zero documents</summary>

Ensure you execute the script from the `data/` directory so `movies.json` resolves correctly.
</details>

## 🎉 Lab Completion
Congratulations. You now have:
- ✅ RIOT import implemented
- ✅ Movie dataset loaded in Redis JSON
- ✅ Search endpoint returning results from real data

## ➡️ Next Steps
Proceed to [Lab 3: Implementing Embedding Creation](../../tree/lab-3-starter/README.md)

```bash
git checkout lab-3-starter
```
