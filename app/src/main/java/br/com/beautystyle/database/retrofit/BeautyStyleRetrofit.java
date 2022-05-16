package br.com.beautystyle.database.retrofit;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.beautystyle.database.retrofit.service.ClientService;
import br.com.beautystyle.database.retrofit.service.EventService;
import br.com.beautystyle.database.retrofit.service.EventWithJobsService;
import br.com.beautystyle.database.retrofit.service.ExpenseService;
import br.com.beautystyle.database.retrofit.service.JobService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class BeautyStyleRetrofit {

    private static final String URL_BASE = "http://192.168.112.1:8080";
    private final ClientService clientService;
    private final JobService jobService;
    private final ExpenseService expenseService;
    private final EventService eventService;
    private final EventWithJobsService eventWithJobsService;

    public BeautyStyleRetrofit() {

        OkHttpClient client = clientConfiguration();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .client(client)
                    .addConverterFactory(JacksonConverterFactory.create(mapper))
                    .build();

        clientService = retrofit.create(ClientService.class);
        jobService = retrofit.create(JobService.class);
        expenseService = retrofit.create(ExpenseService.class);
        eventService = retrofit.create(EventService.class);
        eventWithJobsService = retrofit.create(EventWithJobsService.class);

    }


    @NonNull
    private static OkHttpClient clientConfiguration() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    public ClientService getClientService() {
        return clientService;
    }

    public JobService getJobService() {
        return jobService;
    }

    public ExpenseService getExpenseService() {
        return expenseService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public EventWithJobsService getEventWithJobsService() {
        return eventWithJobsService;
    }
}
