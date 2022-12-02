package br.com.beautystyle.retrofit.callback;

import static br.com.beautystyle.retrofit.callback.CallbackMessages.MESSAGE_SERVER_ERROR;
import static br.com.beautystyle.retrofit.callback.CallbackMessages.NO_INTERNET_CONNECTION;

import androidx.annotation.NonNull;

import java.io.IOException;

import br.com.beautystyle.retrofit.NoConnectivityException;
import okhttp3.ResponseBody;
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
        T result = response.body();
        if (response.isSuccessful()) {
            if (result != null) {
                callBack.onSuccess(result);
            }
        } else {
            ResponseBody responseBody = response.errorBody();
            if (responseBody != null) {
                try {
                    callBack.onError(responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                callBack.onError(response.message());
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
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
