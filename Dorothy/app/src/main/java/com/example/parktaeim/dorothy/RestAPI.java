package com.example.parktaeim.dorothy;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

/**
 * Created by parktaeim on 2017. 10. 3..
 */


public interface RestAPI {
    @FormUrlEncoded
    @POST(APIUrl.SIGN_UP_URL)
    Call<Void> signUp(@Field("id") String id, @Field("password") String password, @Field("phone") String phone, @Field("name") String name);

    @FormUrlEncoded
    @POST(APIUrl.LOGIN_URL)
    Call<Void> login(@Field("id") String id, @Field("password") String password);

    @GET(APIUrl.SEARCH)
    Call<JsonObject> search(@Header("Accept") String acceptHeader, @Header("appKey") String appKeyHeader, @QueryMap HashMap<String, Object> fieldMap);

    @POST(APIUrl.NAVIGATION_URL)
    Call<JsonObject> navigation(@Header("Accept") String acceptHeader, @Header("appKey") String appKeyHeader, @QueryMap HashMap<String, Object> fieldMap);


    @FormUrlEncoded
    @POST(APIUrl.REPORT)
    Call<Void> report(@FieldMap HashMap<String, Object> fieldMap);

    @GET(APIUrl.GETREPORTLIST)
    Call<JsonObject> getReportList();

}
