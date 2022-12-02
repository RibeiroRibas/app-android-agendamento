package br.com.beautystyle.retrofit.service;

import java.util.List;

import br.com.beautystyle.model.entity.Customer;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CostumerService {

    @GET("customer")
    Call<List<Customer>> getAll(@Header("Authorization") String token);

    @POST("customer")
    Call<Customer> insert(@Body Customer customer, @Header("Authorization") String token);

    @POST("customer/insert_all")
    Call<List<Customer>> insertAll(@Body List<Customer> customer, @Header("Authorization") String token);

    @PUT("customer/{id}")
    Call<Void> update(@Body Customer customer, @Header("Authorization") String token);

    @DELETE("customer/{id}")
    Call<Void> delete(@Path("id") long id, @Header("Authorization") String token);

}
