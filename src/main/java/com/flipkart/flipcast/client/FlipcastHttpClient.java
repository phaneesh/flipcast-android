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

import com.flipkart.flipcast.core.DeviceData;
import com.flipkart.flipcast.core.MessageAckResponse;
import com.flipkart.flipcast.core.MessageCountResponse;
import com.flipkart.flipcast.core.MessagesResponse;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author phaneesh
 */
public interface FlipcastHttpClient {

    @POST("flipcast/device/{config}/deviceId/{id}")
    Call<DeviceData> register(@Path("config") String config, @Path("id") String id, @Body DeviceData registerRequest);

    @POST("flipcast/device/{config}/deviceId/{id}")
    Call<DeviceData> register(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id, @Body DeviceData registerRequest);

    @DELETE("flipcast/device/{config}/deviceId/{id}")
    Call<Response> unregister(@Path("config") String config, @Path("id") String id);

    @DELETE("flipcast/device/{config}/deviceId/{id}")
    Call<Response> unregister(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id);

    @GET("flipcast/inapp/message/count/{config}/{id}")
    Call<MessageCountResponse> count(@Path("config") String config, @Path("id") String id);

    @GET("flipcast/inapp/message/count/{config}/{id}")
    Call<MessageCountResponse> countAuth(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id);

    @GET("flipcast/inapp/message/count/{config}/{id}/{messageType}")
    Call<MessageCountResponse> count(@Path("config") String config, @Path("id") String id, @Path("messageType") String messageType);

    @GET("flipcast/inapp/message/count/{config}/{id}/{messageType}")
    Call<MessageCountResponse> countAuth(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id, @Path("messageType") String messageType);

    @GET("flipcast/inapp/message/count/{config}/{id}/{messageType}/{priority}")
    Call<MessageCountResponse> count(@Path("config") String config, @Path("id") String id, @Path("messageType") String messageType, @Path("priority") String priority);

    @GET("flipcast/inapp/message/count/{config}/{id}/{messageType}/{priority}")
    Call<MessageCountResponse> countAuth(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id, @Path("messageType") String messageType, @Path("priority") String priority);


    @GET("flipcast/inapp/messages/{config}/{id}")
    Call<MessagesResponse> messages(@Path("config") String config, @Path("id") String id);

    @GET("flipcast/inapp/messages/{config}/{id}")
    Call<MessagesResponse> messagesAuth(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id);

    @GET("flipcast/inapp/messages/{config}/{id}/{messageType}")
    Call<MessagesResponse> messages(@Path("config") String config, @Path("id") String id, @Path("messageType") String messageType);

    @GET("flipcast/inapp/messages/{config}/{id}/{messageType}")
    Call<MessagesResponse> messagesAuth(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id, @Path("messageType") String messageType);

    @GET("flipcast/inapp/messages/{config}/{id}/{messageType}/{priority}")
    Call<MessagesResponse> messages(@Path("config") String config, @Path("id") String id, @Path("messageType") String messageType, @Path("priority") String priority);

    @GET("flipcast/inapp/messages/{config}/{id}/{messageType}/{priority}")
    Call<MessagesResponse> messagesAuth(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id, @Path("messageType") String messageType, @Path("priority") String priority);

    @POST("flipcast/inapp/messages/ack/{config}/{id}")
    Call<MessageAckResponse> ack(@Path("config") String config, @Path("id") String id);

    @POST("flipcast/inapp/messages/ack/{config}/{id}")
    Call<MessageAckResponse> ack(@Header("Authorization") String authToken, @Path("config") String config, @Path("id") String id);

}
