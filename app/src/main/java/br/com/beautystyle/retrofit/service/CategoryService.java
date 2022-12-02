package br.com.beautystyle.retrofit.service;

import java.util.List;

import br.com.beautystyle.model.entity.Category;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryService {

    @GET("category")
    Call<List<Category>> getAll(@Header("Authorization") String token);

    @POST("category")
    Call<Category> insert(@Body Category category, @Header("Authorization") String token);

    @PUT("category/{id}")
    Call<Category> update(@Path("id") Long id, @Body Category category, @Header("Authorization") String token);

    @DELETE("category/{id}")
    Call<Void> delete(@Path("id") Long id, @Header("Authorization") String token);
}
