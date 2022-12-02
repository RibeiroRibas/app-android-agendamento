package br.com.beautystyle.retrofit.service;

import java.time.LocalDate;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.retrofit.model.dto.EventWithClientAndJobsDto;
import br.com.beautystyle.retrofit.model.form.EventForm;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventService {

    @GET("event/by_professional/{eventDate}")
    Call<EventWithClientAndJobsDto> getByDate(@Path("eventDate") LocalDate eventDate,
                                                    @Header("Authorization") String token);

    @POST("event/by_professional")
    Call<EventWithClientAndJobs> insert(@Body EventForm event,
                                        @Header("Authorization") String token);

    @PUT("event/by_professional/{id}")
    Call<Void> update(@Path("id") Long id,
                      @Body EventForm event,
                      @Header("Authorization") String token);

    @DELETE("event/by_professional/{id}")
    Call<Void> delete(@Path("id") Long id, @Header("Authorization") String token);

}
