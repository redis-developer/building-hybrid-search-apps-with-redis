# Workshop Complete: Building Hybrid Search Apps with Redis (Java)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 21+](https://img.shields.io/badge/Java-21%2B-blue.svg)](https://www.oracle.com/java/technologies/downloads)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-6DB33F.svg)](https://spring.io/projects/spring-boot)
[![Redis Query Engine](https://img.shields.io/badge/Redis-Query%20Engine-DC382D.svg)](https://redis.io/docs/latest/develop/interact/search-and-query/)
[![Redis OM Spring](https://img.shields.io/badge/Redis%20OM-Spring-DC382D.svg)](https://github.com/redis/redis-om-spring)

## Congratulations

You have completed the **Building Hybrid Search Apps with Redis** workshop.

This branch (`workshop-complete`) contains the full reference implementation for the final workshop outcome, covering end-to-end indexing, data ingestion, embedding generation, native hybrid search, and prompt-embedding caching foundations.

![search.png](images/search.png)

## What You Built

Your final application demonstrates a complete hybrid-search pipeline with Redis:

- Spring Boot API and browser UI for movie discovery
- Redis JSON document storage and RediSearch index configuration
- FTS, VSS, and native hybrid retrieval strategies
- Startup embedding generation for movie plots
- Prompt-embedding cache-aside model using `Keyword`

## Workshop Techniques Implemented

### 1. Search Bootstrapping (Lab 1)
- Technique: Index-first search setup
- Implementation: Redis index creation and app startup flow
- Outcome: Search-ready schema before data volume grows

### 2. Data Ingestion with RIOT (Lab 2)
- Technique: Structured bulk loading into Redis JSON
- Implementation: `data/import-movies.sh` using RIOT processors
- Outcome: Consistent, repeatable dataset initialization

### 3. Embedding Creation Pipeline (Lab 3)
- Technique: Offline/startup vectorization
- Implementation: `MovieService.regenerateMissingEmbeddings()`
- Outcome: Existing data gains vector-search capability without reimport

### 4. Native Hybrid Search (Lab 4)
- Technique: Redis-native text + vector retrieval fusion
- Implementation: `SearchService.nativeHybridSearch(...)`
- Outcome: Better relevance with reduced app-side orchestration

### 5. Prompt Embedding Cache-Aside (Lab 5)
- Technique: Cache-aside for recurring semantic queries
- Implementation: `Keyword` domain model and repository-backed lookup/save flow
- Outcome: Reduced repeated embedding work for recurring prompts

## Architecture Summary

### Backend
- Java 21 + Spring Boot 4
- Redis OM Spring repositories and metamodel queries
- REST endpoint: `GET /search?query=...&limit=...`

### Data and Search
- Redis JSON movie documents (`movie:*`)
- RediSearch index (`movie_index`) with text, numeric, tag, and vector fields
- Vector embeddings configured at dimension `384` (cosine distance)

### Frontend
- Static HTML/JS UI served by NGINX (`/redis-movies-searcher`)
- Live search calls against backend `search` API

## Run the Complete Solution

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

- UI: http://localhost:8080/redis-movies-searcher
- API sample: http://localhost:8081/search?query=dude%20who%20teaches%20rock
- Redis Insight: http://localhost:5540

## Suggested Next Improvements

- Add strategy toggles in the UI to compare FTS, VSS, manual hybrid, and native hybrid side by side
- Add response metadata (`mode`, `timings`, `counts`) for observability
- Add tests for each lab milestone to support workshop validation
- Add benchmark scripts for latency/quality comparisons per strategy

## Resources

- [Redis Query Engine](https://redis.io/docs/latest/develop/interact/search-and-query/)
- [Redis Vector Search](https://redis.io/docs/latest/develop/ai/search-and-query/vectors/)
- [Redis OM Spring](https://github.com/redis/redis-om-spring)
- [RIOT Documentation](https://redis.io/docs/latest/develop/tools/riot/)
- [Redis Insight](https://redis.io/insight/)

## Maintainers

- Ricardo Ferreira — [@riferrei](https://github.com/riferrei)

## License

This project is licensed under the [MIT License](./LICENSE).
