package br.com.beautystyle.database.retrofit;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.inject.Singleton;

import br.com.beautystyle.database.retrofit.service.CategoryService;
import br.com.beautystyle.database.retrofit.service.ClientService;
import br.com.beautystyle.database.retrofit.service.EventService;
import br.com.beautystyle.database.retrofit.service.ExpenseService;
import br.com.beautystyle.database.retrofit.service.JobService;
import br.com.beautystyle.database.retrofit.service.UserService;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class NetworkModule {

    private static final String URL_BASE = "http://192.168.3.214:8080";

    @Singleton
    @Provides
    public Retrofit provideRetrofit(OkHttpClient client,ObjectMapper mapper){
        return  new Retrofit.Builder()
                .baseUrl(URL_BASE)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();
    }

    @Singleton
    @Provides
    public ObjectMapper providesMapper(){
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
                .addInterceptor(new NetworkConnectionInterceptor(context))
                .build();
    }

    @Singleton
    @Provides
    public EventService providesEventService(Retrofit retrofit){
        return retrofit.create(EventService.class);
    }

    @Singleton
    @Provides
    public ClientService providesClientService(Retrofit retrofit){
        return retrofit.create(ClientService.class);
    }

    @Singleton
    @Provides
    public JobService providesJobService(Retrofit retrofit){
        return retrofit.create(JobService.class);
    }

    @Singleton
    @Provides
    public CategoryService providesCategoryService (Retrofit retrofit){
        return retrofit.create(CategoryService.class);
    }

    @Singleton
    @Provides
    public UserService providesUserService(Retrofit retrofit){
        return retrofit.create(UserService.class);
    }

    @Singleton
    @Provides
    public ExpenseService providesExpenseService(Retrofit retrofit){
        return retrofit.create(ExpenseService.class);
    }

}
