package com.example.dataharvester;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApis {
    @Multipart
    @POST("upload")
    Call<RequestBody> uploadAudio(@Part MultipartBody.Part part, @Part("recordingData") RequestBody requestBody);
}
