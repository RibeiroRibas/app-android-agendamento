package br.com.beautystyle.retrofit.webclient;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.model.entity.Costumer;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.retrofit.callback.CallBackReturn;
import br.com.beautystyle.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.retrofit.service.ClientService;
import retrofit2.Call;

public class CostumerWebClient {

    @Inject
    ClientService service;
    private final String token;
    private final Long tenant;

    @Inject
    public CostumerWebClient(SharedPreferences preferences) {
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void getAll(ResultsCallBack<List<Costumer>> callBack) {
        Call<List<Costumer>> callClients = service.getAllByCompanyId(tenant, token);
        callClients.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<List<Costumer>>() {
                    @Override
                    public void onSuccess(List<Costumer> costumerList) {
                        callBack.onSuccess(costumerList);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }));
    }

    public void insert(Costumer costumer, ResultsCallBack<Costumer> callBack) {
        costumer.setCompanyId(tenant);
        Call<Costumer> callNewClient = service.insert(costumer, token);
        callNewClient.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<Costumer>() {
                    @Override
                    public void onSuccess(Costumer costumer) {
                        callBack.onSuccess(costumer);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }
        ));
    }

    public void update(Costumer costumer, ResultsCallBack<Void> callBack) {
        Call<Void> callUpdateClient = service.update(costumer, token);
        callUpdateClient.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callBack.onSuccess(null);
            }

            @Override
            public void onError(String erro) {

            }
        }));
    }

    public void delete(Costumer costumer, ResultsCallBack<Void> callBack) {
        Call<Void> callDeletedClient = service.delete(costumer.getApiId(), token);
        callDeletedClient.enqueue(new CallBackWithoutReturn(
                        new CallBackWithoutReturn.CallBackResponse() {
                            @Override
                            public void onSuccess() {
                                callBack.onSuccess(null);
                            }

                            @Override
                            public void onError(String erro) {
                                callBack.onError(erro);
                            }
                        }
                )
        );
    }

    public void insertAll(List<Costumer> contactList, ResultsCallBack<List<Costumer>> callBack) {
        contactList.forEach(costumer -> costumer.setCompanyId(tenant));
        Call<List<Costumer>> callClientList = service.insertAll(contactList, token);
        callClientList.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<List<Costumer>>() {
                    @Override
                    public void onSuccess(List<Costumer> costumers) {
                        callBack.onSuccess(costumers);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }
        ));
    }

}
