package com.depex.odepto.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ProjectApi {

    @POST("list_board.php")
    Call<String> getBoardList(@Body String body);
    @POST("index.php")
    Call<String> getCards(@Body String body);

    @POST("index.php")
    Call<String> createCard(@Body String body);

    @POST("index.php")
    Call<String> moveCard(@Body String body);

    @POST("index.php")
    Call<String> createList(@Body String body);

    @Multipart
    @POST("file_upload.php")
    Call<ResponseBody> upload(@Part("description")RequestBody description, @Part MultipartBody.Part file);

    @POST("index.php")
    Call<String> createComment(@Body String body);

    @POST("index.php")
    Call<String> cardComments(@Body String body);

    @POST("index.php")
    Call<String> cardLabel(@Body String body);
}
