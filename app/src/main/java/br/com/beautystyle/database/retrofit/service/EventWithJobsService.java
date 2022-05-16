package br.com.beautystyle.database.retrofit.service;

import java.util.List;

import br.com.beautystyle.model.EventDto;
import br.com.beautystyle.model.entities.Job;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface EventWithJobsService {

    @PUT("event/{eventId}/jobList")
    Call<EventDto> insertJobList(@Body List<Job> jobList);

}
