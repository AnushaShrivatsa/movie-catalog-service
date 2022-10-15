package com.io.java.moviecatalogservice.Services;

import com.io.java.moviecatalogservice.model.Rating;
import com.io.java.moviecatalogservice.model.UserRating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserRatingInfo {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod="getFallbackUserrating",
            threadPoolKey = "movieInfoPool",

            threadPoolProperties = {
                    @HystrixProperty(name="coreSize",value="20"), // max 20 concurrent threads can be in this thread pool.
                    @HystrixProperty(name="maxQueueSize",value="10") // Max 10 threads can wait in queue when there is no space in thread pool.
            },
            //Setting circuit breaker parameters (Once threads are full in queue and exceeds the limit  fallback method is called).
            commandProperties = {
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="2000"),
                    @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value="5"),
                    @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value="50"),
                    @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value="5000"),
            }

            )
    public UserRating getUserRating(@PathVariable("userId") String userId){

        return restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/"+userId,UserRating.class);


    }

    public UserRating getFallbackUserrating(@PathVariable("userId") String userId){

        UserRating userRating = new UserRating();
        userRating.setUserId(userId);

        userRating.setUserRating(Arrays.asList(
                new Rating("0",0)
                )
        );

        return userRating;
    }
}
