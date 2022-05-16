package br.com.beautystyle.database.retrofit.service;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.model.EventDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface EventService {

    @GET("event")
    Call<List<EventDto>> getAll();

    @GET("event/{eventDate}")
    Call<List<EventDto>> getByDate(@Path("eventDate") LocalDate date);

    @POST("event")
    Call<EventDto> insert(@Body EventDto event);

    @PUT("event")
    Call<EventDto> update(@Body EventDto event);

    @DELETE("event/{id}")
    Call<Void> delete(@Path("id") long id);


}
