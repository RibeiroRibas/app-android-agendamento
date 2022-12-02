package br.com.beautystyle.database.rxjava;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomOpeningHoursDao;
import br.com.beautystyle.model.entity.OpeningHours;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OpeningHoursRxJava {

    private final RoomOpeningHoursDao dao;

    @Inject
    public OpeningHoursRxJava(BeautyStyleDatabase database) {
        this.dao = database.getRoomOpeningHoursDao();
    }

    public Single<List<OpeningHours>> getAll(Long tenant) {
        return dao.getAll(tenant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable insertAll(List<OpeningHours> ope) {
        return dao.insertAll(ope)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
