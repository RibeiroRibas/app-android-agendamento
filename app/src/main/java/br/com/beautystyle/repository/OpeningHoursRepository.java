package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.rxjava.OpeningHoursRxJava;
import br.com.beautystyle.model.entity.OpeningHours;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class OpeningHoursRepository {

    @Inject
    OpeningHoursRxJava dao;
    private final Long tenant;
    private final MutableLiveData<Resource<List<OpeningHours>>> mutableLiveData = new MutableLiveData<>();

    @Inject
    public OpeningHoursRepository(SharedPreferences preferences) {
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public Single<List<OpeningHours>> getAll() {
       return dao.getAll(tenant);
    }

    public Completable insertAll(List<OpeningHours> openingHours) {
       return  dao.insertAll(openingHours);
    }
}
