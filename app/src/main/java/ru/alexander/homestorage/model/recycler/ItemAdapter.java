package ru.alexander.homestorage.model.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.ItemItemLayoutBinding;
import ru.alexander.homestorage.fragments.StorageFragment;
import ru.alexander.homestorage.fragments.StorageFragmentDirections;
import ru.alexander.homestorage.model.storage.Item;
import ru.alexander.homestorage.services.StorageSystemManager;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final List<Item> items;
    private final StorageFragment fragment;

    public ItemAdapter(StorageFragment fragment) {
        this.fragment = fragment;
        items = fragment.storage.getItems();
    }

    public List<Item> items() {
        return items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemItemLayoutBinding binding = ItemItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item data = items.get(position);
        holder.binding.name.setText(data.getName());
        holder.binding.count.setText(String.format(Locale.getDefault(), "%d", data.getCount()));
        holder.itemView.setOnClickListener(v -> {
            StorageFragmentDirections.ToItem direction = StorageFragmentDirections
                    .toItem(fragment.system, fragment.storage, data, fragment.user.getUid());
            Navigation.findNavController(holder.parent).navigate(direction);
        });
        holder.binding.increment.setOnClickListener(view -> {
            if (!fragment.user.isCanEditAny() && !(fragment.user.isCanEdit() && fragment.storage.getAuthor().equals(fragment.user.getUid()))) {
                Toast.makeText(fragment.getContext(), R.string.you_have_not_enough_access_rights, Toast.LENGTH_SHORT).show();
                return;
            }
            data.setCount(data.getCount() + 1);
            holder.binding.count.setText(String.format(Locale.getDefault(), "%d", data.getCount()));
            StorageSystemManager.set(fragment.system);
        });
        holder.binding.decrement.setOnClickListener(view -> {
            if (!fragment.user.isCanEditAny() && !(fragment.user.isCanEdit() && fragment.storage.getAuthor().equals(fragment.user.getUid()))) {
                Toast.makeText(fragment.getContext(), R.string.you_have_not_enough_access_rights, Toast.LENGTH_SHORT).show();
                return;
            }
            data.setCount(data.getCount() - 1);
            holder.binding.count.setText(String.format(Locale.getDefault(), "%d", data.getCount()));
            StorageSystemManager.set(fragment.system);
        });

        holder.binding.getRoot().setOnLongClickListener(view -> {
            items.remove(position);
            notifyDataSetChanged();
            StorageSystemManager.set(fragment.system);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final ItemItemLayoutBinding binding;
        private final ViewGroup parent;

        public ViewHolder(ItemItemLayoutBinding binding, ViewGroup parent) {
            super(binding.getRoot());
            this.binding = binding;
            this.parent = parent;
        }
    }
}
