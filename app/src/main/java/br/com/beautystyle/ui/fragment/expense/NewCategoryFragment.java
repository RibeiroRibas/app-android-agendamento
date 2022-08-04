package br.com.beautystyle.ui.fragment.expense;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_NAME_CATEGORY;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_RESULT_CATEGORY;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_CATEGORY;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.beautystyle.R;

import br.com.beautystyle.model.entity.Category;

public class NewCategoryFragment extends Fragment {

    private EditText categoryName;
    private Category category;

    public NewCategoryFragment(Category category) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_UPDATE_CATEGORY, category);
        setArguments(arguments);
    }

    public NewCategoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_new_category, container, false);

        addCategoryListener(inflatedView);
        closeFragmentListener(inflatedView);
        loadCategory(inflatedView);

        return inflatedView;
    }

    private void addCategoryListener(View inflatedView) {
        ImageView addCategory = inflatedView.findViewById(R.id.fragment_new_category_add);
        addCategory.setOnClickListener(v -> setResult());
    }

    private void setResult() {
        Bundle result = new Bundle();
        if (isUpdateCategory()) {
            setResultUpdate(result);
        } else {
            result.putString(KEY_NAME_CATEGORY, categoryName.getText().toString());
            removeThisFragment();
        }
        getParentFragmentManager().setFragmentResult(KEY_RESULT_CATEGORY, result);
    }

    private void setResultUpdate(Bundle result) {
        category.setName(categoryName.getText().toString());
        result.putSerializable(KEY_UPDATE_CATEGORY, category);
        removeThisFragment();
    }

    private void closeFragmentListener(View inflatedView) {
        ImageView closeFragment = inflatedView.findViewById(R.id.fragment_new_category_close);
        closeFragment.setOnClickListener(v -> removeThisFragment());
    }

    private void removeThisFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }

    private void loadCategory(View inflatedView) {
        categoryName = inflatedView.findViewById(R.id.fragment_new_category_name);
        if (isUpdateCategory()) {
            category = getSerializable();
            categoryName.setText(category.getName());
        }
    }

    private Category getSerializable() {
        return (Category) requireArguments().getSerializable(KEY_UPDATE_CATEGORY);
    }

    private boolean isUpdateCategory() {
        return getArguments() != null && requireArguments().containsKey(KEY_UPDATE_CATEGORY);
    }
}
