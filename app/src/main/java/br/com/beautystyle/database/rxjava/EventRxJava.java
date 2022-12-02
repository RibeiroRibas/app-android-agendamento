package br.com.beautystyle.database.rxjava;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomEventDao;
import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.util.CalendarUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EventRxJava {
    private final RoomEventDao dao;

    @Inject
    public EventRxJava(BeautyStyleDatabase database) {
        dao = database.getRoomEventDao();
    }

    public Single<List<EventWithClientAndJobs>> getByDate(Long tenant) {
        return dao.getByDate(CalendarUtil.selectedDate, tenant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<Long> insert(Event event) {
        return dao.insert(event)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable update(Event event) {
        return dao.update(event)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Long>> insertAll(List<Event> events) {
        return dao.insertAll(events)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable delete(Event event) {
        return dao.delete(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateAll(List<Event> events) {
        return dao.updateAll(events)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<EventWithClientAndJobs>> getByPeriod(LocalDate startDate,
                                                            LocalDate endDate,
                                                            Long tenant) {
        return dao.getByPeriod(startDate, endDate, tenant)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<LocalDate>> getYearsList(Long tenant) {
        return dao.getYearsList(tenant)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
