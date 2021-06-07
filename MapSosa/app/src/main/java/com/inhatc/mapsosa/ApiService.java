package com.inhatc.mapsosa;

import com.kakao.auth.Session;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    public static final String API_URL = "https://kakao.com/v2/api/talk/memo/default/";
    Session session= Session.getCurrentSession();
     public static final String token = session.getAccessToken();


    @POST("send")
    Call<ResponseBody>postComment();
}
