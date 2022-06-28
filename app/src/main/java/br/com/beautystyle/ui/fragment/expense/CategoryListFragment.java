package br.com.beautystyle.ui.fragment.expense;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_NAME_CATEGORY;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.repository.CategoryRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.ui.adapter.recyclerview.CategoryListAdapter;

public class CategoryListFragment extends DialogFragment {

    private CategoryListAdapter adapterCategories;
    @Inject
    CategoryRepository repository;

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

        setAdapterCategoriesListView(view);

        newCategoryListener(view, savedInstanceState);
        closeDialogFragmentListener(view);
        categoriesRecycleViewListener();

        setFragmentResultlistener();

    }

    @Override
    public void onResume() {
        super.onResume();
        setLayoutParamsDialog();
        updateAdapter();
    }

    private void setLayoutParamsDialog() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    private void updateAdapter() {
        repository.getAllFromRoom()
                .doOnSuccess(categoriesFromRoom -> {
                    adapterCategories.publishResultsRangeInserted(categoriesFromRoom);
                    getAllFromApi(categoriesFromRoom);
                }).subscribe();
    }

    private void getAllFromApi(List<Category> categoriesFromRoom) {
        repository.getCategoryListFromApi(new ResultsCallBack<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categoriesFromApi) {
                updateLocalDatabase(categoriesFromRoom, categoriesFromApi);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void updateLocalDatabase(List<Category> categoriesFromRoom, List<Category> categoriesFromApi) {
        repository.insertAllOnRoom(categoriesFromRoom, categoriesFromApi)
                .doOnSuccess(ids -> {
                    setIds(categoriesFromApi, ids);
                    adapterCategories.publishResultsRangeInserted(categoriesFromApi);
                }).subscribe();
    }

    private void setIds(List<Category> categoriesFromApi, List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            categoriesFromApi.get(i).setId(ids.get(i));
        }
    }

    private void setAdapterCategoriesListView(View view) {
        RecyclerView categoriesListView = view.findViewById(R.id.fragment_list_category_recycle_view);
        adapterCategories = new CategoryListAdapter(requireActivity());
        categoriesListView.setAdapter(adapterCategories);
    }

    private void newCategoryListener(View view, Bundle savedInstanceState) {
        ImageView newCategory = view.findViewById(R.id.fragment_list_category_new);
        newCategory.setOnClickListener(v -> replaceContainer(new NewCategoryFragment()));
    }

    private void closeDialogFragmentListener(View view) {
        ImageView closeFragment = view.findViewById(R.id.fragment_list_category_close);
        closeFragment.setOnClickListener(v -> Objects.requireNonNull(getDialog()).dismiss());
    }

    private void categoriesRecycleViewListener() {
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
        adapterCategories.setOnItemLongClickListener((category, position) -> {
            showAlertDialogMenu(category, position);
            return true;
        });
    }

    private void showAlertDialogMenu(Category category, int position) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("O que você deseja fazer?")
                .setMessage("selecione uma das opções abaixo:")
                .setNeutralButton("Nada", null)
                .setPositiveButton("Excluir",
                        (dialog, which) -> delete(category, position)
                )
                .setNegativeButton("Editar", (dialog, which) -> {
                    NewCategoryFragment fragment = new NewCategoryFragment(category, position);
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

    private void delete(Category category, int position) {
        repository.deleteOnApi(category.getApiId(), new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleteOnRoom(category, position);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void deleteOnRoom(Category category, int position) {
        repository.deleteOnRoom(category)
                .doOnComplete(() ->
                        adapterCategories.publishResultsRemoved(category, position))
                .subscribe();
    }

    private void setFragmentResultlistener() {
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
        repository.insertOnApi(category, new ResultsCallBack<Category>() {
            @Override
            public void onSuccess(Category category) {
                insertOnRoom(category);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void insertOnRoom(Category category) {
        repository.insertOnRoom(category)
                .doOnComplete(() -> adapterCategories.publishResultsInserted(category))
                .subscribe();
    }

    private void update(Bundle result) {
        Category category = (Category) result.getSerializable(KEY_UPDATE_CATEGORY);
        int position = result.getInt(KEY_POSITION);
        repository.updateOnApi(category, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void resultado) {
                updateOnRoom(category, position);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void updateOnRoom(Category category, int position) {
        repository.updateOnRoom(category)
                .doOnComplete(() -> adapterCategories.publishResultsChanged(category, position))
                .subscribe();
    }

    private void showErrorMessage(String erro) {
        Toast.makeText(requireActivity(), erro, Toast.LENGTH_LONG).show();
    }
}
