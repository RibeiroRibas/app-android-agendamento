package br.com.beautystyle.database.retrofit.service;

import java.util.List;

import br.com.beautystyle.model.entity.Client;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ClientService {

    @GET("client/{id}")
    Call<List<Client>> getAllByCompanyId(@Path("id") Long tenant, @Header("Authorization") String token);

    @POST("client")
    Call<Client> insert(@Body Client client, @Header("Authorization") String token);

    @POST("client/client_list")
    Call<List<Client>> insertAll(@Body List<Client> client, @Header("Authorization") String token);

    @PUT("client")
    Call<Void> update(@Body Client client, @Header("Authorization") String token);

    @DELETE("client/{id}")
    Call<Void> delete(@Path("id") long id, @Header("Authorization") String token);

}
