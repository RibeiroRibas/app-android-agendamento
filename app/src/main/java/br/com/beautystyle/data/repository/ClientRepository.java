package br.com.beautystyle.data.repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.data.database.BeautyStyleDatabase;
import br.com.beautystyle.data.database.dao.RoomClientDao;
import br.com.beautystyle.model.Client;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ClientRepository {

    private final RoomClientDao dao;

    public ClientRepository(Application application) {
        dao = BeautyStyleDatabase.getInstance(application).getRoomClientDao();
    }

    public Single<Long> insert(Client client) {
        return dao.insert(client)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable update(Client client) {
        return dao.update(client)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Completable delete(Client client) {
        return dao.delete(client)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }


    public Single<Client> getById(int id) {
        return dao.getById(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }


    public Single<List<Client>> getAll() {
        return dao.getAll().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @SuppressLint("Range")
    public Single<List<Client>> getContactListFromSmartphone(Application application) {
        return dao.getAll()
                .map(clientList -> {
                    //Thread.sleep(5000);
                    List<Client> contactList = new ArrayList<>();
                    Uri uri = ContactsContract.Contacts.CONTENT_URI;
                    Cursor cursor = application.getApplicationContext().getContentResolver().query(uri, null, null, null);
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {

                            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                            Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                            String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                            Cursor phoneCursor = application.getApplicationContext().getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);
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

    public Single<Long[]> insertAll(List<Client> contactList) {
        return dao.insertAll(contactList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
