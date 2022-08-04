package br.com.beautystyle.retrofit.webclient;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.retrofit.callback.CallBackReturn;
import br.com.beautystyle.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.retrofit.service.CategoryService;
import retrofit2.Call;

public class CategoryWebClient {

    @Inject
    CategoryService service;
    private final String token;
    private final Long tenant;

    @Inject
    public CategoryWebClient(SharedPreferences preferences) {
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void getAll(ResultsCallBack<List<Category>> callBack) {
        Call<List<Category>> callCategories = service.getAllByCompanyId(tenant, token);
        callCategories.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Category>>() {
            @Override
            public void onSuccess(List<Category> response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void insert(Category category, ResultsCallBack<Category> callBack) {
        category.setCompanyId(tenant);
        Call<Category> callInsertCategory = service.insert(category, token);
        callInsertCategory.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Category>() {
            @Override
            public void onSuccess(Category category) {
                callBack.onSuccess(category);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }


    public void update(Category category, ResultsCallBack<Void> callBack) {
        Call<Void> callUpdate = service.update(category, token);
        callUpdate.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callBack.onSuccess(null);
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
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }
}
