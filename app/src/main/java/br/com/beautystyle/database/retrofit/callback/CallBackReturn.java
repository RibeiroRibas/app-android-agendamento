package br.com.beautystyle.database.retrofit.callback;

import static br.com.beautystyle.database.retrofit.callback.CallbackMessages.BAD_CREDENTIALS;
import static br.com.beautystyle.database.retrofit.callback.CallbackMessages.NO_INTERNET_CONNECTION;
import static br.com.beautystyle.database.retrofit.callback.CallbackMessages.MESSAGE_ERROR;
import static br.com.beautystyle.database.retrofit.callback.CallbackMessages.MESSAGE_SERVER_ERROR;

import androidx.annotation.NonNull;

import br.com.beautystyle.database.retrofit.NoConnectivityException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class CallBackReturn<T> implements Callback<T> {

    private final CallBackResponse<T> callBack;

    public CallBackReturn(CallBackResponse<T> callBack) {
        this.callBack = callBack;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            T result = response.body();
            if (result != null) {
                callBack.onSuccess(result);
            }
        } else if (response.code() == 400) {
            callBack.onError(BAD_CREDENTIALS);
        } else {
            callBack.onError(MESSAGE_ERROR);
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, Throwable t) {
        if (t instanceof NoConnectivityException) {
            callBack.onError(NO_INTERNET_CONNECTION);
        } else {
            callBack.onError(MESSAGE_SERVER_ERROR + " " + t.getMessage());
        }
    }

    public interface CallBackResponse<T> {
        void onSuccess(T response);

        void onError(String error);
    }

}
