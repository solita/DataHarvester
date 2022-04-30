package com.example.dataharvester;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface UploadApis {
    @GET("fileUpload/")
    Call<List<File>> getFiles();

    @GET("fileUpload/files/{filename}")
    @Streaming
    Call<ResponseBody> getImageByName(@Path("filename") String name);

    @Multipart
    @POST("upload/")
    Call<ResponseBody> uploadFiles(@Part MultipartBody.Part recordingData);

    @FormUrlEncoded
    @POST("uploadlabels/")
    Call<ResponseBody> uploadLabel(@Field("filename") String fileName, @Field("labels") String labels);

}
