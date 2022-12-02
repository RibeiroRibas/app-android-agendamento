package br.com.beautystyle.database.rxjava;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomBlockTimeDao;
import br.com.beautystyle.model.entity.BlockTime;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BlockTimeRxJava {

    private final RoomBlockTimeDao dao;

    @Inject
    public BlockTimeRxJava(BeautyStyleDatabase database) {
        dao = database.getRoomBlockTimeDao();
    }

    public Single<List<BlockTime>> getByDate(LocalDate date, Long tenant) {
        return dao.getAll(tenant, date)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Long>> insertAll(List<BlockTime> blockTimes) {
        return dao.insertAll(blockTimes)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable delete(BlockTime blockTime) {
        return dao.delete(blockTime)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable update(BlockTime blockTime){
        return dao.update(blockTime)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

}
