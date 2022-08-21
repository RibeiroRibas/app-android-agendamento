package br.com.beautystyle.retrofit;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import br.com.beautystyle.retrofit.service.CategoryService;
import br.com.beautystyle.retrofit.service.ClientService;
import br.com.beautystyle.retrofit.service.EventService;
import br.com.beautystyle.retrofit.service.ExpenseService;
import br.com.beautystyle.retrofit.service.JobService;
import br.com.beautystyle.retrofit.service.ReportService;
import br.com.beautystyle.retrofit.service.UserService;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class NetworkModule {

  //  private static final String URL_BASE = "http://192.168.3.214:8080/";
    private static final String URL_BASE = "https://api-beauty-style.herokuapp.com";

    @Singleton
    @Provides
    public Retrofit provideRetrofit(OkHttpClient client, ObjectMapper mapper) {
        return new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();
    }


    @Singleton
    @Provides
    public ObjectMapper providesMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Singleton
    @Provides
    public OkHttpClient clientConfiguration(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new NetworkConnectionInterceptor(context))
                .build();
    }

    @Singleton
    @Provides
    public EventService providesEventService(Retrofit retrofit) {
        return retrofit.create(EventService.class);
    }

    @Singleton
    @Provides
    public ClientService providesClientService(Retrofit retrofit) {
        return retrofit.create(ClientService.class);
    }

    @Singleton
    @Provides
    public JobService providesJobService(Retrofit retrofit) {
        return retrofit.create(JobService.class);
    }

    @Singleton
    @Provides
    public CategoryService providesCategoryService(Retrofit retrofit) {
        return retrofit.create(CategoryService.class);
    }

    @Singleton
    @Provides
    public UserService providesUserService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

    @Singleton
    @Provides
    public ExpenseService providesExpenseService(Retrofit retrofit) {
        return retrofit.create(ExpenseService.class);
    }

    @Singleton
    @Provides
    public ReportService providesReportService(Retrofit retrofit) {
        return retrofit.create(ReportService.class);
    }

}
