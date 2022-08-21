package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.FREE_ACCOUNT;
import static br.com.beautystyle.repository.ConstantsRepository.PROFILE_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.USER_PREMIUM;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.database.rxjavaassinc.CostumerAsynchDao;
import br.com.beautystyle.model.entity.Costumer;
import br.com.beautystyle.retrofit.webclient.CostumerWebClient;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ClientRepository {

    @Inject
    CostumerAsynchDao dao;
    @Inject
    CostumerWebClient webClient;
    private final MutableLiveData<Resource<List<Costumer>>> mutableCostumers = new MutableLiveData<>();
    private final String profile;
    private final Long tenant;

    @Inject
    public ClientRepository(SharedPreferences preferences) {
        profile = preferences.getString(PROFILE_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public LiveData<Resource<List<Costumer>>> getAllLiveData() {
        getAllFromRoomObservable();
        if(isUserPremium()){
            getAllFromApi();
        }
        return mutableCostumers;
    }

    private void getAllFromRoomObservable() {
        dao.getAllObservable(tenant).doOnNext(costumersFromRoom -> {
                    Resource<List<Costumer>> resource = new Resource<>(costumersFromRoom, null);
                    mutableCostumers.setValue(resource);
                }).subscribe();
    }

    private void getAllFromApi() {
            webClient.getAll(new ResultsCallBack<List<Costumer>>() {
                @Override
                public void onSuccess(List<Costumer> costumersFromApi) {
                    updateAndInsertAll(costumersFromApi, null);
                }

                @Override
                public void onError(String error) {
                    mutableCostumers.setValue(new Resource<>(null, error));
                }
            });
    }


    public void update(Costumer costumer) {
        if(isUserPremium()) {
            updateOnApi(costumer);
        }
        if(isFreeUser()){
            updateOnRoom(costumer);
        }
    }

    private void updateOnRoom(Costumer costumer) {
        dao.update(costumer).subscribe();
    }

    private void updateOnApi(Costumer costumer) {
        webClient.update(costumer, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
               updateOnRoom(costumer);
            }

            @Override
            public void onError(String error) {
                mutableCostumers.setValue(new Resource<>(null, error));
            }
        });
    }

    public void delete(Costumer costumer) {
        if(isUserPremium()){
            deleteOnApi(costumer);
        }
        if(isFreeUser()){
            deleteOnRoom(costumer);
        }
    }

    private void deleteOnRoom(Costumer costumer) {
        dao.delete(costumer).subscribe();
    }

    private void deleteOnApi(Costumer costumer) {
        webClient.delete(costumer, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleteOnRoom(costumer);
            }

            @Override
            public void onError(String error) {
                mutableCostumers.setValue(new Resource<>(null, error));
            }
        });
    }

    public void insertAll(List<Costumer> contactList) {
        if(isUserPremium()){
            insertAllOnApi(contactList);
        }
        if(isFreeUser()){
            insertAllOnRoom(contactList);
        }
    }

    private void insertAllOnRoom(List<Costumer> costumers) {
        dao.insertAll(costumers).subscribe();
    }

    private void insertAllOnApi(List<Costumer> contactList) {
        webClient.insertAll(contactList, new ResultsCallBack<List<Costumer>>() {
            @Override
            public void onSuccess(List<Costumer> costumers) {
                costumers.forEach(costumer -> costumer.setClientId(null));
                insertAllOnRoom(costumers);
            }

            @Override
            public void onError(String error) {
                mutableCostumers.setValue(new Resource<>(null, error));
            }
        });
    }


    private void deleteFromRoomIfNotExistOnApi(List<Costumer> costumersFromApi,
                                               List<Costumer> costumersFromRoom) {
        costumersFromRoom.forEach(fromRoom -> {
            if (fromRoom.isNotExistOnApi(costumersFromApi))
                dao.delete(fromRoom).subscribe();
        });
    }

    public LiveData<List<Costumer>> getContactListFromSmartphone(Context context) {
        MutableLiveData<List<Costumer>> mutableContactList = new MutableLiveData<>();
        dao.getAll(tenant)
                .doOnSuccess(costumersFromRoom ->
                        getNewContacts(context, costumersFromRoom)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .doOnSuccess(mutableContactList::setValue)
                                .subscribe()
                ).subscribe();
        return mutableContactList;
    }

    @SuppressLint("Range")
    private Single<List<Costumer>> getNewContacts(Context context,
                                                  List<Costumer> costumersFromRoom) {
        return Single.create(emitter -> {
            Thread.sleep(5000);
            List<Costumer> contactList = new ArrayList<>();
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(uri, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    Cursor phoneCursor = getPhoneCursor(context, id);

                    String number = "";

                    if (phoneCursor.moveToNext()) {
                        number = phoneCursor.getString(
                                phoneCursor.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                        );
                    }

                    Costumer contact = new Costumer(name, number);

                    if (contact.isNewContact(costumersFromRoom)) {
                        contactList.add(contact);
                    }

                    phoneCursor.close();
                }
            }
            cursor.close();
            emitter.onSuccess(contactList);
        });
    }

    private Cursor getPhoneCursor(Context context, String id) {
        Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
        return context.getContentResolver()
                .query(uriPhone, null, selection, new String[]{id}, null);
    }

    public void updateAndInsertAll(List<Costumer> clientsFromApi,
                                   ResultsCallBack<List<Costumer>> callBack) {
        dao.getAll(tenant)
                .doOnSuccess(clientsFromRoom -> {
                    if (callBack == null)
                        deleteFromRoomIfNotExistOnApi(clientsFromApi, clientsFromRoom);
                    mergeClientId(clientsFromRoom, clientsFromApi);
                    List<Costumer> clientsToUpdate = getClientsToUpdate(clientsFromApi);
                    dao.updateAll(clientsToUpdate)
                            .doOnComplete(() -> {
                                List<Costumer> newCostumers = getClientsToInsert(clientsFromApi);
                                newCostumers.forEach(client -> client.setClientId(null));
                                dao.insertAll(newCostumers)
                                        .doOnSuccess(ids -> {
                                            if (callBack != null) {
                                                setClientIds(newCostumers, ids);
                                                mergeClientId(newCostumers, clientsFromApi);

                                                callBack.onSuccess(clientsFromApi);
                                            }
                                        }).subscribe();
                            }).subscribe();
                }).subscribe();
    }

    private void setClientIds(List<Costumer> newCostumers, List<Long> ids) {
        for (int i = 0; i < newCostumers.size(); i++) {
            newCostumers.get(i).setClientId(ids.get(i));
        }
    }

    @NonNull
    private List<Costumer> getClientsToInsert(List<Costumer> clientsFromApi) {
        return clientsFromApi.stream()
                .filter(client -> !client.checkId())
                .filter(Costumer::checkApiId)
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Costumer> getClientsToUpdate(List<Costumer> clientsFromApi) {
        return clientsFromApi.stream()
                .filter(Costumer::checkId)
                .collect(Collectors.toList());
    }

    private void mergeClientId(List<Costumer> clientsFromRoom, List<Costumer> clientsFromApi) {
        clientsFromApi.forEach((clientApi -> {
            for (Costumer costumerRoom : clientsFromRoom) {
                if (clientApi.getApiId().equals(costumerRoom.getApiId())) {
                    clientApi.setClientId(costumerRoom.getClientId());
                    break;
                }
            }
        }
        ));
    }
    private boolean isFreeUser() {
        return profile.equals(FREE_ACCOUNT);
    }

    private boolean isUserPremium() {
        return profile.equals(USER_PREMIUM);
    }

    public void insert(Costumer costumer) {
        if(isFreeUser()){
            insertOnRoom(costumer);
        }
        if(isUserPremium()){
            insertOnApi(costumer);
        }
    }

    private void insertOnApi(Costumer costumer) {
        webClient.insert(costumer, new ResultsCallBack<Costumer>() {
            @Override
            public void onSuccess(Costumer costumer) {
                insertOnRoom(costumer);
            }

            @Override
            public void onError(String error) {
                mutableCostumers.setValue(new Resource<>(null, error));
            }
        });
    }

    private void insertOnRoom(Costumer costumer) {
        costumer.setClientId(null);
        costumer.setCompanyId(tenant);
        dao.insert(costumer).subscribe();
    }
}
