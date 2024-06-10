package ru.alexander.homestorage.model.recycler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.CategoryItemLayoutBinding;
import ru.alexander.homestorage.services.ProfileManager;

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.ViewHolder> {
    private final FirebaseAuth auth;
    private final Fragment fragment;
    private final List<ProfileManager.Path> paths;

    public SearchItemAdapter(Fragment fragment) {
        auth = FirebaseAuth.getInstance();
        this.fragment = fragment;
        paths = new ArrayList<>();
    }

    public void load(Queue<ProfileManager.Path> queue, int count) {
        clear();
        while (!queue.isEmpty() && count > 0) {
            paths.add(queue.poll());
            count--;
        }
        notifyDataSetChanged();
    }
    public void clear() {
        paths.clear();
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
        ProfileManager.Path path = paths.get(position);

        holder.binding.name.setText(path.getItem().getName());
        holder.binding.icon.setImageResource(R.drawable.component);
        holder.itemView.setOnClickListener(v -> {
            clear();

            Bundle args = new Bundle();
            args.putSerializable("storage_system", path.getSystem());
            args.putSerializable("storage", path.getStorage());
            args.putSerializable("item", path.getItem());
            args.putString("user", auth.getCurrentUser().getUid());

            Navigation.findNavController(fragment.getView()).navigate(R.id.itemFragment, args);
        });
    }

    @Override
    public int getItemCount() {
        return paths.size();
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
