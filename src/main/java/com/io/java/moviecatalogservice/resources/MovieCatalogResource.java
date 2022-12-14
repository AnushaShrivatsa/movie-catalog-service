package com.io.java.moviecatalogservice.resources;

import com.io.java.moviecatalogservice.Services.MovieInfo;
import com.io.java.moviecatalogservice.Services.UserRatingInfo;
import com.io.java.moviecatalogservice.model.CatalogItem;
import com.io.java.moviecatalogservice.model.Movie;
import com.io.java.moviecatalogservice.model.Rating;
import com.io.java.moviecatalogservice.model.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/catalog") //hey spring boot when somebody call this url /catalog load up this resource class.
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    MovieInfo movieInfo;

    @Autowired
    UserRatingInfo userRatingInfo;

    // To tell spring boot to treat this as an API accesible at /catalog/{userId} execute this method.
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        UserRating ratings = userRatingInfo.getUserRating(userId);

        System.out.println("******ratings : "+ratings);

        return ratings.getUserRating().stream().map(rating -> movieInfo.getCatalogItem(rating))
                .collect(Collectors.toList());


    }


}
 /* Movie movie = webClientBuilder.build()
                            .get()
                            .uri("http://localhost:8082/movies/"+rating.getMovieId())
                            .retrieve()
                            .bodyToMono(Movie.class)
                            .block();*/