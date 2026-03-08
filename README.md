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

### Step 1: Open the import script
File: `data/import-movies.sh`

In this branch, the script still contains a TODO placeholder.

### Step 2: Implement the RIOT import command
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
### Validate key count
```bash
redis-cli --scan --pattern "movie:*" | wc -l
```
You should see a non-zero count (thousands of records).

### Inspect a sample record
```bash
redis-cli JSON.GET movie:1
```
Confirm fields like `title`, `plot`, `actors`, and `rating` exist.

### Search verification (FTS)
```bash
curl "http://localhost:8081/search?query=Tom%20Hanks"
```
You should get movie matches from imported data.

### Redis Insight check
1. Open `http://localhost:5540`
2. Connect to `redis-database:6379`
3. Browse `movie:*` keys
4. Run: `FT.SEARCH movie_index "@title:star" LIMIT 0 5`

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

Confirm `movie_index` exists and matches JSON paths:
```bash
redis-cli FT.INFO movie_index
```
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
