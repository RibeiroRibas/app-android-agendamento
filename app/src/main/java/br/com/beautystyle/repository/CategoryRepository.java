package br.com.beautystyle.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.rxjavaassinc.CategoryAsynchDao;
import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.retrofit.webclient.CategoryWebClient;

public class CategoryRepository {

    @Inject
    CategoryAsynchDao dao;
    @Inject
    CategoryWebClient webClient;
    private final MutableLiveData<Resource<List<Category>>> liveData = new MutableLiveData<>();

    @Inject
    public CategoryRepository() {
    }



    public void insert(Category category) {
        webClient.insert(category, new ResultsCallBack<Category>() {
            @Override
            public void onSuccess(Category result) {
                dao.insert(category).subscribe();
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public void update(Category category) {
        webClient.update(category, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                dao.update(category).subscribe();
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public void delete(Category category) {
        webClient.delete(category.getApiId(), new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                dao.delete(category).subscribe();
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public LiveData<Resource<List<Category>>> getAllFromRoomLiveData() {
        dao.getAll().doOnNext(categories -> {
            liveData.setValue(new Resource<>(categories,null));
        }).subscribe();
        return liveData;
    }

    public void getAllFromApi() {
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
        dao.getAllSingle().doOnSuccess(categoriesFromRoom->{
            deleteFromRoomIfNotExistOnApi(categoriesFromApi,categoriesFromRoom);
            setIdFromRoomToApi(categoriesFromRoom, categoriesFromApi);
            dao.insertAll(categoriesFromApi).subscribe();
        }).subscribe();
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
