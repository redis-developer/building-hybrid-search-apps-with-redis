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

### Step 1: Inspect the data import layer
Open `data/import-movies.sh` and review how RIOT will map source fields into Redis JSON documents.

In this branch, the script still contains a TODO placeholder.

### Step 2: Inspect the backend search layer
Review how imported data is consumed by the API:
- `src/main/java/io/redis/movies/searcher/core/domain/Movie.java`
- `src/main/java/io/redis/movies/searcher/core/controller/SearchController.java`
- `src/main/java/io/redis/movies/searcher/core/service/SearchService.java`

### Step 3: Implement the RIOT import command
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

### Step 4: Run the import
If you are using **Local development**, run:

```bash
cd data
chmod +x import-movies.sh
./import-movies.sh
cd ..
```

If you are using **GitHub Codespaces** or **Dev Containers**, run the same command from the workspace terminal.

> 💡 From this point on, every time you change backend code, rebuild and run the backend again before validating behavior.

## 🧪 Testing Your Import
### API verification (FTS)
```bash
curl "http://localhost:8081/search?query=Tom%20Hanks"
```
You should get movie matches from imported data.

### UI verification
1. Open `http://localhost:8080/redis-movies-searcher` (or forwarded URL)
2. Search for a few known terms (for example, `star`, `Tom Hanks`)
3. Confirm there are no UI errors
4. Check backend logs and confirm responses are being returned

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
