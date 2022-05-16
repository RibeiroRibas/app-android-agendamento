package br.com.beautystyle.database.retrofit.service;

import java.util.List;

import br.com.beautystyle.model.entities.Client;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ClientService {

    @GET("client")
    Call<List<Client>> getAll();

    @POST("client")
    Call<Client> insert(@Body Client client);

    @POST("client/salva_lista")
    Call<List<Client>> insertAll(@Body List<Client> client);

    @PUT("client")
    Call<Client> update(@Body Client client);

    @DELETE("client/{id}")
    Call<Void> delete(@Path("id") long id);

    @GET("client/{id}")
    Single<Client> getById(@Path("id") long id, @Body Client client);

}
