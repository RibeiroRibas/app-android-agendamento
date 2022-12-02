package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.FREE_ACCOUNT;
import static br.com.beautystyle.repository.ConstantsRepository.PROFILE_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.USER_PREMIUM;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.rxjava.CategoryRxJava;
import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.retrofit.webclient.CategoryWebClient;

public class CategoryRepository {

    @Inject
    CategoryRxJava dao;
    @Inject
    CategoryWebClient webClient;
    private final MutableLiveData<Resource<List<Category>>> liveData = new MutableLiveData<>();
    private final Long tenant;
    private final String profile;

    @Inject
    public CategoryRepository(SharedPreferences preferences) {
        profile = preferences.getString(PROFILE_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void insert(Category category) {
        if(isPremiumUser()){
            insertOnApi(category);
        }
        if(isFreeAccount()){
            category.setTenant(tenant);
            insertOnRoom(category);
        }
    }

    private void insertOnRoom(Category category) {
        dao.insert(category).subscribe();
    }

    private void insertOnApi(Category category) {
        webClient.insert(category, new ResultsCallBack<Category>() {
            @Override
            public void onSuccess(Category result) {
                insertOnRoom(result);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public void update(Category category) {
        if(isPremiumUser()){
            updateOnApi(category);
        }
        if(isFreeAccount()){
            updateOnRoom(category);
        }
    }

    private void updateOnRoom(Category category) {
        dao.update(category).subscribe();
    }

    private void updateOnApi(Category category) {
        webClient.update(category, new ResultsCallBack<Category>() {
            @Override
            public void onSuccess(Category updatedCategory) {
                updateOnRoom(updatedCategory);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public void delete(Category category) {
        if(isPremiumUser()){
            deleteOnApi(category);
        }
        if(isFreeAccount()){
            deleteOnRoom(category);
        }
    }

    private void deleteOnRoom(Category category) {
        dao.delete(category).subscribe();
    }

    private void deleteOnApi(Category category) {
        webClient.delete(category.getApiId(), new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
               deleteOnRoom(category);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public LiveData<Resource<List<Category>>> getAllLiveData() {
        getAllFromRoom();
        if(isPremiumUser()){
            getAllFromApi();
        }
        return liveData;
    }

    private void getAllFromRoom() {
        dao.getAllObservable(tenant).doOnNext(categories -> {
            liveData.setValue(new Resource<>(categories,null));
        }).subscribe();
    }

    private void getAllFromApi() {
        webClient.getAll(new ResultsCallBack<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categoriesFromApi) {
                updateLocal(categoriesFromApi);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    private void updateLocal(List<Category> categoriesFromApi) {
        dao.getAllSingle(tenant).doOnSuccess(categoriesFromRoom->{
            deleteFromRoomIfNotExistOnApi(categoriesFromApi,categoriesFromRoom);
            setIdFromRoomToApi(categoriesFromRoom, categoriesFromApi);
            dao.insertAll(categoriesFromApi).subscribe();
        }).subscribe();
    }

    public boolean isFreeAccount() {
        return profile.equals(FREE_ACCOUNT);
    }

    public boolean isPremiumUser() {
        return profile.equals(USER_PREMIUM);
    }

    private void deleteFromRoomIfNotExistOnApi(List<Category> categoriesFromApi,
                                               List<Category> categoriesFromRoom) {
        categoriesFromRoom.forEach(fromRoom -> {
            if (fromRoom.isNotExistOnApi(categoriesFromApi))
                dao.delete(fromRoom).subscribe();
        });
    }

    private void setIdFromRoomToApi(List<Category> categoriesFromRoom,
                                    List<Category> categoriesFromApi) {
        categoriesFromRoom.forEach(fromRoom -> categoriesFromApi.forEach(fromApi -> {
            if (fromRoom.isApiIdEquals(fromApi))
                fromApi.setId(fromRoom.getId());
        }));
    }

}
