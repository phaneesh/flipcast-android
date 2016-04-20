package com.flipkart.flipcast.client;

import com.flipkart.flipcast.core.DeviceData;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author phaneesh
 */
public interface FlipcastHttpClient {

    @POST("flipcast/device/{config}/deviceId/{id}")
    Call<Response<DeviceData>> register(@Path("config") String config, @Path("id") String id, @Body DeviceData registerRequest);

    @POST("flipcast/device/{config}/deviceId/{id}")
    Call<Response<DeviceData>> register(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id, @Body DeviceData registerRequest);

    @DELETE("flipcast/device/{config}/deviceId/{id}")
    Call<Response> unregister(@Path("config") String config, @Path("id") String id);

    @DELETE("flipcast/device/{config}/deviceId/{id}")
    Call<Response> unregister(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id);
}
