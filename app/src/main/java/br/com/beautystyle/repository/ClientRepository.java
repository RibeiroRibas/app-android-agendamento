package br.com.beautystyle.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.database.retrofit.BeautyStyleRetrofit;
import br.com.beautystyle.database.retrofit.callback.CallBackReturn;
import br.com.beautystyle.database.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.database.retrofit.service.ClientService;
import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomClientDao;
import br.com.beautystyle.model.entities.Client;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class ClientRepository {

    private final RoomClientDao dao;
    private final ClientService service;

    public ClientRepository(Context context) {
        dao = BeautyStyleDatabase.getInstance(context).getRoomClientDao();
        service = new BeautyStyleRetrofit().getClientService();
    }

    public void getAllClients(ResultsCallBack<List<Client>> callBack) {
        getClientListFromRoom(callBack);
    }

    private void getClientListFromRoom(ResultsCallBack<List<Client>> callBack) {
        dao.getAll().observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(clientList -> {
                    callBack.onSuccess(clientList);
                    getClientListFromApi(callBack, clientList);
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void getClientListFromApi(ResultsCallBack<List<Client>> callBack, List<Client> clientListFromRoom) {
        Call<List<Client>> callClientList = service.getAll();
        callClientList.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Client>>() {
            @Override
            public void onSuccess(List<Client> response) {
                callBack.onSuccess(response);
                insertClientListOnRoom(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    private void insertClientListOnRoom(List<Client> clientList) {
        dao.insertAll(clientList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void insert(Client client, ResultsCallBack<Client> callBack) {
        insertClientOnApi(client, callBack);
    }

    private void insertClientOnApi(Client client, ResultsCallBack<Client> callBack) {
        Call<Client> callNewClient = service.insert(client);
        callNewClient.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Client>() {
            @Override
            public void onSuccess(Client response) {
                insertClientOnRoom(response, callBack);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    private void insertClientOnRoom(Client client, ResultsCallBack<Client> callBack) {
        dao.insert(client)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> callBack.onSuccess(client))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void update(Client client, ResultsCallBack<Void> callBack) {
        updateClientOnApi(client, callBack);
    }

    public void updateClientOnApi(Client client, ResultsCallBack<Void> callBack) {
        Call<Client> callUpdateClient = service.update(client);
        callUpdateClient.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Client>() {
            @Override
            public void onSuccess(Client response) {
                updateClientOnRoom(response, callBack);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void updateClientOnRoom(Client client, ResultsCallBack<Void> callBack) {
        dao.update(client)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> callBack.onSuccess(null))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void delete(Client client, ResultsCallBack<Void> callBack) {
        deleteClientOnApi(client, callBack);
    }

    private void deleteClientOnApi(Client client, ResultsCallBack<Void> callBack) {
        Call<Void> callDeletedClient = service.delete(client.getClientId());
        callDeletedClient.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                deleteClientOnRoom(client, callBack);
            }

            @Override
            public void onError(String erro) {
                callBack.onError(erro);
            }
        }));
    }

    private void deleteClientOnRoom(Client client, ResultsCallBack<Void> callBack) {
        dao.delete(client)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> callBack.onSuccess(null))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    public void getById(Long id, ResultsCallBack<Client> callBack) {
        dao.getById(id)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(callBack::onSuccess)
                .doOnError(throwable -> {
                    callBack.onError(throwable.getMessage());
                    callBack.onSuccess(new Client());
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    @SuppressLint("Range")
    public Single<List<Client>> getContactListFromSmartphone(Context context) {
        return dao.getAll()
                .map(clientList -> {
                    //Thread.sleep(5000);
                    List<Client> contactList = new ArrayList<>();
                    Uri uri = ContactsContract.Contacts.CONTENT_URI;
                    Cursor cursor = context.getContentResolver().query(uri, null, null, null);
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {

                            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                            Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                            String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                            Cursor phoneCursor = context.getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);
                            @SuppressLint("Range") String number = null;

                            if (phoneCursor.moveToNext()) {
                                number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            }
                            Client contactClient = new Client(name, number);
                            if (!contactClient.checkNameAndPhone(clientList)) {
                                contactList.add(contactClient);
                                phoneCursor.close();
                            }
                        }
                    }
                    cursor.close();
                    return contactList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void insertAll(List<Client> contactList, ResultsCallBack<List<Client>> callBack) {
        inserAllOnApi(contactList, callBack);
    }

    private void inserAllOnApi(List<Client> contactList, ResultsCallBack<List<Client>> callBack) {
        Call<List<Client>> callClientList = service.insertAll(contactList);
        callClientList.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Client>>() {
            @Override
            public void onSuccess(List<Client> response) {
                callBack.onSuccess(response);
                insertAllOnRoom(response).subscribe();
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public Completable insertAllOnRoom(List<Client> response) {
        return dao.insertAll(response)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
