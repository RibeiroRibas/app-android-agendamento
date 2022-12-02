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

import br.com.beautystyle.database.rxjava.BlockTimeRxJava;
import br.com.beautystyle.model.entity.BlockTime;
import br.com.beautystyle.retrofit.webclient.BlockTimeWebClient;
import br.com.beautystyle.util.CalendarUtil;
import io.reactivex.rxjava3.core.Single;

public class BlockTimeRepository {

    @Inject
    BlockTimeRxJava dao;
    private final Long tenant;
    @Inject
    BlockTimeWebClient webClient;
    private final String profile;


    @Inject
    public BlockTimeRepository(SharedPreferences preferences) {
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
        profile = preferences.getString(PROFILE_SHARED_PREFERENCES, "");
    }

    public Single<List<BlockTime>> getAllByDate() {
        return dao.getByDate(CalendarUtil.selectedDate, tenant);
    }

    public Single<List<Long>> insertAllOnRoom(List<BlockTime> blockTimes) {
        return dao.insertAll(blockTimes);
    }

    public LiveData<Resource<BlockTime>> insert(BlockTime blockTime) {
        MutableLiveData<Resource<BlockTime>> liveData = new MutableLiveData<>();
        webClient.insert(blockTime, new ResultsCallBack<BlockTime>() {
            @Override
            public void onSuccess(BlockTime result) {
                liveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> delete(BlockTime blockTime) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        if (isUserPremium()) {
            deleteOnApi(blockTime, liveData);
        }
        if (isFreeAccount()) {
            deleteOnRoom(blockTime, liveData);
        }
        return liveData;
    }

    private void deleteOnRoom(BlockTime blockTime, MutableLiveData<Resource<Void>> liveData) {
        dao.delete(blockTime).doOnComplete(() ->
                liveData.setValue(new Resource<>(null, null))).subscribe();
    }

    private void deleteOnApi(BlockTime blockTime, MutableLiveData<Resource<Void>> liveData) {
        webClient.delete(blockTime.getApiId(), new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleteOnRoom(blockTime, liveData);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    private boolean isUserPremium() {
        return profile.equals(USER_PREMIUM);
    }

    private boolean isFreeAccount() {
        return profile.equals(FREE_ACCOUNT);
    }

    public void updateAll(List<BlockTime> blockTimesFromApi, ResultsCallBack<List<BlockTime>> callBack) {
        getAllByDate().doOnSuccess(blockTimesFromRoom -> {
            mergeBlockTimeId(blockTimesFromApi, blockTimesFromRoom);
            insertAllOnRoom(blockTimesFromApi).doOnSuccess(ids -> {
                setBlockTimeId(blockTimesFromApi, ids);
                callBack.onSuccess(blockTimesFromApi);
            }).subscribe();
        }).subscribe();
    }

    private void setBlockTimeId(List<BlockTime> blockTimesFromApi,
                                List<Long> ids) {
        for (int i = 0; i < blockTimesFromApi.size(); i++) {
            blockTimesFromApi.get(i).setId(ids.get(i));
        }
    }

    private void mergeBlockTimeId(List<BlockTime> blockTimesFromApi, List<BlockTime> blockTimesFromRoom) {
        blockTimesFromRoom.forEach(fromRoom ->
                blockTimesFromApi.forEach(fromApi -> {
                    if (fromRoom.isApiIdEquals(fromApi.getApiId()))
                        fromApi.setId(fromRoom.getId());
                }));
    }

    public LiveData<Resource<Void>> update(BlockTime blockTime) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        if (isUserPremium()) {
            updateOnApi(blockTime, liveData);
        } else {
            updateOnRoom(blockTime, liveData);
        }
        return liveData;
    }

    private void updateOnApi(BlockTime blockTime, MutableLiveData<Resource<Void>> liveData) {
        webClient.update(blockTime, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateOnRoom(blockTime, liveData);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    private void updateOnRoom(BlockTime blockTime, MutableLiveData<Resource<Void>> liveData) {
        dao.update(blockTime).doOnComplete(() -> {
            liveData.setValue(new Resource<>(null, null));
        }).subscribe();
    }
}
