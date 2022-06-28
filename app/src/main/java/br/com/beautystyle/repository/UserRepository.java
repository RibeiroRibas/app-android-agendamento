package br.com.beautystyle.repository;

import javax.inject.Inject;

import br.com.beautystyle.database.retrofit.callback.CallBackReturn;
import br.com.beautystyle.database.retrofit.service.UserService;
import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomUserDao;
import br.com.beautystyle.model.UserLogin;
import br.com.beautystyle.model.UserToken;
import br.com.beautystyle.model.entity.User;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class UserRepository {

    @Inject
    UserService service;

    private final RoomUserDao dao;

    @Inject
    public UserRepository(BeautyStyleDatabase localDatabase) {
        dao = localDatabase.getRoomUserDao();
    }

    public void authUser(UserLogin login, ResultsCallBack<UserToken> callBack) {
        Call<UserToken> callAuth = service.auth(login);
        callAuth.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<UserToken>() {
            @Override
            public void onSuccess(UserToken response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public Completable insert(User user) {
        return dao.insert(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<User> getByEmail(String email) {
        return dao.getByEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
