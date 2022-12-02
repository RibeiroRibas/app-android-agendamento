package br.com.beautystyle.retrofit.service;

import br.com.beautystyle.retrofit.model.dto.UserDto;
import br.com.beautystyle.retrofit.model.form.UserLoginForm;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("auth")
    Call<UserDto> auth(@Body UserLoginForm login);

}
