package com.example.projectuber.Maps;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {
    @GET("maps/api/directions/json")
    Single<Result> getDirection(@Query("mode") String mode,
                                @Query("transit_routing_preferance") String prefreance,
                                @Query("origin") String origin,
                                @Query("destination") String destination,
                                @Query("key") String key);
}
