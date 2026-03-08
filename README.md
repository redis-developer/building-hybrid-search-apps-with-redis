# Workshop Complete: Building Hybrid Search Apps with Redis (Java)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 21+](https://img.shields.io/badge/Java-21%2B-blue.svg)](https://www.oracle.com/java/technologies/downloads)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-6DB33F.svg)](https://spring.io/projects/spring-boot)
[![Redis Query Engine](https://img.shields.io/badge/Redis-Query%20Engine-DC382D.svg)](https://redis.io/docs/latest/develop/interact/search-and-query/)
[![Redis OM Spring](https://img.shields.io/badge/Redis%20OM-Spring-DC382D.svg)](https://github.com/redis/redis-om-spring)

## 🏆 Congratulations!
You completed the **Building Hybrid Search Apps with Redis** workshop.

This branch (`workshop-complete`) contains the full reference implementation with all labs applied end to end.

![search.png](images/search.png)

## 🎯 What You Built
Your final application includes:
- Spring Boot API and browser UI for search
- Redis JSON document storage and RediSearch indexing
- FTS, VSS, manual hybrid, and native hybrid strategies
- Startup embedding regeneration for existing movie records
- Prompt-embedding cache-aside using `Keyword`

## 🧠 Techniques Implemented
### 1. Search Bootstrapping (Lab 1)
- **Technique**: index-first setup
- **Implementation**: Redis index creation + baseline app startup
- **Outcome**: search-ready schema before ingestion

### 2. Data Ingestion with RIOT (Lab 2)
- **Technique**: structured bulk JSON import
- **Implementation**: `data/import-movies.sh`
- **Outcome**: repeatable dataset loading flow

### 3. Embedding Backfill Pipeline (Lab 3)
- **Technique**: startup vector generation for existing records
- **Implementation**: `MovieService.regenerateMissingEmbeddings()`
- **Outcome**: vector search without re-importing data

### 4. Native Hybrid Search (Lab 4)
- **Technique**: Redis-native lexical + semantic fusion
- **Implementation**: `SearchService.nativeHybridSearch(...)`
- **Outcome**: improved relevance with less app-side orchestration

### 5. Prompt Embedding Cache-Aside (Lab 5)
- **Technique**: cache-aside for recurring semantic prompts
- **Implementation**: `Keyword` model + repository lookup/save
- **Outcome**: fewer repeated embedding computations

## 🧩 Architecture Summary
### Backend
- Java 21 + Spring Boot 4
- Redis OM Spring repositories and metamodel queries
- API endpoint: `GET /search?query=...&limit=...`

### Data and Search
- Redis JSON movie documents (`movie:*`)
- RediSearch index (`movie_index`) with text, numeric, tag, and vector fields
- Embedding dimension `384` using cosine distance

### Frontend
- Static HTML/JS app served by NGINX (`/redis-movies-searcher`)
- Live calls to backend search API

## 🚀 Run the Complete Solution
### 1. Start infrastructure
```bash
docker compose up -d redis-database redis-insight rhs-frontend
```

### 2. Create index and import data
```bash
cd data
./create-index.sh
./import-movies.sh
cd ..
```

### 3. Run backend
```bash
./mvnw spring-boot:run
```

### 4. Access the app
- UI: `http://localhost:8080/redis-movies-searcher`
- API sample: `http://localhost:8081/search?query=dude%20who%20teaches%20rock`
- Redis Insight: `http://localhost:5540`

## 🔭 Suggested Next Improvements
- Add UI toggles to compare FTS/VSS/manual/native hybrid side by side
- Include response metadata (`mode`, `timings`, `counts`) for observability
- Add milestone tests per lab for workshop validation
- Add benchmark scripts for latency and relevance comparisons

## 📚 Resources
- [Redis Query Engine](https://redis.io/docs/latest/develop/interact/search-and-query/)
- [Redis Vector Search](https://redis.io/docs/latest/develop/ai/search-and-query/vectors/)
- [Redis OM Spring](https://github.com/redis/redis-om-spring)
- [RIOT Documentation](https://redis.io/docs/latest/develop/tools/riot/)
- [Redis Insight](https://redis.io/insight/)

## 👥 Maintainers
- Ricardo Ferreira — [@riferrei](https://github.com/riferrei)

## 📄 License
This project is licensed under the [MIT License](./LICENSE).
