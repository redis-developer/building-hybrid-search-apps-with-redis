package io.redis.movies.searcher.core.domain;

import com.redis.om.spring.annotations.*;
import com.redis.om.spring.indexing.DistanceMetric;
import com.redis.om.spring.indexing.VectorType;
import org.springframework.data.annotation.Id;
import redis.clients.jedis.search.schemafields.VectorField;

@Document(value = "keyword")
@IndexingOptions(indexName = "keyword_index")
public class Keyword {

    @Id
    private String id;

    @Searchable
    @Vectorize(
            destination = "embedding",
            embeddingType = EmbeddingType.SENTENCE
    )
    private String value;

    @Indexed(
            schemaFieldType = SchemaFieldType.VECTOR,
            algorithm = VectorField.VectorAlgorithm.FLAT,
            type = VectorType.FLOAT32,
            dimension = 384,
            distanceMetric = DistanceMetric.COSINE,
            initialCapacity = 10
    )
    private float[] embedding;

    public Keyword() {}

    public Keyword(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}
