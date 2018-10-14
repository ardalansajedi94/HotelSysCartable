package com.hotelsys.cartable.androidapp.Server;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Mohammad on 12/9/2017.
 */

public interface RequestInterface {
    @Headers("Accept: application/json")
    @POST("employee/login")
    Call<ServerResponse> login(@Body ServerRequest request);
    @Headers("Accept: application/json")
    @GET("employee/myProfile")
    Call<ServerResponse> getProfile(@Header("Authorization") String jwt);
    @Headers("Accept: application/json")
    @GET("instructions/categories")
    Call<ServerResponse> getInstructionCategories(@Header("Authorization") String jwt);
    @Headers("Accept: application/json")
    @GET("instructions")
    Call<ServerResponse> getInstuctions(@Header("Authorization") String jwt,@Query("cat_id") int cat_id);
    @Headers("Accept: application/json")
    @GET("internal_news")
    Call<ServerResponse> getInternalNews(@Header("Authorization") String jwt);
    @GET("cartable/general")
    Call<ServerResponse> getGeneralCartable(@Header("Authorization") String jwt);
    @GET("cartable/mine")
    Call<ServerResponse> getMyCartable(@Header("Authorization") String jwt);
    @GET("cartable/assign_task_items")
    Call<ServerResponse> getAssignTaskItems(@Header("Authorization") String jwt);
    @Multipart()
    @Headers("Accept: application/json")
    @POST("cartable/assign_manual_task")
    Call<ServerResponse> addManualTask(@Header("Authorization") String jwt, @Part("title") RequestBody title,@Part("content") RequestBody content,@Part("worker_id") RequestBody worker_id, @Part List<MultipartBody.Part> images);
    @Headers("Accept: application/json")
    @GET()
    Call<ServerResponse> dynamicGetRequestJWT(@Header("Authorization") String jwt, @Url String url);
    @Headers("Accept: application/json")
    @POST()
    Call<ServerResponse> dynamicPostRequest(@Header("Authorization") String jwt,@Url String url,@Body ServerRequest request);
    @Multipart
    @Headers("Accept: application/json")
    @POST("employee/profile/picture")
    Call<ServerResponse> upload_profile_picture(@Header("Authorization") String jwt,  @Part MultipartBody.Part file);
}
