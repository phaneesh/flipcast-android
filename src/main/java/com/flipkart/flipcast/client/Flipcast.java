/*
 * Copyright 2016 Phaneesh Nagaraja <phaneesh.n@gmail.com>.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.flipkart.flipcast.client;

import android.content.Context;
import android.util.Log;

import com.flipkart.flipcast.config.FlipcastConfig;
import com.flipkart.flipcast.core.DeviceData;
import com.flipkart.flipcast.core.MessageAckResponse;
import com.flipkart.flipcast.core.MessageCountResponse;
import com.flipkart.flipcast.core.MessagesResponse;
import com.flipkart.flipcast.data.CacheUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;
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

    @Builder(builderMethodName = "createDefault", builderClassName = "CreateDefaultBuilder")
    public Flipcast(final FlipcastConfig config) {
        this.config = config;
        this.okHttpClient = new OkHttpClient.Builder().connectionPool(new ConnectionPool(1, 3000, TimeUnit.SECONDS))
                                                      .connectTimeout(config.getConnectionTimeout(), TimeUnit.MILLISECONDS)
                                                      .readTimeout(config.getRequestTimeout(), TimeUnit.MILLISECONDS)
                                                      .writeTimeout(config.getRequestTimeout(), TimeUnit.MILLISECONDS)
                                                      .build();
        setupBaseUrl();
        setupClient(new GsonBuilder().create());
    }

    private void setupBaseUrl() {
        String scheme;
        if (config.isSecured()) {
            scheme = "https";
        } else {
            scheme = "http";
        }
        baseUrl = scheme + "://" + config.getHost() + ":" + config.getPort() + "/" + config.getEndpoint();
    }

    private void setupClient(Gson gson) {
        retrofit = new Retrofit.Builder().client(okHttpClient).baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(gson)).build();
        flipcastHttpClient = retrofit.create(FlipcastHttpClient.class);
    }

    @Builder(builderMethodName = "custom", builderClassName = "CustomBuilder")
    public Flipcast(OkHttpClient okHttpClient, FlipcastConfig config, Gson gson, FlipcastAuthenticationProvider authenticationProvider) {
        this.config = config;
        this.okHttpClient = okHttpClient;
        this.authenticationProvider = authenticationProvider;
        setupBaseUrl();
        setupClient(gson);
    }

    /**
     * Register the device with flipcast service
     *
     * @param request
     * @param callback
     */
    public void register(DeviceData request, final Callback<DeviceData> callback) {
        Call<DeviceData> call = authenticationProvider == null ? flipcastHttpClient.register(request.getConfigName(), request.getDeviceId(), request) :
                flipcastHttpClient
                .register(String.format("%s %s", authenticationProvider.prefix(), authenticationProvider.token()), request.getConfigName(), request
                        .getDeviceId(), request);
        call.enqueue(new Callback<DeviceData>() {
            @Override
            public void onResponse(Call<DeviceData> call, Response<DeviceData> response) {
                Log.i("flipcast", "Device registered successfully: " + response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<DeviceData> call, Throwable t) {
                Log.e("flipcast", "Error registering device: " + t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Unregister the device from flipcast service
     *
     * @param config
     * @param id
     * @param callback
     */
    public void unregister(String config, String id, final Callback<Response> callback) {
        Call<Response> call = authenticationProvider == null ? flipcastHttpClient.unregister(config, id) : flipcastHttpClient.unregister(String.format("%s "
                + "%s", authenticationProvider
                .prefix(), authenticationProvider.token()), config, id);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response<Response> response) {
                Log.i("flipcast", "Device unregistered successfully: " + response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("flipcast", "Error unregistering device: " + t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Count the messages for a device from flipcast service
     *
     * @param config
     * @param id
     * @param callback
     */
    public void count(String config, String id, final Callback<MessageCountResponse> callback) {
        Call<MessageCountResponse> call = authenticationProvider == null ? flipcastHttpClient.count(config, id) : flipcastHttpClient.countAuth(String.format
                ("%s %s", authenticationProvider
                .prefix(), authenticationProvider.token()), config, id);
        call.enqueue(new Callback<MessageCountResponse>() {
            @Override
            public void onResponse(Call<MessageCountResponse> call, Response<MessageCountResponse> response) {
                Log.i("flipcast", "Message count fetched successfully: " + response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MessageCountResponse> call, Throwable t) {
                Log.e("flipcast", "Error fetching message count: " + t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Count the messages by message type for a device from flipcast service
     *
     * @param config
     * @param id
     * @param messageType
     * @param callback
     */
    public void count(String config, String id, String messageType, final Callback<MessageCountResponse> callback) {
        Call<MessageCountResponse> call = authenticationProvider == null ? flipcastHttpClient.count(config, id, messageType) : flipcastHttpClient.countAuth
                (String
                .format("%s %s", authenticationProvider.prefix(), authenticationProvider.token()), config, id, messageType);
        call.enqueue(new Callback<MessageCountResponse>() {
            @Override
            public void onResponse(Call<MessageCountResponse> call, Response<MessageCountResponse> response) {
                Log.i("flipcast", "Message count fetched successfully: " + response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MessageCountResponse> call, Throwable t) {
                Log.e("flipcast", "Error fetching message count: " + t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Count the messages by message type and priority for a device from flipcast service
     *
     * @param config
     * @param id
     * @param messageType
     * @param callback
     */
    public void count(String config, String id, String messageType, String priority, final Callback<MessageCountResponse> callback) {
        Call<MessageCountResponse> call = authenticationProvider == null ? flipcastHttpClient.count(config, id, messageType, priority) : flipcastHttpClient
                .countAuth(String
                .format("%s %s", authenticationProvider.prefix(), authenticationProvider.token()), config, id, messageType, priority);
        call.enqueue(new Callback<MessageCountResponse>() {
            @Override
            public void onResponse(Call<MessageCountResponse> call, Response<MessageCountResponse> response) {
                Log.i("flipcast", "Message count fetched successfully: " + response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MessageCountResponse> call, Throwable t) {
                Log.e("flipcast", "Error fetching message count: " + t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Get all the unread messages for a device from flipcast service
     *
     * @param config
     * @param id
     * @param callback
     */
    public void messages(String config, String id, final Callback<MessagesResponse> callback) {
        Call<MessagesResponse> call = authenticationProvider == null ? flipcastHttpClient.messages(config, id) : flipcastHttpClient.messagesAuth(String
                .format("%s %s", authenticationProvider
                .prefix(), authenticationProvider.token()), config, id);
        call.enqueue(new Callback<MessagesResponse>() {
            @Override
            public void onResponse(Call<MessagesResponse> call, Response<MessagesResponse> response) {
                Log.i("flipcast", "Messages fetched successfully: " + response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MessagesResponse> call, Throwable t) {
                Log.e("flipcast", "Error fetching messages: " + t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Get all the unread messages by message type for a device from flipcast service
     *
     * @param config
     * @param id
     * @param messageType
     * @param callback
     */
    public void messages(String config, String id, String messageType, final Callback<MessagesResponse> callback) {
        Call<MessagesResponse> call = authenticationProvider == null ? flipcastHttpClient.messages(config, id, messageType) : flipcastHttpClient.messagesAuth
                (String
                .format("%s %s", authenticationProvider.prefix(), authenticationProvider.token()), config, id, messageType);
        call.enqueue(new Callback<MessagesResponse>() {
            @Override
            public void onResponse(Call<MessagesResponse> call, Response<MessagesResponse> response) {
                Log.i("flipcast", "Messages fetched successfully: " + response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MessagesResponse> call, Throwable t) {
                Log.e("flipcast", "Error fetching messages: " + t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    /**
     * Get all the unread messages by message type and priority for a device from flipcast service
     *
     * @param config
     * @param id
     * @param messageType
     * @param callback
     */
    public void messages(String config, String id, String messageType, String priority, final Callback<MessagesResponse> callback) {
        Call<MessagesResponse> call = authenticationProvider == null ? flipcastHttpClient.messages(config, id, messageType, priority) : flipcastHttpClient
                .messagesAuth(String
                .format("%s %s", authenticationProvider.prefix(), authenticationProvider.token()), config, id, messageType, priority);
        call.enqueue(new Callback<MessagesResponse>() {
            @Override
            public void onResponse(Call<MessagesResponse> call, Response<MessagesResponse> response) {
                Log.i("flipcast", "Message count fetched successfully: " + response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MessagesResponse> call, Throwable t) {
                Log.e("flipcast", "Error fetching messages: " + t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    public void ack(String config, String id, final Callback<MessageAckResponse> callback) {
        Call<MessageAckResponse> call = authenticationProvider == null ? flipcastHttpClient.ack(config, id) : flipcastHttpClient.ack(String.format("%s %s",
                authenticationProvider
                .prefix(), authenticationProvider.token()), config, id);
        call.enqueue(new Callback<MessageAckResponse>() {
            @Override
            public void onResponse(Call<MessageAckResponse> call, Response<MessageAckResponse> response) {
                Log.i("flipcast", "Message acknowledged successfully: " + response.body());
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MessageAckResponse> call, Throwable t) {
                Log.e("flipcast", "Error fetching messages: " + t.getMessage());
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }


    /**
     * Use this function to set limit the size of InApp messages local cache
     *
     * @param context
     * @param limit
     */
    public void setInAppCacheLimit(final Context context, int limit) {
        CacheUtils cacheUtils = new CacheUtils(context);
        cacheUtils.setInAppCacheLimit(limit);
    }
}
