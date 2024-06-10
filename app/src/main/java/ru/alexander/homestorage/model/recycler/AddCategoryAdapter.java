package ru.alexander.homestorage.model.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;

import ru.alexander.homestorage.databinding.AddCategoryItemLayoutBinding;
import ru.alexander.homestorage.dialogs.AddCategoryDialog;
import ru.alexander.homestorage.model.storage.Category;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.services.StorageSystemManager;

public class AddCategoryAdapter extends RecyclerView.Adapter<AddCategoryAdapter.ViewHolder> {
    public boolean edit;
    private final List<Category> categories;
    private final AddCategoryDialog dialog;

    public AddCategoryAdapter(AddCategoryDialog dialog) {
        this.dialog = dialog;
        categories = ProfileManager.get().getCategories();
    }

    public void order(Comparator<Category> comparator) {
        categories.sort(comparator);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        AddCategoryItemLayoutBinding binding = AddCategoryItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category data = categories.get(position);
        holder.binding.text.setText(data.getName());
        holder.binding.text.setOnClickListener(view -> {
                dialog.fragment.item.getCategories().add(data);
                dialog.fragment.adapter.notifyDataSetChanged();
                StorageSystemManager.set(dialog.fragment.system);
                dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final AddCategoryItemLayoutBinding binding;

        public ViewHolder(AddCategoryItemLayoutBinding binding, ViewGroup parent) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
