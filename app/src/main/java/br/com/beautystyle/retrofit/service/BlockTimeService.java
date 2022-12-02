package br.com.beautystyle.retrofit.service;

import br.com.beautystyle.model.entity.BlockTime;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BlockTimeService {

    @POST("block_time")
    Call<BlockTime> insert(@Body BlockTime blockTime, @Header("Authorization") String token);

    @PUT("block_time/{id}")
    Call<Void> update(@Path("id") Long id, @Body BlockTime blockTime, @Header("Authorization") String token);

    @DELETE("block_time/{id}")
    Call<Void> delete(@Path("id") Long id, @Header("Authorization") String token);

}
