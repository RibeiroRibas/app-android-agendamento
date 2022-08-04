package br.com.beautystyle.retrofit.service;

import br.com.beautystyle.model.UserToken;
import br.com.beautystyle.model.UserLogin;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("auth")
    Call<UserToken> auth(@Body UserLogin login);

}
