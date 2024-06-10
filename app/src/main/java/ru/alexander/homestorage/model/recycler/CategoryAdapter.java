package ru.alexander.homestorage.model.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import ru.alexander.homestorage.databinding.CategoryItemLayoutBinding;
import ru.alexander.homestorage.fragments.CategoriesFragmentDirections;
import ru.alexander.homestorage.model.storage.Category;
import ru.alexander.homestorage.services.ProfileManager;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    public boolean edit;
    private final List<Category> categories;

    public CategoryAdapter() {
        categories = ProfileManager.get().getCategories();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CategoryItemLayoutBinding binding = CategoryItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category data = categories.get(position);
        holder.binding.name.setText(data.getName());
        holder.itemView.setOnClickListener(v -> {
            CategoriesFragmentDirections.ToCategory direction = CategoriesFragmentDirections.toCategory(data);
            Navigation.findNavController(holder.parent).navigate(direction);
        });


        holder.binding.getRoot().setOnLongClickListener(view -> {
            categories.remove(position);
            notifyDataSetChanged();

            try {
                ProfileManager.update(false);
            } catch (IOException e) {
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final CategoryItemLayoutBinding binding;
        private final ViewGroup parent;

        public ViewHolder(CategoryItemLayoutBinding binding, ViewGroup parent) {
            super(binding.getRoot());
            this.binding = binding;
            this.parent = parent;
        }
    }
}
