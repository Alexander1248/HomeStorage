package ru.alexander.homestorage.model.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import ru.alexander.homestorage.databinding.MarkItemLayoutBinding;
import ru.alexander.homestorage.fragments.CategoryFragment;
import ru.alexander.homestorage.fragments.ItemFragment;
import ru.alexander.homestorage.model.storage.Category;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.services.StorageSystemManager;

public class StringMarkAdapter extends RecyclerView.Adapter<StringMarkAdapter.ViewHolder> {
    public boolean edit;
    private final List<String> categories;

    public StringMarkAdapter(CategoryFragment fragment) {
        categories = fragment.category.getFilters();
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
        String data = categories.get(position);
        holder.binding.text.setText(data);
        holder.binding.remove.setVisibility(
                edit ? View.VISIBLE : View.GONE
        );
        holder.binding.remove.setOnClickListener(view -> {
            categories.remove(position);
            notifyDataSetChanged();
            try {
                ProfileManager.update(false);
            } catch (IOException e) {
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final MarkItemLayoutBinding binding;

        public ViewHolder(MarkItemLayoutBinding binding, ViewGroup parent) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
