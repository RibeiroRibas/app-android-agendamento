package br.com.beautystyle.ui;

import android.content.Context;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;

import br.com.beautystyle.dao.ServiceDao;
import br.com.beautystyle.model.Services;
import br.com.beautystyle.ui.fragment.NewServiceFragment;
import br.com.beautystyle.ui.adapter.listview.ServiceListAdapter;

public class ListServiceView {
    private final ServiceListAdapter adapter;
    private final ServiceDao dao;
    private final Context context;

    public ListServiceView(Context context) {
        this.context = context;
        this.adapter = new ServiceListAdapter(context);
        this.dao = new ServiceDao();
    }

    public ServiceListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ListView listService) {
        listService.setAdapter(adapter);
    }
    public void save(Services service){
        dao.save(service);
        adapter.save(service);
    }
    public void updateService() {
        adapter.update(dao.listAll());
    }
    public void edit(Services service){
        dao.edit(service);
        adapter.update(dao.listAll());
    }

    public void edit(MenuItem item, FragmentActivity listServicefragment) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Services selectedService = (Services) adapter.getItem(menuInfo.position);
        NewServiceFragment newServiceFragment = new NewServiceFragment(this,selectedService);
        newServiceFragment.show(listServicefragment.getSupportFragmentManager(), "EditServiceFragment");
    }

    public void checkRemove(MenuItem item) {
        new AlertDialog
                .Builder(context)
                .setTitle("Removendo Serviço")
                .setMessage("Tem Certeza que deseja remover esse item?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    AdapterView.AdapterContextMenuInfo menuInfo =
                            (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    Services service = (Services) adapter.getItem(menuInfo.position);
                    remove(service);
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void remove(Services service) {
        dao.remove(service);
        adapter.remove(service);
    }

    @NonNull
    public SearchView.OnQueryTextListener getServiceListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        };
    }
}
