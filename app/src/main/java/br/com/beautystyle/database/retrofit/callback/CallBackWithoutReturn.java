package br.com.beautystyle.database.retrofit.callback;

import static br.com.beautystyle.database.retrofit.callback.CallBackMessages.MESSAGE_CONNECTION_FAIL;
import static br.com.beautystyle.database.retrofit.callback.CallBackMessages.MESSAGE_WITHOUT_RESPONSE;

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
            callBack.onError(MESSAGE_WITHOUT_RESPONSE);
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<Void> call, Throwable t) {
        callBack.onError(MESSAGE_CONNECTION_FAIL + t.getMessage());
    }

    public interface CallBackResponse {
        void onSuccess();
        void onError(String erro);
    }
}
