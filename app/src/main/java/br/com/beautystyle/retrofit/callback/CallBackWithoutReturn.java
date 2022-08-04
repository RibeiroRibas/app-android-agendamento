package br.com.beautystyle.retrofit.callback;

import static br.com.beautystyle.retrofit.callback.CallbackMessages.NO_INTERNET_CONNECTION;
import static br.com.beautystyle.retrofit.callback.CallbackMessages.MESSAGE_ERROR;
import static br.com.beautystyle.retrofit.callback.CallbackMessages.MESSAGE_SERVER_ERROR;

import br.com.beautystyle.retrofit.NoConnectivityException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CallBackWithoutReturn implements Callback<Void> {

    private final CallBackResponse callBack;

    public CallBackWithoutReturn(CallBackResponse callBack) {
        this.callBack = callBack;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<Void> call, Response<Void> response) {
        if(response.isSuccessful()){
            callBack.onSuccess();
        }else{
            callBack.onError(MESSAGE_ERROR);
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<Void> call, Throwable t) {
        if(t instanceof NoConnectivityException) {
            callBack.onError(NO_INTERNET_CONNECTION);
        }else{
            callBack.onError(MESSAGE_SERVER_ERROR + " " + t.getMessage());
        }
    }

    public interface CallBackResponse {
        void onSuccess();
        void onError(String erro);
    }
}
