#!/bin/bash

# Drop existing index if it exists (ignore error if it doesn't exist)
redis-cli FT.DROPINDEX movie_index 2>/dev/null

# Create the movie_index index
redis-cli FT.CREATE movie_index ON JSON PREFIX 1 "movie:" SCHEMA \
  $.title AS title TEXT WEIGHT 1.0 \
  $.year AS year NUMERIC SORTABLE \
  $.plot AS plot TEXT WEIGHT 1.0 \
  $.releaseDate AS releaseDate TAG \
  $.rating AS rating NUMERIC SORTABLE \
  $.actors[*] AS actors TAG \
  $.plotEmbedding AS plotEmbedding VECTOR FLAT 6 TYPE FLOAT32 DIM 384 DISTANCE_METRIC COSINE

echo "Index 'movie_index' created successfully."
