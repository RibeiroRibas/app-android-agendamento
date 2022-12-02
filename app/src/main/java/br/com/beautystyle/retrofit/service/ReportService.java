package br.com.beautystyle.retrofit.service;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.model.util.Report;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ReportService {

    @GET("report/{id}/{startDate}/{endDate}")
    Call<List<Report>> getReportByPeriod(@Path("startDate") LocalDate startDate,
                                         @Path("endDate") LocalDate endDate,
                                         @Path("id") Long tenant,
                                         @Header("Authorization") String token);

    @GET("report/{companyId}")
    Call<List<String>> getYearsList(@Path("companyId") Long tenant,
                                    @Header("Authorization") String token);

    @GET("report/{id}/{date}")
    Call<List<Report>> getReportByDate(@Path("date") LocalDate date,
                                       @Path("id") Long tenant,
                                       @Header("Authorization") String token);
}
