package br.com.beautystyle.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomUserDao;
import br.com.beautystyle.retrofit.model.form.UserLoginForm;
import br.com.beautystyle.retrofit.model.dto.UserDto;
import br.com.beautystyle.model.entity.User;
import br.com.beautystyle.retrofit.webclient.UserWebClient;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserRepository {

    @Inject
    UserWebClient webClient;
    private final RoomUserDao dao;

    @Inject
    public UserRepository(BeautyStyleDatabase localDatabase) {
        dao = localDatabase.getRoomUserDao();
    }

    public LiveData<Resource<UserDto>> authUser(UserLoginForm login) {
        MutableLiveData<Resource<UserDto>> liveData = new MutableLiveData<>();
        webClient.authUser(login, new ResultsCallBack<UserDto>() {
            @Override
            public void onSuccess(UserDto result) {
                liveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }

    public Completable insertOnRoom(User user) {
        return dao.insert(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<Resource<User>> getByEmail(String email) {
        MutableLiveData<Resource<User>> liveData = new MutableLiveData<>();
        dao.getByEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(user -> liveData.setValue(new Resource<>(user, null)))
                .doOnError(error -> liveData.setValue(new Resource<>(null, error.getMessage())))
                .subscribe();
        return liveData;
    }
}
