package br.com.beautystyle.retrofit.webclient;

import javax.inject.Inject;

import br.com.beautystyle.model.UserLogin;
import br.com.beautystyle.model.UserToken;
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

    public void authUser(UserLogin login, ResultsCallBack<UserToken> callBack) {
        Call<UserToken> callAuth = service.auth(login);
        callAuth.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<UserToken>() {
            @Override
            public void onSuccess(UserToken response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }
}
