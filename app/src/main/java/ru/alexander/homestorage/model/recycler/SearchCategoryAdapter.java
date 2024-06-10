package ru.alexander.homestorage.model.recycler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.CategoryItemLayoutBinding;
import ru.alexander.homestorage.fragments.CategoriesFragmentDirections;
import ru.alexander.homestorage.model.storage.Category;
import ru.alexander.homestorage.services.ProfileManager;

public class SearchCategoryAdapter extends RecyclerView.Adapter<SearchCategoryAdapter.ViewHolder> {
    private final List<Category> categories;

    public SearchCategoryAdapter() {
        categories = new ArrayList<>();
    }

    public void load(Queue<Category> queue, int count) {
        clear();
        while (!queue.isEmpty() && count > 0) {
            categories.add(queue.poll());
            count--;
        }
        notifyDataSetChanged();
    }
    public void clear() {
        categories.clear();
        notifyDataSetChanged();
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