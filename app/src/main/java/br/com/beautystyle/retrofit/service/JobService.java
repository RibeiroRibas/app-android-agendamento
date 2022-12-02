package br.com.beautystyle.retrofit.service;

import java.util.List;

import br.com.beautystyle.model.entity.Job;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface JobService {

    @GET("job")
    Call<List<Job>> getAll(@Header("Authorization") String token);

    @POST("job")
    Call<Job> insert(@Body Job job, @Header("Authorization") String token);

    @PUT("job/{id}")
    Call<Void> update(@Path("id") Long id,@Body Job job, @Header("Authorization") String token);

    @DELETE("job/{id}")
    Call<Void> delete(@Path("id") Long id, @Header("Authorization") String token);

}
