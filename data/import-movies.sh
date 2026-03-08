#!/bin/bash

# The URI below assumes local Redis. For Codespaces/Dev Containers,
# you must use the following value: redis://redis-database:6379
REDIS_URI="${REDIS_URI:-redis://localhost:6379}"

riot file-import \
    --uri "$REDIS_URI" \
    --var counter="new java.lang.Integer(1)" \
    --proc id="#counter++" \
    --proc plot="info.plot" \
    --proc releaseDate="info.release_date" \
    --proc rating="info.rating" \
    --proc actors="info.actors != null ? remove('info').actors : null" \
    movies.json json.set --keyspace movie --key id
