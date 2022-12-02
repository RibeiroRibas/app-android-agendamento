package br.com.beautystyle.retrofit.webclient;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.model.entity.Customer;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.retrofit.callback.CallBackReturn;
import br.com.beautystyle.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.retrofit.service.CostumerService;
import retrofit2.Call;

public class CostumerWebClient {

    @Inject
    CostumerService service;
    private final String token;
    private final Long tenant;

    @Inject
    public CostumerWebClient(SharedPreferences preferences) {
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void getAll(ResultsCallBack<List<Customer>> callBack) {
        Call<List<Customer>> callClients = service.getAll(token);
        callClients.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<List<Customer>>() {
                    @Override
                    public void onSuccess(List<Customer> customerList) {
                        callBack.onSuccess(customerList);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }));
    }

    public void insert(Customer customer, ResultsCallBack<Customer> callBack) {
        Call<Customer> callNewClient = service.insert(customer, token);
        callNewClient.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<Customer>() {
                    @Override
                    public void onSuccess(Customer customer) {
                        callBack.onSuccess(customer);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }
        ));
    }

    public void update(Customer customer, ResultsCallBack<Void> callBack) {
        Call<Void> callUpdateClient = service.update(customer, token);
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

    public void delete(Customer customer, ResultsCallBack<Void> callBack) {
        Call<Void> callDeletedClient = service.delete(customer.getApiId(), token);
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

    public void insertAll(List<Customer> contactList, ResultsCallBack<List<Customer>> callBack) {
        Call<List<Customer>> callClientList = service.insertAll(contactList, token);
        callClientList.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<List<Customer>>() {
                    @Override
                    public void onSuccess(List<Customer> customers) {
                        callBack.onSuccess(customers);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }
        ));
    }

}
