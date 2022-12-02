package br.com.beautystyle.database.rxjava;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomCategoryDao;
import br.com.beautystyle.model.entity.Category;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategoryRxJava {

    private final RoomCategoryDao dao;

    @Inject
    public CategoryRxJava(BeautyStyleDatabase database) {
        dao = database.getRoomCategoryDao();
    }

    public Observable<List<Category>> getAllObservable(Long tenant) {
        return dao.getAllObservable(tenant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable insert(Category category) {
        return dao.insert(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable delete(Category category) {
        return dao.delete(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable update(Category category) {
        return dao.update(category)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Long>> insertAll(List<Category> categories) {
        return dao.insertAll(categories)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Category>> getAllSingle(Long tenant) {
        return dao.getAllSingle(tenant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
