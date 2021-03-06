package com.juan.mezclar.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Juan on 09/10/2017.
 */

public interface ServicioRetrofit2 {
    //En este proyecto no se usa retrofit por que no hay web services.
    //Mantengo esta clase para el futuro.

    @Multipart
    @POST("/")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);

}
