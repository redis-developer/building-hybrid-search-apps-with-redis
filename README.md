## 🏆 Congratulations!

You've successfully completed the **Building Hybrid Search Apps with Redis** workshop and built a complete hybrid search application with Java, Spring Boot, and Redis.

This branch contains the full implementation of everything covered throughout the workshop, including data ingestion, embedding generation, native hybrid search, and prompt-embedding cache-aside.

## 🎯 What You've Built

### Complete Hybrid Search System

Your application now implements an end-to-end search experience with:

![search.png](images/search.png)

## 📚 Hybrid Search Techniques Implemented

### 1. **Search Bootstrapping** (Lab 1)
- **Technique**: Index-first setup
- **Implementation**: Redis index creation + application startup flow
- **Benefits**:
  - Consistent schema from day one
  - Reliable search initialization across environments
  - Clear baseline for progressive labs

### 2. **Structured Data Ingestion with RIOT** (Lab 2)
- **Technique**: Bulk JSON document import with transformation
- **Implementation**: `data/import-movies.sh` and RIOT processors
- **Benefits**:
  - Repeatable data loading workflow
  - Consistent key naming and document shape
  - Fast workshop setup for all participants

### 3. **Startup Embedding Regeneration** (Lab 3)
- **Technique**: Batched embedding backfill for existing records
- **Implementation**: `MovieService.regenerateMissingEmbeddings()` + startup hook
- **Benefits**:
  - Existing documents become vector-search ready
  - No need to re-import source data
  - Predictable startup enrichment workflow

### 4. **Native Hybrid Search with Redis** (Lab 4)
- **Technique**: Server-side lexical and semantic score fusion
- **Implementation**: `SearchService.nativeHybridSearch(...)`
- **Benefits**:
  - Better relevance by combining keyword and semantic signals
  - Less app-side orchestration complexity
  - Cleaner retrieval pipeline

### 5. **Prompt Embedding Cache-Aside** (Lab 5)
- **Technique**: Reuse query embeddings for recurring prompts
- **Implementation**: `Keyword` domain + repository-backed lookup/save flow
- **Benefits**:
  - Reduced repeated embedding generation
  - Lower query latency for recurring intents
  - Better resource efficiency under repeated traffic

## 🔧 Technology Stack Mastered

### Core Technologies
- **Java 21**: modern language/runtime features
- **Spring Boot 4**: REST API and app lifecycle orchestration
- **Redis OM Spring**: repository-based modeling and query abstractions

### Data and Search Components
- **Redis Query Engine**: full-text, vector, and hybrid capabilities
- **Redis JSON**: document storage model for movie and keyword entities
- **RIOT**: high-volume JSON import and transformation tooling

### Interface and Tooling
- **NGINX + HTML/JS frontend**: interactive search UI
- **Redis Insight**: data/index inspection and query validation
- **Dev Containers / Codespaces**: reproducible workshop environments

## 🎓 Concepts Learned

1. **Lexical vs Semantic Retrieval**: understanding precision vs intent
2. **Hybrid Ranking Design**: combining text and vector scores
3. **Embedding Lifecycle Management**: when and how to generate vectors
4. **Cache-Aside Strategy**: reducing repeated expensive operations
5. **Redis Data Modeling**: designing JSON documents and indexes for search

## 🚀 Next Steps for Your Journey

### 1. Add Strategy Comparison in the UI
- Toggle between FTS, VSS, manual hybrid, and native hybrid
- Display strategy metadata and ranking behavior side-by-side

### 2. Add Observability for Search Quality
- Include response metadata (`mode`, `timings`, `result counts`)
- Track latency and relevance drift over time

### 3. Add Automated Validation
- Add lab-level tests for each milestone
- Add benchmark scripts for latency and relevance comparisons

### 4. Expand Cache-Aside Capabilities
- Add TTL and invalidation strategies for keyword embeddings
- Add normalization and deduplication for query variants

## 📚 Resources
- [Redis Query Engine](https://redis.io/docs/latest/develop/interact/search-and-query/)
- [Redis Vector Search](https://redis.io/docs/latest/develop/ai/search-and-query/vectors/)
- [Redis OM Spring](https://github.com/redis/redis-om-spring)
- [RIOT Documentation](https://redis.io/docs/latest/develop/tools/riot/)
- [Redis Insight](https://redis.io/insight/)

## 🤝 Contributing
Contributions and improvements are welcome.
- Open an issue for proposals
- Submit pull requests for enhancements and fixes

## 👥 Maintainers
- Ricardo Ferreira — [@riferrei](https://github.com/riferrei)

## 📄 License
This project is licensed under the [MIT License](./LICENSE).
