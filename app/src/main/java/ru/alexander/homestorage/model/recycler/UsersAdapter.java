package ru.alexander.homestorage.model.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.alexander.homestorage.databinding.UserItemLayoutBinding;
import ru.alexander.homestorage.dialogs.AccessDialog;
import ru.alexander.homestorage.fragments.StorageSystemFragment;
import ru.alexander.homestorage.model.storage.User;
import ru.alexander.homestorage.services.ProfileManager;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private final List<User> users;
    private final StorageSystemFragment fragment;

    public UsersAdapter(StorageSystemFragment fragment) {
        this.fragment = fragment;
        users = fragment.system.getUsers();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        UserItemLayoutBinding binding = UserItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User data = users.get(position);
        ProfileManager.load(data.getUid(), profile ->
                holder.binding.name.setText(profile.getUsername()));

        AccessDialog dialog = new AccessDialog(fragment);
        holder.binding.edit.setOnClickListener(view -> {
            dialog.user = data;
            dialog.show(fragment.getChildFragmentManager(), "access");
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final UserItemLayoutBinding binding;
        private final ViewGroup parent;

        public ViewHolder(UserItemLayoutBinding binding, ViewGroup parent) {
            super(binding.getRoot());
            this.binding = binding;
            this.parent = parent;
        }
    }
}
