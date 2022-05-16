package br.com.beautystyle.database.retrofit.service;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.model.entities.Expense;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ExpenseService {

    @GET("expense")
    Call<List<Expense>> getAll();

    @GET("expense/{date}")
    Call<List<Expense>> findByDate(@Path("date") LocalDate date);

    @POST("expense")
    Call<Expense> insert(@Body Expense expense);

    @PUT("expense")
    Call<Expense> update(@Body Expense expense);

    @DELETE("expense/{id}")
    Call<Void> delete(@Path("id") long id);


}
