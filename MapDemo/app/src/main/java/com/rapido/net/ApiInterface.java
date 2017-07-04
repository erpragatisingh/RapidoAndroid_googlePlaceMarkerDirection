package com.rapido.net;

/**
 * Created by pragati.singh on 7/4/2017.
 */
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {
    @GET
    Call<JsonObject> getLocationData(@Url String subURL);
}
