package ru.alexander.homestorage.model.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.fragments.StorageSystemFragment;
import ru.alexander.homestorage.fragments.StorageSystemFragmentDirections;
import ru.alexander.homestorage.model.storage.Storage;
import ru.alexander.homestorage.services.StorageSystemManager;

public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.ViewHolder> {
    private final List<Storage> storages;
    private final StorageSystemFragment fragment;

    public StorageAdapter(StorageSystemFragment fragment) {
        this.fragment = fragment;
        storages = fragment.system.getStorages();
    }

    public List<Storage> storages() {
        return storages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.storage_item_layout, parent, false);
        return new ViewHolder(view, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Storage data = storages.get(position);
        holder.textView.setText(data.getName());
        holder.itemView.setOnClickListener(v -> {
            StorageSystemFragmentDirections.ToStorage direction = StorageSystemFragmentDirections
                    .toStorage(fragment.system, data, fragment.user.getUid());
            Navigation.findNavController(holder.parent).navigate(direction);
        });
        holder.item.setOnLongClickListener(view -> {
            if (!fragment.user.isCanDeleteAny() && !(fragment.user.isCanDelete() && data.getAuthor().equals(fragment.user))) {
                Toast.makeText(view.getContext(), R.string.you_have_not_enough_access_rights, Toast.LENGTH_SHORT).show();
                return true;
            }

            storages.remove(position);
            notifyDataSetChanged();
            StorageSystemManager.set(fragment.system);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return storages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewGroup parent;
        public View item;
        public ViewHolder(@NonNull View itemView, ViewGroup parent) {
            super(itemView);
            this.parent = parent;
            textView = itemView.findViewById(R.id.name);
            item = itemView.findViewById(R.id.gridItem);
        }
    }
}
