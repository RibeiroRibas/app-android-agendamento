package br.com.beautystyle.retrofit.webclient;

import javax.inject.Inject;

import br.com.beautystyle.retrofit.model.form.UserLoginForm;
import br.com.beautystyle.retrofit.model.dto.UserDto;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.retrofit.callback.CallBackReturn;
import br.com.beautystyle.retrofit.service.UserService;
import retrofit2.Call;

public class UserWebClient {

    @Inject
    UserService service;

    @Inject
    public UserWebClient() {
    }

    public void authUser(UserLoginForm login, ResultsCallBack<UserDto> callBack) {
        Call<UserDto> callAuth = service.auth(login);
        callAuth.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<UserDto>() {
            @Override
            public void onSuccess(UserDto response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }
}
