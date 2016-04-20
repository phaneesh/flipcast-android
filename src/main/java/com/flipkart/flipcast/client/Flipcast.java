package com.flipkart.flipcast.client;

import android.util.Log;

import com.flipkart.flipcast.config.FlipcastConfig;
import com.flipkart.flipcast.core.DeviceData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import lombok.Builder;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author phaneesh
 */
public class Flipcast {

    private OkHttpClient okHttpClient;

    private FlipcastConfig config;

    private String baseUrl;

    private Retrofit retrofit;

    private FlipcastHttpClient flipcastHttpClient;

    private FlipcastAuthenticationProvider authenticationProvider = null;

    @Builder(builderMethodName = "defaultClient")
    public Flipcast(final FlipcastConfig config) {
        this.config = config;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectionPool(
                new ConnectionPool(1, 3000, TimeUnit.SECONDS)
            )
                .connectTimeout(config.getConnectionTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getRequestTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(config.getRequestTimeout(), TimeUnit.MILLISECONDS)
            .build();
        setupBaseUrl();
        setupClient(new GsonBuilder().create());
    }

    private void setupBaseUrl() {
        baseUrl = String.format("%s://%s:%d/%s", config.isSecured() ? "https" : "http", config.getHost(), config.getPort(), config.getEndpoint());
    }

    private void setupClient(Gson gson) {
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        flipcastHttpClient = retrofit.create(FlipcastHttpClient.class);
    }

    @Builder(builderMethodName = "from")
    public Flipcast(OkHttpClient okHttpClient, FlipcastConfig config, Gson gson, FlipcastAuthenticationProvider authenticationProvider) {
        this.config = config;
        this.okHttpClient = okHttpClient;
        this.authenticationProvider = authenticationProvider;
        setupBaseUrl();
        setupClient(gson);
    }

    /**
     * Register the device with flipcast service
     * @param request
     * @param callback
     */
    public void register(DeviceData request, final Callback<Response<DeviceData>> callback) {
        Call<Response<DeviceData>> call = authenticationProvider ==  null ?
                flipcastHttpClient.register(request.getConfigName(), request.getDeviceId(), request) :
                flipcastHttpClient.register(String.format("%s %s", authenticationProvider.prefix(),
                        authenticationProvider. token()), request.getConfigName(),
                        request.getDeviceId(), request);
        call.enqueue(new Callback<Response<DeviceData>>() {
            @Override
            public void onResponse(Call<Response<DeviceData>> call, Response<Response<DeviceData>> response) {
                Log.i("flipcast", "Device registered successfully: " +response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Response<DeviceData>> call, Throwable t) {
                Log.e("flipcast", "Error registering device: " +t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Unregister the device from flipcast service
     * @param config
     * @param id
     * @param callback
     */
    public void unregister(String config, String id, final Callback<Response> callback) {
        Call<Response> call = authenticationProvider ==  null ? flipcastHttpClient.unregister(config, id) : flipcastHttpClient.unregister(String.format("%s %s", authenticationProvider.prefix(),
                authenticationProvider. token()), config, id);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response<Response> response) {
                Log.i("flipcast", "Device unregistered successfully: " +response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("flipcast", "Error unregistering device: " +t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }
}
