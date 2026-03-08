package io.redis.movies.searcher.core.domain;

import com.redis.om.spring.annotations.*;
import com.redis.om.spring.indexing.DistanceMetric;
import com.redis.om.spring.indexing.VectorType;
import org.springframework.data.annotation.Id;
import redis.clients.jedis.search.schemafields.VectorField;

import java.util.List;

@Document("movie")
@IndexingOptions(indexName = "movie_index")
public class Movie {

    @Id
    private int id;

    @Searchable
    private String title;

    @Indexed(sortable = true)
    private int year;

    @Vectorize(
            destination = "plotEmbedding",
            embeddingType = EmbeddingType.SENTENCE
    )
    private String plot;

    @Indexed(
            schemaFieldType = SchemaFieldType.VECTOR,
            algorithm = VectorField.VectorAlgorithm.FLAT,
            type = VectorType.FLOAT32,
            dimension = 384,
            distanceMetric = DistanceMetric.COSINE,
            initialCapacity = 10
    )
    private float[] plotEmbedding;

    @Indexed
    private String releaseDate;

    @Indexed(sortable = true)
    private double rating;

    @Indexed
    private List<String> actors;

    public Movie() {}

    public Movie(int id, String title, int year, String plot,
                 String releaseDate, double rating, List<String> actors) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.plot = plot;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.actors = actors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public float[] getPlotEmbedding() {
        return plotEmbedding;
    }

    public void setPlotEmbedding(float[] plotEmbedding) {
        this.plotEmbedding = plotEmbedding;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

}
