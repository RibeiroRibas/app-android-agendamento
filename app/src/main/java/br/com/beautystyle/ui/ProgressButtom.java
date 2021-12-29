package br.com.beautystyle.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.model.Client;

public class ProgressButtom {

    private View progressbar;
    private TextView textView;
    private Context context;

    public ProgressButtom(View view, Context context) {
        progressbar = view.findViewById(R.id.custom_loading_progress_bar);
        textView = view.findViewById(R.id.custom_loading_text_view);
        this.context = context;
    }

    public void buttonActivated(){
        progressbar.setVisibility(View.VISIBLE);
        textView.setText("Importando...");
    }
    @SuppressLint("Range")
    public List<Client> getContactList() {
        List<Client> contactList = new ArrayList<>();
        ListClientView listClientView = new ListClientView();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = context.getApplicationContext().getContentResolver().query(uri,null,null,null);
        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                @SuppressLint("Range") String id =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";
                Cursor phoneCursor = context.getApplicationContext().getContentResolver().query(uriPhone,null,selection,new String[]{id},null);
                @SuppressLint("Range") String number = null;
                if(phoneCursor.moveToNext()){
                    number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                Client client = new Client(name,number);
                if(!listClientView.checkContactList(client))
                    contactList.add(client);
                phoneCursor.close();
            }
        }
        cursor.close();
        buttonFinished();
        return contactList;
    }
    public void buttonFinished(){
        progressbar.setVisibility(View.GONE);
        textView.setText("Importar");
    }
}
