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

import br.com.beautystyle.database.rxjava.CostumerRxJava;
import br.com.beautystyle.model.entity.Customer;
import br.com.beautystyle.retrofit.webclient.CostumerWebClient;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ClientRepository {

    @Inject
    CostumerRxJava dao;
    @Inject
    CostumerWebClient webClient;
    private final MutableLiveData<Resource<List<Customer>>> mutableCostumers = new MutableLiveData<>();
    private final String profile;
    private final Long tenant;

    @Inject
    public ClientRepository(SharedPreferences preferences) {
        profile = preferences.getString(PROFILE_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public LiveData<Resource<List<Customer>>> getAllLiveData() {
        getAllFromRoomObservable();
        if (isUserPremium()) {
            getAllFromApi();
        }
        return mutableCostumers;
    }

    private void getAllFromRoomObservable() {
        dao.getAllObservable(tenant).doOnNext(costumersFromRoom -> {
            Resource<List<Customer>> resource = new Resource<>(costumersFromRoom, null);
            mutableCostumers.setValue(resource);
        }).subscribe();
    }

    private void getAllFromApi() {
        webClient.getAll(new ResultsCallBack<List<Customer>>() {
            @Override
            public void onSuccess(List<Customer> costumersFromApi) {
                updateAndInsertAll(costumersFromApi, null);
            }

            @Override
            public void onError(String error) {
                mutableCostumers.setValue(new Resource<>(null, error));
            }
        });
    }

    public void update(Customer customer) {
        if (isUserPremium()) {
            updateOnApi(customer);
        }
        if (isFreeUser()) {
            updateOnRoom(customer);
        }
    }

    private void updateOnRoom(Customer customer) {
        dao.update(customer).subscribe();
    }

    private void updateOnApi(Customer customer) {
        webClient.update(customer, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateOnRoom(customer);
            }

            @Override
            public void onError(String error) {
                mutableCostumers.setValue(new Resource<>(null, error));
            }
        });
    }

    public void delete(Customer customer) {
        if (isUserPremium()) {
            deleteOnApi(customer);
        }
        if (isFreeUser()) {
            deleteOnRoom(customer);
        }
    }

    private void deleteOnRoom(Customer customer) {
        dao.delete(customer).subscribe();
    }

    private void deleteOnApi(Customer customer) {
        webClient.delete(customer, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleteOnRoom(customer);
            }

            @Override
            public void onError(String error) {
                mutableCostumers.setValue(new Resource<>(null, error));
            }
        });
    }

    public void insertAll(List<Customer> contactList) {
        if (isUserPremium()) insertAllOnApi(contactList);

        if (isFreeUser()) {
            contactList.forEach(costumer -> costumer.setTenant(tenant));
            insertAllOnRoom(contactList);
        }
    }

    private void insertAllOnRoom(List<Customer> customers) {
        dao.insertAll(customers).subscribe();
    }

    private void insertAllOnApi(List<Customer> contactList) {
        webClient.insertAll(contactList, new ResultsCallBack<List<Customer>>() {
            @Override
            public void onSuccess(List<Customer> customers) {
                insertAllOnRoom(customers);
            }

            @Override
            public void onError(String error) {
                mutableCostumers.setValue(new Resource<>(null, error));
            }
        });
    }


    private void deleteFromRoomIfNotExistOnApi(List<Customer> costumersFromApi,
                                               List<Customer> costumersFromRoom) {
        costumersFromRoom.forEach(fromRoom -> {
            if (fromRoom.isNotExistOnApi(costumersFromApi))
                dao.delete(fromRoom).subscribe();
        });
    }

    public LiveData<List<Customer>> getContactListFromSmartphone(Context context) {
        MutableLiveData<List<Customer>> mutableContactList = new MutableLiveData<>();
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
    private Single<List<Customer>> getNewContacts(Context context,
                                                  List<Customer> costumersFromRoom) {
        return Single.create(emitter -> {
            Thread.sleep(5000);
            List<Customer> contactList = new ArrayList<>();
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

                    Customer contact = new Customer(name, number);

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

    public void updateAndInsertAll(List<Customer> clientsFromApi,
                                   ResultsCallBack<List<Customer>> callBack) {
        dao.getAll(tenant)
                .doOnSuccess(clientsFromRoom -> {
                    if (callBack == null)
                        deleteFromRoomIfNotExistOnApi(clientsFromApi, clientsFromRoom);
                    mergeClientId(clientsFromRoom, clientsFromApi);
                    List<Customer> clientsToUpdate = getClientsToUpdate(clientsFromApi);
                    dao.updateAll(clientsToUpdate)
                            .doOnComplete(() -> {
                                List<Customer> newCustomers = getClientsToInsert(clientsFromApi);
                                newCustomers.forEach(client -> client.setId(null));
                                dao.insertAll(newCustomers)
                                        .doOnSuccess(ids -> {
                                            if (callBack != null) {
                                                setClientIds(newCustomers, ids);
                                                mergeClientId(newCustomers, clientsFromApi);
                                                callBack.onSuccess(clientsFromApi);
                                            }
                                        }).subscribe();
                            }).subscribe();
                }).subscribe();
    }

    private void setClientIds(List<Customer> newCustomers, List<Long> ids) {
        for (int i = 0; i < newCustomers.size(); i++) {
            newCustomers.get(i).setId(ids.get(i));
        }
    }

    @NonNull
    private List<Customer> getClientsToInsert(List<Customer> clientsFromApi) {
        return clientsFromApi.stream()
                .filter(client -> !client.isIdNotNull())
                .filter(Customer::checkApiId)
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Customer> getClientsToUpdate(List<Customer> clientsFromApi) {
        return clientsFromApi.stream()
                .filter(Customer::isIdNotNull)
                .collect(Collectors.toList());
    }

    private void mergeClientId(List<Customer> clientsFromRoom, List<Customer> clientsFromApi) {
        clientsFromApi.forEach((clientApi -> {
            for (Customer customerRoom : clientsFromRoom) {
                if (clientApi.isApiIdEquals(customerRoom.getApiId())) {
                    clientApi.setId(customerRoom.getId());
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

    public void insert(Customer customer) {
        if (isFreeUser()) {
            customer.setTenant(tenant);
            insertOnRoom(customer);
        }
        if (isUserPremium()) insertOnApi(customer);
    }

    private void insertOnApi(Customer customer) {
        webClient.insert(customer, new ResultsCallBack<Customer>() {
            @Override
            public void onSuccess(Customer customer) {
                insertOnRoom(customer);
            }

            @Override
            public void onError(String error) {
                mutableCostumers.setValue(new Resource<>(null, error));
            }
        });
    }

    private void insertOnRoom(Customer customer) {
        dao.insert(customer).subscribe();
    }
}
