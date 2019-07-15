package com.bytedance.androidcamp.network.dou.api;



import com.bytedance.androidcamp.network.dou.model.GetVidRes;
import com.bytedance.androidcamp.network.dou.model.PostVidRes;
import com.bytedance.androidcamp.network.dou.model.Video;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IMiniDouyinService {
    // TODO 7: Define IMiniDouyinService
    String HOST = "http://test.androidcamp.bytedance.com/mini_douyin/invoke/";

    @Multipart
    @POST("video")
    Call<PostVidRes> postVideo(@Query("student_id") int id,
                               @Query("user_name") String name,
                               @Part MultipartBody.Part cover,
                               @Part MultipartBody.Part video);

    @GET("video")
    Call<GetVidRes> getVideo();
}
