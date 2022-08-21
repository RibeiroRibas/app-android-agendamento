package br.com.beautystyle.ui.fragment.expense;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_NAME_CATEGORY;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_RESULT_CATEGORY;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_CATEGORY;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.util.Objects;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.CategoryViewModel;
import br.com.beautystyle.ViewModel.factory.CategoryFactory;
import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.repository.CategoryRepository;
import br.com.beautystyle.ui.adapter.recyclerview.CategoryListAdapter;

public class CategoryListFragment extends DialogFragment {

    private CategoryListAdapter adapterCategories;
    @Inject
    CategoryRepository repository;
    private CategoryViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        injectFragment();
        super.onAttach(context);
    }

    private void injectFragment() {
        ((BeautyStyleApplication) requireActivity().getApplicationContext())
                .applicationComponent.injectCategoryFrag(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_list_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CategoryFactory factory = new CategoryFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(CategoryViewModel.class);

        setAdapterCategories(view); // recyclerView

        // LISTENERS
        newCategoryListener(view);
        closeDialogFragmentListener(view);
        adapterCategoriesListener();

        setFragmentResultListener();

        updateAdapterLiveData();
    }

    @Override
    public void onResume() {
        super.onResume();
        setLayoutParamsDialog();
    }

    private void setLayoutParamsDialog() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    private void updateAdapterLiveData() {
        viewModel.getAllLiveData().observe(requireActivity(), resource -> {
            if(resource.isDataNotNull()){
                adapterCategories.update(resource.getData());
            }else{
                showErrorMessage(resource.getError());
            }
        });
    }

    private void setAdapterCategories(View view) {
        RecyclerView categoriesListView = view.findViewById(R.id.fragment_list_category_recycle_view);
        adapterCategories = new CategoryListAdapter(requireActivity());
        categoriesListView.setAdapter(adapterCategories);
    }

    private void newCategoryListener(View view) {
        ImageView newCategory = view.findViewById(R.id.fragment_list_category_new);
        newCategory.setOnClickListener(v -> replaceContainer(new NewCategoryFragment()));
    }

    private void closeDialogFragmentListener(View view) {
        ImageView closeFragment = view.findViewById(R.id.fragment_list_category_close);
        closeFragment.setOnClickListener(v -> Objects.requireNonNull(getDialog()).dismiss());
    }

    private void adapterCategoriesListener() {
        itemClickListener();
        itemLongClickListener();
    }

    private void itemClickListener() {
        adapterCategories.setOnItemClickListener(categoryName -> {
            Bundle result = new Bundle();
            result.putString(KEY_NAME_CATEGORY, categoryName);
            getParentFragmentManager().setFragmentResult(KEY_RESULT_CATEGORY, result);
            Objects.requireNonNull(getDialog()).dismiss();
        });
    }

    private void itemLongClickListener() {
        adapterCategories.setOnItemLongClickListener((category) -> {
            showAlertDialogMenu(category);
            return true;
        });
    }

    private void showAlertDialogMenu(Category category) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("O que você deseja fazer?")
                .setMessage("Selecione uma das opções abaixo:")
                .setNeutralButton("Nada", null)
                .setPositiveButton("Excluir",
                        (dialog, which) -> repository.delete(category)
                )
                .setNegativeButton("Editar", (dialog, which) -> {
                    NewCategoryFragment fragment = new NewCategoryFragment(category);
                    replaceContainer(fragment);
                })
                .show();
    }

    private void replaceContainer(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_list_expense_container_new,
                        fragment, null)
                .commit();
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(KEY_RESULT_CATEGORY,
                this, (requestKey, result) -> {
                    if (result.containsKey(KEY_NAME_CATEGORY)) {
                        insert(result);
                    } else {
                        update(result);
                    }
                }
        );
    }

    private void insert(Bundle result) {
        String name = result.getString(KEY_NAME_CATEGORY);
        Category category = new Category(name);
        repository.insert(category);
    }

    private void update(Bundle result) {
        Category category = (Category) result.getSerializable(KEY_UPDATE_CATEGORY);
        repository.update(category);
    }

    private void showErrorMessage(String error) {
        Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
    }
}
