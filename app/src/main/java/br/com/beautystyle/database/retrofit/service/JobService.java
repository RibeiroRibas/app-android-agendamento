package br.com.beautystyle.database.retrofit.service;

import java.util.List;

import br.com.beautystyle.model.entities.Job;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface JobService {

    @GET("job")
    Call<List<Job>> getAll();

    @POST("job")
    Call<Job> insert(@Body Job job);

    @PUT("job")
    Call<Job> update(@Body Job job);

    @DELETE("job/{id}")
    Call<Void> delete(@Path("id") long id);

}
