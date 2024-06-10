package ru.alexander.homestorage.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.io.IOException;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.DialogAddStorageSystemBinding;
import ru.alexander.homestorage.fragments.StorageSystemsFragment;
import ru.alexander.homestorage.model.storage.StorageSystem;
import ru.alexander.homestorage.model.storage.User;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.services.StorageSystemManager;

public class AddStorageSystemDialog extends DialogFragment {

    public DialogAddStorageSystemBinding binding;
    private final StorageSystemsFragment fragment;

    public AddStorageSystemDialog(StorageSystemsFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DialogAddStorageSystemBinding.inflate(inflater, container, false);
        binding.createButton.setOnClickListener(view -> {
            String name = binding.name.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), R.string.empty_name_field, Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = fragment.auth.getCurrentUser().getUid();
            StorageSystem system = new StorageSystem(
                    name, new User.Builder(uid).creator().build()
            );
            ProfileManager.getStorageSystems().add(system);
            fragment.adapter.notifyDataSetChanged();
            try {
                ProfileManager.update(true);
            } catch (IOException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            dismiss();
        });
        setStyle(DialogFragment.STYLE_NORMAL, R.style.HomeStorage_Dialog);
        return binding.getRoot();
    }
}
