package br.com.beautystyle.ui.fragment.client;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_INSERT_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_UPDATE_CLIENT;

import android.Manifest;
import android.content.Context;
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

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.model.entity.Client;
import br.com.beautystyle.repository.ClientRepository;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.ui.ProgressButtom;
import br.com.beautystyle.ui.adapter.recyclerview.ClientListAdapter;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;

public class ClientListFragment extends Fragment {

    private EventViewModel eventViewModel;
    private View inflateDView;
    private final ActivityResultLauncher<String> requestPermissionLauncher = getPermission();
    private static final String NO_PERMISSION = "Permissão Negada";
    @Inject
    EventRepository eventRepository;
    @Inject
    ClientRepository clientRepository;
    private ClientListAdapter adapter;
    private Disposable disposable;
    private ProgressButtom progressButtom;
    private Call<List<Client>> callBack;

    @NonNull
    private ActivityResultLauncher<String> getPermission() {
        return this.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        getContactListFromSmartphone();
                    } else {
                        Toast.makeText(requireActivity(), NO_PERMISSION, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        ((BeautyStyleApplication) requireActivity().getApplicationContext())
                .applicationComponent.injectClientListFrag(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
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
        int adapterPosition = item.getGroupId();
        if (isDeleteClientMenu(item.getItemId())) {
            Client client = adapter.getClientAtPosition(adapterPosition);
            checkRemoveAlertDialog(client, adapterPosition);
        } else if (isUpdateClientMenu(item.getItemId())) { // update client menu
            Client client = adapter.getClientAtPosition(adapterPosition);
            Bundle bundle = createBundle(client, adapterPosition);
            showNewClientFragmentUpdateMode(bundle);
        }
        return super.onContextItemSelected(item);
    }

    private boolean isUpdateClientMenu(int itemId) {
        return itemId == 1;
    }

    private boolean isDeleteClientMenu(int itemId) {
        return itemId == 2;
    }

    private Bundle createBundle(Client selectedClient, int adapterPosition) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_UPDATE_CLIENT, selectedClient);
        bundle.putInt(KEY_POSITION, adapterPosition);
        return bundle;
    }

    private void showNewClientFragmentUpdateMode(Bundle bundle) {
        NewClientFragment newClientFragment = new NewClientFragment();
        newClientFragment.setArguments(bundle);
        newClientFragment.show(getChildFragmentManager(), TAG_UPDATE_CLIENT);
    }

