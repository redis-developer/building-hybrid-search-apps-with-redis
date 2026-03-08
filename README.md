# Building Hybrid Search Apps with Redis (Java Workshop)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 21+](https://img.shields.io/badge/Java-21%2B-blue.svg)](https://www.oracle.com/java/technologies/downloads)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-6DB33F.svg)](https://spring.io/projects/spring-boot)
[![Redis Query Engine](https://img.shields.io/badge/Redis-Query%20Engine-DC382D.svg)](https://redis.io/docs/latest/develop/interact/search-and-query/)
[![Redis OM Spring](https://img.shields.io/badge/Redis%20OM-Spring-DC382D.svg)](https://github.com/redis/redis-om-spring)

## Overview
Welcome to this hands-on workshop, where you'll learn how to design and build modern search experiences using Redis. You will start with a working Spring Boot application and progressively evolve it from basic full-text search into advanced hybrid search with embeddings, native Redis hybrid search, and embedding cache-aside.

This workshop uses a movie-search domain because it naturally demonstrates the difference between:
- lexical matching (what users type exactly)
- semantic matching (what users mean)
- hybrid ranking (combining both approaches)

![search.png](images/search.png)

### Why Hybrid Search?

Search UX breaks down when you rely on a single strategy:
- FTS alone misses intent when query wording differs from stored text
- VSS alone can surface semantically related but lexically irrelevant results
- ad hoc fallback logic adds latency and complexity

Hybrid search lets you balance precision and semantic relevance in one retrieval flow.

### What you'll build

By the end of this workshop, you'll have built a complete Redis-powered search application featuring:
- Redis JSON document modeling and indexing for movies
- Full-Text Search (FTS) with Redis Query Engine
- Vector Similarity Search (VSS) with embeddings
- Native hybrid search using Redis support
- Startup embedding generation flow
- Cache-aside for recurring prompt embeddings via `Keyword` documents
- A browser UI to compare behavior and latency across strategies

## Prerequisites

### Required knowledge
- Basic Java and Spring Boot familiarity
- Basic understanding of search concepts (keywords, ranking)
- Familiarity with command-line tools
- Basic understanding of Docker and Git

### Required software

#### Option 1: GitHub Codespaces
- GitHub account
- Access to GitHub Codespaces (quota/billing enabled)
- Browser or VS Code with Codespaces support

#### Option 2: Local development
- [Java 21+](https://www.oracle.com/java/technologies/downloads)
- [Maven 3.9+](https://maven.apache.org/install.html)
- [Docker](https://docs.docker.com/get-docker/)
- [Git](https://git-scm.com/install/)
- [RIOT](https://redis.io/docs/latest/develop/tools/riot/) (for dataset import labs)
- Java IDE

### Required accounts

No paid account is required for the core workshop flow. Everything can run locally with Docker.

## Workshop Structure

This workshop has an estimated duration of 90 minutes and is organized into 5 progressive labs.

| Lab | Topic | Duration | Branch |
|:----|:------|:---------|:-------|
| 1 | [Get the search up and running](../../tree/lab-1-starter/README.md) | 20 mins | `lab-1-starter` |
| 2 | [Importing data into Redis](../../tree/lab-2-starter/README.md) | 15 mins | `lab-2-starter` |
| 3 | [Implementing embedding creation](../../tree/lab-3-starter/README.md) | 20 mins | `lab-3-starter` |
| 4 | [Implementing native hybrid search](../../tree/lab-4-starter/README.md) | 20 mins | `lab-4-starter` |
| 5 | [Caching prompt embedding](../../tree/lab-5-starter/README.md) | 15 mins | `lab-5-starter` |

Each lab also has a corresponding `lab-X-solution` branch with the completed code for reference. You can compare your implementation using:

```bash
git diff lab-X-solution
```

## Getting Started

### Step 1: Choose your setup option

Pick one of the setup options from the Prerequisites section:
- GitHub Codespaces
- Local development

### Step 2: Start your workspace

If you are using **GitHub Codespaces**:
- Create a new codespace for this repository.
- Forward ports `8080`, `8081`, `6379`, and `5540`.

If you are using **Local development**:
- Clone the repository:

  ```bash
  git clone https://github.com/redis-developer/building-hybrid-search-apps-with-redis.git
  ```

- Verify tools:

  ```bash
  java -version
  mvn -version
  docker --version
  git --version
  riot --version
  ```

- Start infrastructure services:

  ```bash
  docker compose up -d redis-database redis-insight rhs-frontend
  ```

- Run backend:

  ```bash
  ./mvnw spring-boot:run
  ```

Access points:
- App UI: http://localhost:8080/redis-movies-searcher
- Backend API: http://localhost:8081/search?query=star
- Redis Insight: http://localhost:5540

### Step 3: Begin your first lab

Switch to the starter branch for Lab 1:

```bash
git checkout lab-1-starter
```

Then follow the lab README instructions.

## Resources
- [Redis Query Engine](https://redis.io/docs/latest/develop/interact/search-and-query/)
- [Redis Vector Search](https://redis.io/docs/latest/develop/ai/search-and-query/vectors/)
- [Redis OM Spring](https://github.com/redis/redis-om-spring)
- [RIOT Documentation](https://redis.io/docs/latest/develop/tools/riot/)
- [Redis Insight](https://redis.io/insight/)

## Contributing
Contributions are welcome. Please open an issue to discuss major changes before submitting a PR.

## Maintainers
- Ricardo Ferreira — [@riferrei](https://github.com/riferrei)

## License
This project is licensed under the [MIT License](./LICENSE).
