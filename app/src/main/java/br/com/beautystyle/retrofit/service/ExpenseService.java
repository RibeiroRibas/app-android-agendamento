package br.com.beautystyle.retrofit.service;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.model.util.Report;
import br.com.beautystyle.model.entity.Expense;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ExpenseService {

    @GET("expense")
    Call<List<Expense>> getAll();

    @POST("expense")
    Call<Expense> insert(@Body Expense expense,@Header("Authorization") String token);

    @PUT("expense")
    Call<Expense> update(@Body Expense expense,@Header("Authorization") String token);

    @DELETE("expense/{id}")
    Call<Void> delete(@Path("id") long id,@Header("Authorization") String token);

    @GET("expense/{companyId}")
    Call<List<String>> getYearsList(@Path("companyId") Long tenant,
                                    @Header("Authorization") String token);

    @GET("expense/{id}/{startDate}/{endDate}")
    Call<List<Expense>> getByPeriod(@Path("startDate") LocalDate startDate,
                                    @Path("endDate") LocalDate endDate,
                                    @Path("id") Long tenant,
                                    @Header("Authorization") String token);

    @GET("expense/report/{id}/{startDate}/{endDate}")
    Call<List<Report>> getReportByPeriod(@Path("startDate") LocalDate startDate,
                                         @Path("endDate") LocalDate endDate,
                                         @Path("id") Long tenant,
                                         @Header("Authorization") String token);
    @GET("expense/report/{id}/{date}")
    Call<List<Report>> getReportByDate(@Path("date") LocalDate date,
                                       @Path("id") Long tenant,
                                       @Header("Authorization") String token);
}
