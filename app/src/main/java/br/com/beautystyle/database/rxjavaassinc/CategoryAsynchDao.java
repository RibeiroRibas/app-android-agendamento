package br.com.beautystyle.database.rxjavaassinc;

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

public class CategoryAsynchDao {

    private final RoomCategoryDao dao;

    @Inject
    public CategoryAsynchDao(BeautyStyleDatabase database) {
        dao = database.getRoomCategoryDao();
    }

    public Observable<List<Category>> getAll() {
        return dao.getAllObservable()
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

    public Single<List<Category>> getAllSingle() {
        return dao.getAllSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
