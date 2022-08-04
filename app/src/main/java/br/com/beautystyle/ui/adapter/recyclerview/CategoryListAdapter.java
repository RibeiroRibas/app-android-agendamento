package br.com.beautystyle.ui.adapter.recyclerview;

import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_REMOVE;
import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_UPDATE;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.ui.adapter.recyclerview.listener.AdapterListener;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ListCategoryHolder> {

    private final Context context;
    private final List<Category> categoryList = new ArrayList<>();
    private AdapterListener.OnCategoryClickListener onItemClickListener;
    private AdapterListener.OnCategoryLongClickListener onItemLongClickListener;

    public CategoryListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ListCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ListCategoryHolder(createdView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCategoryHolder holder, int position) {
        holder.onBindCategory(categoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setOnItemClickListener(AdapterListener.OnCategoryClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(AdapterListener.OnCategoryLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void update(List<Category> categories) {
        int size = this.categoryList.size();
        this.categoryList.clear();
        notifyItemRangeRemoved(0, size);
        this.categoryList.addAll(categories);
        notifyItemRangeInserted(0, categories.size());
    }

    class ListCategoryHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private Category category;
        private final TextView nameCategory;

        public ListCategoryHolder(@NonNull View itemView) {
            super(itemView);
            nameCategory = itemView.findViewById(android.R.id.text1);

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(category.getName()));
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(v -> onItemLongClickListener.onItemClick(category));

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 5, 0, ITEM_MENU_UPDATE);
            menu.add(this.getAdapterPosition(), 6, 1, ITEM_MENU_REMOVE);
        }

        public void onBindCategory(Category category) {
            this.category = category;
            nameCategory.setText(category.getName());
        }
    }
}
