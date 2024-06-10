package ru.alexander.homestorage.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.DialogAddStorageBinding;
import ru.alexander.homestorage.fragments.StorageSystemFragment;
import ru.alexander.homestorage.model.storage.Storage;
import ru.alexander.homestorage.services.StorageSystemManager;

public class AddStorageDialog extends DialogFragment {

    private final StorageSystemFragment fragment;
    public DialogAddStorageBinding binding;

    public AddStorageDialog(StorageSystemFragment fragment) {
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
        binding = DialogAddStorageBinding.inflate(inflater, container, false);
        binding.createButton.setOnClickListener(view -> {
            String name = binding.name.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(view.getContext(), R.string.empty_name_field, Toast.LENGTH_SHORT).show();
                return;
            }

            fragment.system.getStorages().add(new Storage(name, fragment.user));
            fragment.adapter.notifyDataSetChanged();
            StorageSystemManager.set(fragment.system);
            dismiss();
        });

        setStyle(DialogFragment.STYLE_NORMAL, R.style.HomeStorage_Dialog);

        return binding.getRoot();
    }
}
