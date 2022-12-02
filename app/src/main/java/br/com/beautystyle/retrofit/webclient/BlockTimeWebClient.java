package br.com.beautystyle.retrofit.webclient;

import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import javax.inject.Inject;

import br.com.beautystyle.model.entity.BlockTime;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.retrofit.callback.CallBackReturn;
import br.com.beautystyle.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.retrofit.service.BlockTimeService;
import retrofit2.Call;

public class BlockTimeWebClient {

    @Inject
    BlockTimeService service;
    private final String token;

    @Inject
    public BlockTimeWebClient(SharedPreferences preferences) {
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
    }

    public void insert(BlockTime blockTime, ResultsCallBack<BlockTime> callBack) {
        Call<BlockTime> insert = service.insert(blockTime, token);
        insert.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<BlockTime>() {
            @Override
            public void onSuccess(BlockTime response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void delete(Long apiId, ResultsCallBack<Void> callBack) {
        Call<Void> callDelete = service.delete(apiId, token);
        callDelete.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callBack.onSuccess(null);
            }

            @Override
            public void onError(String erro) {
                callBack.onError(erro);
            }
        }));
    }

    public void update(BlockTime blockTime, ResultsCallBack<Void> callBack) {
        Call<Void> callUpdate = service.update(blockTime.getApiId(), blockTime, token);
        callUpdate.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callBack.onSuccess(null);
            }

            @Override
            public void onError(String erro) {
                callBack.onError(erro);
            }
        }));
    }

}
