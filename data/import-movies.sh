#!/bin/bash

riot file-import \
    --var counter="new java.lang.Integer(1)" \
    --proc id="#counter++" \
    --proc plot="info.plot" \
    --proc releaseDate="info.release_date" \
    --proc rating="info.rating" \
    --proc actors="info.actors != null ? remove('info').actors : null" \
    movies.json json.set --keyspace movie --key id
