package br.com.beautystyle.repository;

import static android.content.ContentValues.TAG;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.database.retrofit.callback.CallBackReturn;
import br.com.beautystyle.database.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.database.retrofit.service.ClientService;
import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomClientDao;
import br.com.beautystyle.model.entity.Client;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class ClientRepository {

    private final RoomClientDao dao;
    @Inject
    ClientService service;
    private final String token;
    private final Long tenant;

    @Inject
    public ClientRepository(BeautyStyleDatabase database, SharedPreferences preferences) {
        dao = database.getRoomClientDao();
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public Single<List<Client>> getAllClientsOnRoom() {
        return dao.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Call<List<Client>> getClientListFromApi(ResultsCallBack<List<Client>> callBack) {
        Call<List<Client>> callClientList = service.getAllByCompanyId(tenant, token);
        callClientList.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<List<Client>>() {
                    @Override
                    public void onSuccess(List<Client> clientList) {
                        callBack.onSuccess(clientList);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }));
        return callClientList;
    }

    public void insertClientOnApi(Client client, ResultsCallBack<Client> callBack) {
        client.setCompanyId(tenant);
        Call<Client> callNewClient = service.insert(client, token);
        callNewClient.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<Client>() {
                    @Override
                    public void onSuccess(Client client) {
                        callBack.onSuccess(client);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }
        ));
    }

    public Single<Long> insertClientOnRoom(Client client) {
        client.setClientId(null);
        return dao.insert(client)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public void updateClientOnApi(Client client, ResultsCallBack<Void> callBack) {
        Call<Void> callUpdateClient = service.update(client, token);
        callUpdateClient.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callBack.onSuccess(null);
            }

            @Override
            public void onError(String erro) {

            }
        }));
    }

    public Completable updateClientOnRoom(Client client) {
        return dao.update(client)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public void deleteClientOnApi(Client client, ResultsCallBack<Void> callBack) {
        Call<Void> callDeletedClient = service.delete(client.getApiId(), token);
        callDeletedClient.enqueue(new CallBackWithoutReturn(
                        new CallBackWithoutReturn.CallBackResponse() {
                            @Override
                            public void onSuccess() {
                                callBack.onSuccess(null);
                            }

                            @Override
                            public void onError(String erro) {
                                callBack.onError(erro);
                            }
                        }
                )
        );
    }


    @SuppressLint("Range")
    public Single<List<Client>> getContactListFromSmartphone(Context context) {
        return dao.getAll()
                .map(clientList -> {
                    List<Client> contactList = new ArrayList<>();
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

                            Client contact = new Client(name, number, tenant);

                            if (contact.isNewContact(clientList)) {
                                contactList.add(contact);
                            }

                            phoneCursor.close();
                        }
                    }
                    cursor.close();
                    return contactList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Cursor getPhoneCursor(Context context, String id) {
        Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
        return context.getContentResolver()
                .query(uriPhone, null, selection, new String[]{id}, null);
    }

    public void insertAllOnApi(List<Client> contactList, ResultsCallBack<List<Client>> callBack) {
        Call<List<Client>> callClientList = service.insertAll(contactList, token);
        callClientList.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<List<Client>>() {
                    @Override
                    public void onSuccess(List<Client> clients) {
                        callBack.onSuccess(clients);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }
        ));
    }

    public Single<List<Long>> insertAllOnRoom(List<Client> response) {
        return dao.insertAll(response)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<Long> insert(Client client) {
        return dao.insert(client)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable update(Client client) {
        return dao.update(client)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateAllOnRoom(List<Client> updatedClientList) {
        return dao.updateAll(updatedClientList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void updateClients(List<Client> clientsFromApi,
                              ResultsCallBack<List<Client>> callBack) {
        getAllClientsOnRoom()
                .doOnSuccess(clientsFromRoom -> {
                    mergeClientId(clientsFromRoom, clientsFromApi);
                    List<Client> clientsToUpdate = getClientsToUpdate(clientsFromApi);
                    updateAllOnRoom(clientsToUpdate)
                            .doOnComplete(() -> {
                                List<Client> newClients = getClientsToInsert(clientsFromApi);
                                if (newClients.isEmpty()) {
                                    callBack.onSuccess(clientsFromApi);
                                } else {
                                    newClients.forEach(client -> client.setClientId(null));
                                    insertAllOnRoom(newClients)
                                            .doOnSuccess(ids -> {
                                                setClientIds(newClients, ids);
                                                mergeClientId(newClients, clientsFromApi);
                                                callBack.onSuccess(clientsFromApi);
                                            }).subscribe();
                                }
                            }).subscribe();
                }).subscribe();
    }

    private void setClientIds(List<Client> newClients, List<Long> ids) {
        for (int i = 0; i < newClients.size(); i++) {
            newClients.get(i).setClientId(ids.get(i));
        }
    }

    @NonNull
    private List<Client> getClientsToInsert(List<Client> clientsFromApi) {
        return clientsFromApi.stream()
                .filter(client -> !client.checkId())
                .filter(Client::checkApiId)
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Client> getClientsToUpdate(List<Client> clientsFromApi) {
        return clientsFromApi.stream()
                .filter(Client::checkId)
                .collect(Collectors.toList());
    }

    private void mergeClientId(List<Client> clientsFromRoom, List<Client> clientsFromApi) {
        clientsFromApi.forEach((clientApi ->
                clientsFromRoom.forEach(clientRoom -> {
                    try {
                        if (clientApi.getApiId().equals(clientRoom.getApiId())) {
                            clientApi.setClientId(clientRoom.getClientId());
                        }
                    } catch (Exception erro) {
                        Log.i(TAG, "eventApiId Null: " + erro);
                    }
                })
        ));
    }

    public Completable deleteClient(Client client) {
        return dao.delete(client)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
