package ru.alexander.homestorage.model.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.fragments.StorageSystemsFragmentDirections;
import ru.alexander.homestorage.model.storage.StorageSystem;
import ru.alexander.homestorage.services.StorageSystemManager;
import ru.alexander.homestorage.services.ProfileManager;

public class StorageSystemAdapter extends RecyclerView.Adapter<StorageSystemAdapter.ViewHolder> {
    private final List<StorageSystem> storages;
    private final FirebaseAuth auth;
    public StorageSystemAdapter() {
        storages = ProfileManager.getStorageSystems();
        auth = FirebaseAuth.getInstance();
    }

    public List<StorageSystem> storages() {
        return storages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.storage_system_item_layout, parent, false);
        return new ViewHolder(view, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StorageSystem data = storages.get(position);
        holder.textView.setText(data.getName());
        holder.itemView.setOnClickListener(v -> {
            StorageSystemsFragmentDirections.ToStorageSystem direction
                    = StorageSystemsFragmentDirections.toStorageSystem(data);
            Navigation.findNavController(holder.parent).navigate(direction);
        });
        holder.item.setOnLongClickListener(view -> {
            storages.remove(position);
            notifyDataSetChanged();


            try {
                if (data.getAuthor().equals(auth.getCurrentUser().getUid()))
                    StorageSystemManager.remove(data.getAuthor(), data.getName());
                 else
                    Toast.makeText(view.getContext(), R.string.you_have_not_enough_access_rights, Toast.LENGTH_SHORT).show();


                ProfileManager.update(false);
            } catch (IOException e) {
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return true;
            }
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