    private void checkRemoveAlertDialog(Client clientAtPosition, int position) {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Removendo Cliente")
                .setMessage("Tem Certeza que deseja remover esse item?")
                .setPositiveButton("Sim",
                        (dialog, which) -> deleteClient(clientAtPosition, position)
                )
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteClient(Client client, int position) {
        clientRepository.deleteClientOnApi(client, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleteClientOnRoom(client, position);
            }

            @Override
            public void onError(String error) {
                showError(error);
            }
        });
    }

    private void deleteClientOnRoom(Client client, int position) {
        clientRepository.deleteClient(client)
                .doOnComplete(() ->
                        eventRepository.getEventsByClientId(client.getClientId())
                                .doOnSuccess(events -> {
                                    events.forEach(event -> event.setClientCreatorId(0L));
                                    eventRepository.updateOnRoom(events)
                                            .doOnComplete(() ->
                                                    adapter.publishResultsRemoved(client, position)
                                            )
                                            .subscribe();
                                }).subscribe()
                ).subscribe();
    }

    private void setAdapterClient() {
        RecyclerView listClient = inflateDView.findViewById(R.id.fragment_list_cliente_rv);
        listClient.setAdapter(adapter);
        registerForContextMenu(listClient);
    }

    private void getAllClients() {
        clientRepository.getAllClientsOnRoom()
                .doOnSuccess(clients -> {
                    adapter.publishClientList(clients);
                    getClientListFromApi();
                }).subscribe();
    }

    private void getClientListFromApi() {
        callBack = clientRepository.getClientListFromApi(new ResultsCallBack<List<Client>>() {
            @Override
            public void onSuccess(List<Client> clients) {
                clientRepository.updateClients(clients,
                        new ResultsCallBack<List<Client>>() {
                            @Override
                            public void onSuccess(List<Client> clients) {
                                adapter.publishClientList(clients);
                            }

                            @Override
                            public void onError(String erro) {
                                showError(erro);
                            }
                        }
                );
            }

            @Override
            public void onError(String erro) {
                showError(erro);
            }
        });
    }

    private void showError(String message) {
        if (this.getActivity() != null)
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
        adapter.setOnItemClickListener((
                (client) -> {
                    eventViewModel.add(client);
                    removeThisFragment();
                }
        ));
    }

    private void removeThisFragment() {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }

    private void setNewClientListener() {
        ImageView addClient = inflateDView.findViewById(R.id.fragment_list_cliente_btn_add);
        addClient.setOnClickListener(v -> showNewClientFragment());
    }

    private void showNewClientFragment() {
        NewClientFragment newClientFragment = new NewClientFragment();
        newClientFragment.show(getChildFragmentManager(), TAG_INSERT_CLIENT);
    }

    private void importContactListener() {
        View importContactList = inflateDView.findViewById(R.id.fragment_list_client_import);
        importContactList.setOnClickListener(this::importClients);
    }

    private void importClients(View v) {
        progressButtom = new ProgressButtom(v);
        progressButtom.buttonActivated();
        if (checkPermission()) {
            getContactListFromSmartphone();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void getContactListFromSmartphone() {
        disposable = clientRepository.getContactListFromSmartphone(requireActivity())
                .doOnSuccess(contactList -> {
                    progressButtom.buttonFinished();
                    showDialogContactlist(contactList);
                }).subscribe();
    }

    private void showDialogContactlist(List<Client> contactList) {
        if (contactList.isEmpty()) {
            showAlertDiologEmptyResults();
        } else {
            showAlertDialogFindResults(contactList);
        }

    }

    private void showAlertDialogFindResults(List<Client> contactList) {
        new AlertDialog
                .Builder(requireActivity())
                .setMessage("Foram encontrados " +
                        contactList.size() +
                        " contatos no seu smartphone! Deseja importar todos para sua Agenda?")
                .setPositiveButton("Sim",
                        (dialog, which) -> saveAllImportedClients(contactList)
                )
                .setNegativeButton("Não", null)
                .show();
    }

    private void showAlertDiologEmptyResults() {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Nenhum contato novo encontrato")
                .setMessage("Você já adicionou todos os contatos do seu smartphone para sua agenda.")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void saveAllImportedClients(List<Client> contactList) {
        clientRepository.insertAllOnApi(contactList, new ResultsCallBack<List<Client>>() {
            @Override
            public void onSuccess(List<Client> clients) {
                insertAllClientsOnRoom(clients);
            }

            @Override
            public void onError(String erro) {
                showError(erro);
            }
        });

    }

    private void insertAllClientsOnRoom(List<Client> clients) {
        clients.forEach(client -> client.setClientId(null));
        clientRepository.insertAllOnRoom(clients)
                .doOnSuccess(ids -> {
                            clients.forEach(client -> ids.forEach(client::setClientId));
                            adapter.publishContactList(clients);
                        }
                ).subscribe();
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(
                KEY_CLIENT, this, (requestKey, result) -> {
                    isNewClient(result);
                    isUpdateClient(result);
                }
        );
    }

    private void isNewClient(Bundle result) {
        if (result.containsKey(KEY_INSERT_CLIENT)) {
            Client client = (Client) result.getSerializable(KEY_INSERT_CLIENT);
            insertClient(client);
        }
    }

    private void isUpdateClient(Bundle result) {
        if (result.containsKey(KEY_UPDATE_CLIENT)) {
            Client client = (Client) result.getSerializable(KEY_UPDATE_CLIENT);
            int position = result.getInt(KEY_POSITION);
            updateClient(client, position);
        }
    }

    private void insertClient(Client newClient) {
        clientRepository.insertClientOnApi(newClient, new ResultsCallBack<Client>() {
            @Override
            public void onSuccess(Client client) {
                insertClientOnRoom(client);
            }

            @Override
            public void onError(String erro) {
                showError(erro);
            }
        });
    }

    private void insertClientOnRoom(Client client) {
        clientRepository.insertClientOnRoom(client)
                .doOnSuccess(clientId -> {
                    client.setClientId(clientId);
                    adapter.publishResultsInsert(client);
                    eventViewModel.add(client);
                    removeThisFragment();
                }).subscribe();
    }

    private void updateClient(Client client, int position) {
        clientRepository.updateClientOnApi(client, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateClientOnRoom(client, position);
            }

            @Override
            public void onError(String erro) {
                showError(erro);
            }
        });
    }

    private void updateClientOnRoom(Client client, int position) {
        clientRepository.updateClientOnRoom(client)
                .doOnComplete(() -> adapter.publishResultsUpdate(client, position))
                .subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
        if (callBack != null)
            callBack.cancel();
    }

}
