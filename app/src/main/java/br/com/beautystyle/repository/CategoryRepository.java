package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.retrofit.callback.CallBackReturn;
import br.com.beautystyle.database.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.database.retrofit.service.CategoryService;
import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomCategoryDao;
import br.com.beautystyle.model.entity.Category;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class CategoryRepository {

    private final RoomCategoryDao dao;
    @Inject
    CategoryService service;
    private final String token;
    private final Long tenant;

    @Inject
    public CategoryRepository(BeautyStyleDatabase database, SharedPreferences preferences) {
        dao = database.getRoomCategoryDao();
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public Single<List<Category>> getAllFromRoom() {
        return dao.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable insertOnRoom(Category category) {
        return dao.insert(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable deleteOnRoom(Category category) {
        return dao.delete(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable updateOnRoom(Category category) {
        return dao.update(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Long>> insertAllOnRoom(List<Category> categoriesFromRoom,
                                              List<Category> categoriesFromApi) {
        setIdFromRoomToApi(categoriesFromRoom, categoriesFromApi);
        return dao.insertAll(categoriesFromApi)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    private void setIdFromRoomToApi(List<Category> categoriesFromRoom,
                                    List<Category> categoriesFromApi) {
        categoriesFromRoom.forEach(fromRoom -> categoriesFromApi.forEach(fromApi -> {
            if (fromRoom.getApiId().equals(fromApi.getApiId()))
                fromApi.setId(fromRoom.getId());
        }));
    }

    public void getCategoryListFromApi(ResultsCallBack<List<Category>> callBack) {
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

    public void insertOnApi(Category category, ResultsCallBack<Category> callBack) {
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


    public void updateOnApi(Category category, ResultsCallBack<Void> callBack) {
        Call<Void> callUpdate = service.update(category, token);
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

    public void deleteOnApi(Long apiId, ResultsCallBack<Void> callBack) {
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
}
