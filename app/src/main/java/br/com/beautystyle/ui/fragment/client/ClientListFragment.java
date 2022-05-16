package br.com.beautystyle.ui.fragment.client;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_INSERT_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_UPDATE_CLIENT;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.util.List;

import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.model.entities.Client;
import br.com.beautystyle.repository.ClientRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.ui.ProgressButtom;
import br.com.beautystyle.ui.adapter.recyclerview.ClientListAdapter;
import io.reactivex.rxjava3.disposables.Disposable;

public class ClientListFragment extends Fragment {

    private EventViewModel eventViewModel;
    private View inflateDView;
    private final ActivityResultLauncher<String> requestPermissionLauncher = getPermission();
    private static final String NO_PERMISSION = "Permissão Negada";
    private ClientRepository repository;
    private ClientListAdapter adapter;
    private Disposable disposable;
    private ProgressButtom progressButtom;

    @NonNull
    private ActivityResultLauncher<String> getPermission() {
        return this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                getContactListFromSmartphone();
            } else {
                Toast.makeText(requireActivity(), NO_PERMISSION, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        repository = new ClientRepository(requireActivity());
        adapter = new ClientListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflateDView = inflater.inflate(R.layout.fragment_list_client, container, false);

        setAdapterClient();
        getAllClients();
        setSearchViewClient();

       //LISTENER
        adapterClientListener();
        setNewClientListener();
        importContactListener();
        setFragmentResultListener();

        return inflateDView;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Client clientAtPosition = adapter.getClientAtPosition(item.getGroupId());
        int position = item.getGroupId();
        if (item.getItemId() == 1) {//edit client
            repository.getById(clientAtPosition.getClientId(), new ResultsCallBack<Client>() {
                @Override
                public void onSuccess(Client client) {
                    Bundle bundle = createBundle(client, position);
                    showNewClientFragmentEditMode(bundle);
                }

                @Override
                public void onError(String erro) {
                    showError(erro);
                }
            });
        } else if (item.getItemId() == 2) {// remove client
            checkRemoveAlertDialog(clientAtPosition, position);
        }
        return super.onContextItemSelected(item);
    }

    private Bundle createBundle(Client selectedClient, int id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_UPDATE_CLIENT, selectedClient);
        bundle.putInt(KEY_POSITION, id);
        return bundle;
    }

    private void showNewClientFragmentEditMode(Bundle bundle) {
        NewClientFragment newClientFragment = new NewClientFragment();
        newClientFragment.setArguments(bundle);
        newClientFragment.show(getChildFragmentManager(), TAG_UPDATE_CLIENT);
    }

    private void checkRemoveAlertDialog(Client clientAtPosition, int position) {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Removendo Cliente")
                .setMessage("Tem Certeza que deseja remover esse item?")
                .setPositiveButton("Sim", (dialog, which) -> deleteClient(clientAtPosition,position))
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteClient(Client clientAtPosition, int position) {
        repository.delete(clientAtPosition, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                adapter.publishResultsRemoved(clientAtPosition,position);
            }

            @Override
            public void onError(String error) {
                showError(error);
            }
        });
    }

    private void setAdapterClient() {
        RecyclerView listClient = inflateDView.findViewById(R.id.fragment_list_cliente_rv);
        listClient.setAdapter(adapter);
        registerForContextMenu(listClient);
    }

    private void getAllClients() {
        repository.getAllClients(new ResultsCallBack<List<Client>>() {
            @Override
            public void onSuccess(List<Client> clientList) {
                adapter.publishClientList(clientList);
            }
            @Override
            public void onError(String erro) {
                showError("Erro no servidor");
            }
        });

    }

    private void showError(String message) {
        Toast.makeText(requireActivity(),
                message,
                Toast.LENGTH_LONG).show();
    }

    private void setSearchViewClient() {
        SearchView svClient = inflateDView.findViewById(R.id.fragment_list_client_sv);
        svClient.setOnQueryTextListener(getClientListener());
    }

    private SearchView.OnQueryTextListener getClientListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filteredClient(newText);
                return false;
            }
        };
    }

    private void adapterClientListener() {
        adapter.setOnItemClickListener(((client, position) -> {
            eventViewModel.add(client);
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }));
    }

    private void setNewClientListener() {
        ImageView addClient = inflateDView.findViewById(R.id.fragment_list_cliente_btn_add);
        addClient.setOnClickListener(v -> {
            NewClientFragment newClientFragment = new NewClientFragment();
            newClientFragment.show(getChildFragmentManager(), TAG_INSERT_CLIENT);
        });
    }

    private void importContactListener() {
        View importContactList = inflateDView.findViewById(R.id.fragment_list_client_import);
        importContactList.setOnClickListener(v -> {
           progressButtom = new ProgressButtom(v);
            progressButtom.buttonActivated();
            if (checkPermission()) {
                getContactListFromSmartphone();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            }
        });
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void getContactListFromSmartphone() {
        disposable = repository.getContactListFromSmartphone(requireActivity()).doOnSuccess(contactList -> {
            progressButtom.buttonFinished();
            showDialogContactlist(contactList);
        }).subscribe();
    }

    private void showDialogContactlist(List<Client> contactList) {
        if (contactList.size() > 0) {
            new AlertDialog
                    .Builder(requireActivity())
                    .setMessage("Foram encontrados " + contactList.size() + " contatos no seu smartphone! Deseja importar todos para sua Agenda?")
                    .setPositiveButton("Sim", (dialog, which) -> saveAllImportedClients(contactList))
                    .setNegativeButton("Não", null)
                    .show();
        } else {
            new AlertDialog
                    .Builder(requireActivity())
                    .setTitle("Nenhum contato novo encontrato")
                    .setMessage("Você já adicionou todos os contatos do seu smartphone para sua agenda.")
                    .setPositiveButton("Ok", null)
                    .show();
        }

    }

    private void saveAllImportedClients(List<Client> contactList) {
        repository.insertAll(contactList, new ResultsCallBack<List<Client>>() {
            @Override
            public void onSuccess(List<Client> resultado) {
                adapter.publishContactList(resultado);
            }

            @Override
            public void onError(String erro) {
            }
        });

    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(KEY_CLIENT, this, (requestKey, result) -> {
            if (result.containsKey(KEY_UPDATE_CLIENT)) {
                Client client = (Client) result.getSerializable(KEY_UPDATE_CLIENT);
                int position = result.getInt(KEY_POSITION);
                updateClient(client,position);
            } else {
                Client client = (Client) result.getSerializable(KEY_INSERT_CLIENT);
                insertClient(client);
            }
        });
    }

    private void updateClient(Client client, int position) {
        repository.update(client, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                adapter.publishResultsUpdate(client,position);
            }

            @Override
            public void onError(String erro) {
                showError("Não foi possível atualizar os dados do cliente.");
            }
        });
    }

    private void insertClient(Client client) {
            repository.insert(client, new ResultsCallBack<Client>() {
                @Override
                public void onSuccess(Client result) {
                    adapter.publishResultsInsert(result);
                }

                @Override
                public void onError(String erro) {
                    showError("Não foi possivel cadastrar novo cliente");
                }
            });
    }

//    private boolean isConnected() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        if(networkInfo!=null){
//            return networkInfo.isConnected();
//        }
//        return false;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
    }

}
