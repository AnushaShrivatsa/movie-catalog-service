package com.io.java.moviecatalogservice.Services;

import com.io.java.moviecatalogservice.model.CatalogItem;
import com.io.java.moviecatalogservice.model.Movie;
import com.io.java.moviecatalogservice.model.Rating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class MovieInfo {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod="getFallbackCatalogItem",
    threadPoolKey = "movieInfoPool",
       // properties to set new Thread Pool for this API call
            threadPoolProperties = {
            @HystrixProperty(name="coreSize",value="20"),
            @HystrixProperty(name="maxQueueSize",value="10")
         },
            // Setting circuit breaker parameters
            commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="2000"),
            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value="5"),
            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value="50"),
            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value="5000"),
        }

    )
    public CatalogItem getCatalogItem(Rating rating){

        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(),Movie.class);

        return new CatalogItem(movie.getName(),movie.getDescription(), rating.getRating());


    }

    public CatalogItem getFallbackCatalogItem(Rating rating){

        return new CatalogItem("Movie name not found","",rating.getRating());
    }
}
