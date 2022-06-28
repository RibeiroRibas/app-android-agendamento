package br.com.beautystyle.database.retrofit.service;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.database.room.references.EventWithClientAndJobs;
import br.com.beautystyle.model.Report;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventService {

    @GET("event/{eventDate}/{companyId}")
    Call<List<EventWithClientAndJobs>> getByDate(@Path("eventDate") LocalDate date,
                                                 @Path("companyId") Long companyId,
                                                 @Header("Authorization") String token);

    @POST("event")
    Call<EventWithClientAndJobs> insert(@Body EventWithClientAndJobs event,
                                        @Header("Authorization") String token);

    @PUT("event")
    Call<Void> update(@Body EventWithClientAndJobs event, @Header("Authorization") String token);

    @DELETE("event/{id}")
    Call<Void> delete(@Path("id") long id,@Header("Authorization") String token);

    @GET("event/{companyId}")
    Call<List<String>> getYearsList(@Path("companyId") Long tenant,
                                    @Header("Authorization") String token);

    @GET("event/report/{id}/{startDate}/{endDate}")
    Call<List<Report>> getReportByPeriod(@Path("startDate") LocalDate startDate,
                                         @Path("endDate") LocalDate endDate,
                                         @Path("id") Long tenant,
                                         @Header("Authorization") String token);
    @GET("event/report/{id}/{date}")
    Call<List<Report>> getReportByDate(@Path("date") LocalDate date,
                                       @Path("id") Long tenant,
                                       @Header("Authorization") String token);
}
