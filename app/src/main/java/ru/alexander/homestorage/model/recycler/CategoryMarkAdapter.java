package ru.alexander.homestorage.model.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.alexander.homestorage.databinding.MarkItemLayoutBinding;
import ru.alexander.homestorage.fragments.ItemFragment;
import ru.alexander.homestorage.model.storage.Category;
import ru.alexander.homestorage.services.StorageSystemManager;

public class CategoryMarkAdapter extends RecyclerView.Adapter<CategoryMarkAdapter.ViewHolder> {
    public boolean edit;
    private final List<Category> categories;
    private final ItemFragment fragment;

    public CategoryMarkAdapter(ItemFragment fragment) {
        this.fragment = fragment;
        categories = fragment.item.getCategories();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        MarkItemLayoutBinding binding = MarkItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category data = categories.get(position);
        holder.binding.text.setText(data.getName());
        holder.binding.remove.setVisibility(
                edit ? View.VISIBLE : View.GONE
        );
        holder.binding.remove.setOnClickListener(view -> {
            categories.remove(position);
            notifyDataSetChanged();
            StorageSystemManager.set(fragment.system);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final MarkItemLayoutBinding binding;
        private final ViewGroup parent;

        public ViewHolder(MarkItemLayoutBinding binding, ViewGroup parent) {
            super(binding.getRoot());
            this.binding = binding;
            this.parent = parent;
        }
    }
}
