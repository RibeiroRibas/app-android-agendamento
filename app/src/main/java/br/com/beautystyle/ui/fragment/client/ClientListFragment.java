package br.com.beautystyle.ui.fragment.client;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_CLIENT;
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
import android.widget.ProgressBar;
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
import br.com.beautystyle.ViewModel.CostumerViewModel;
import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.ViewModel.factory.CostumerFactory;
import br.com.beautystyle.model.entity.Customer;
import br.com.beautystyle.repository.ClientRepository;
import br.com.beautystyle.ui.ProgressBottom;
import br.com.beautystyle.ui.adapter.recyclerview.ClientListAdapter;

public class ClientListFragment extends Fragment {

    private EventViewModel eventViewModel;
    private CostumerViewModel costumerViewModel;
    private View inflateDView;
    private final ActivityResultLauncher<String> requestPermissionLauncher = getPermission();
    private static final String NO_PERMISSION = "Permissão Negada";
    @Inject
    ClientRepository repository;
    private ClientListAdapter adapter;
    private ProgressBottom progressBottom;

    @NonNull
    private ActivityResultLauncher<String> getPermission() {
        return this.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        progressBottom.buttonActivated();
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
        CostumerFactory factory = new CostumerFactory(repository);
        costumerViewModel = new ViewModelProvider(this, factory).get(CostumerViewModel.class);
        adapter = new ClientListAdapter(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflateDView = inflater.inflate(R.layout.fragment_list_client, container, false);

        setAdapterRecyclerView();

        //LISTENER
        setSearchViewListener();
        adapterRecyclerViewListener();
        setAddCostumerListener();
        importContactListener();
        setFragmentResultListener();

        costumersObserver();
        return inflateDView;
    }

    private void costumersObserver() {
        costumerViewModel.getAllLiveData().observe(requireActivity(), resource -> {
            if (resource.isDataNotNull()) {
                adapter.update(resource.getData());
            } else {
                showError(resource.getError());
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int adapterPosition = item.getGroupId();
        if (isDeleteClientMenu(item.getItemId())) {
            Customer customer = adapter.getClientAtPosition(adapterPosition);
            checkDeleteAlertDialog(customer);
        } else if (isUpdateClientMenu(item.getItemId())) { // update client menu
            Customer customer = adapter.getClientAtPosition(adapterPosition);
            Bundle bundle = createBundle(customer);
            showAddCostumerFragmentUpdateMode(bundle);
        }
        return super.onContextItemSelected(item);
    }

    private boolean isUpdateClientMenu(int itemId) {
        return itemId == 1;
    }

    private boolean isDeleteClientMenu(int itemId) {
        return itemId == 2;
    }

    private Bundle createBundle(Customer selectedCustomer) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_UPDATE_CLIENT, selectedCustomer);
        return bundle;
    }

    private void showAddCostumerFragmentUpdateMode(Bundle bundle) {
        NewClientFragment newClientFragment = new NewClientFragment();
        newClientFragment.setArguments(bundle);
        newClientFragment.show(getChildFragmentManager(), TAG_UPDATE_CLIENT);
    }

    private void checkDeleteAlertDialog(Customer customerAtPosition) {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Removendo Cliente")
                .setMessage("Tem Certeza que deseja remover o item selecionado?")
                .setPositiveButton("Sim",
                        (dialog, which) -> repository.delete(customerAtPosition)
                )
                .setNegativeButton("Não", null)
                .show();
    }

    private void setAdapterRecyclerView() {
        RecyclerView listClient = inflateDView.findViewById(R.id.fragment_list_cliente_rv);
        listClient.setAdapter(adapter);
        registerForContextMenu(listClient);
    }

    private void showError(String message) {
        if (this.getActivity() != null)
            Toast.makeText(requireActivity(),
                    message,
                    Toast.LENGTH_LONG).show();
    }

    private void setSearchViewListener() {
        SearchView svClient = inflateDView.findViewById(R.id.fragment_list_client_sv);
        svClient.setOnQueryTextListener(getCostumerOnTextListener());
    }

    private SearchView.OnQueryTextListener getCostumerOnTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterSearchView(newText);
                return false;
            }
        };
    }

    private void adapterRecyclerViewListener() {
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

    private void setAddCostumerListener() {
        ImageView addClient = inflateDView.findViewById(R.id.fragment_list_cliente_btn_add);
        addClient.setOnClickListener(v -> showAddClientFragment());
    }

    private void showAddClientFragment() {
        NewClientFragment newClientFragment = new NewClientFragment();
        newClientFragment.show(getChildFragmentManager(), TAG_INSERT_CLIENT);
    }

    private void importContactListener() {
        View importContactList = inflateDView.findViewById(R.id.fragment_list_client_import);
        ProgressBar progressBar = inflateDView.findViewById(R.id.fragment_list_client_progress_bar);
        progressBottom = new ProgressBottom(progressBar);
        importContactList.setOnClickListener(this::showAlertDialogImportContacts);
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void getContactListFromSmartphone() {
        costumerViewModel.getContactListFromSmartphone(requireActivity())
                .observe(this, contactList -> {
                    progressBottom.buttonFinished();
                    showDialogContactList(contactList);
                });
    }

    private void showDialogContactList(List<Customer> contactList) {
        if (contactList.isEmpty()) {
            showAlertDialogEmptyResults();
        } else {
            showAlertDialogFindResults(contactList);
        }

    }

    private void showAlertDialogFindResults(List<Customer> contactList) {
        new AlertDialog
                .Builder(requireActivity())
                .setMessage("Foram encontrados " + contactList.size() +
                        " contatos no seu smartphone! Deseja importar todos para sua Agenda?")
                .setPositiveButton("Sim",
                        (dialog, which) -> repository.insertAll(contactList)
                )
                .setNegativeButton("Não", null)
                .show();
    }

    private void showAlertDialogEmptyResults() {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Nenhum contato novo encontrados")
                .setMessage("Você já adicionou todos os contatos do seu smartphone para sua agenda.")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void showAlertDialogImportContacts(View v) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Deseja importar novos contatos?")
                .setMessage("Vamos buscar os contatos salvos na agenda do seu smartphone e importá-los para sua lista de clientes")
                .setPositiveButton("Sim", (dialog, which) -> importContacts())
                .setNegativeButton("Não", null)
                .show();
    }

    private void importContacts() {
        if (checkPermission()) {
            progressBottom.buttonActivated();
            getContactListFromSmartphone();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(
                KEY_CLIENT, this, (requestKey, result) -> {
                    isInsert(result);
                    isUpdate(result);
                }
        );
    }

    private void isInsert(Bundle result) {
        if (result.containsKey(KEY_INSERT_CLIENT)) {
            Customer customer = (Customer) result.getSerializable(KEY_INSERT_CLIENT);
            repository.insert(customer);

        }
    }

    private void isUpdate(Bundle result) {
        if (result.containsKey(KEY_UPDATE_CLIENT)) {
            Customer customer = (Customer) result.getSerializable(KEY_UPDATE_CLIENT);
            repository.update(customer);
        }
    }
}
