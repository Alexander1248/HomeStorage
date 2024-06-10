package ru.alexander.homestorage.dialogs;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.DialogAccessBinding;
import ru.alexander.homestorage.fragments.StorageSystemFragment;
import ru.alexander.homestorage.model.storage.User;
import ru.alexander.homestorage.services.StorageSystemManager;

public class AccessDialog extends DialogFragment {

    public User user;
    private DialogAccessBinding binding;
    private final StorageSystemFragment fragment;

    public AccessDialog(StorageSystemFragment fragment) {
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
        binding = DialogAccessBinding.inflate(inflater, container, false);

        binding.add.setChecked(user.isCanAdd());
        binding.edit.setChecked(user.isCanEdit());
        binding.editAny.setChecked(user.isCanEditAny());
        binding.delete.setChecked(user.isCanDelete());
        binding.deleteAny.setChecked(user.isCanDeleteAny());
        binding.admin.setChecked(user.isCanAdmin());
        binding.adminAny.setChecked(user.isCanAdminAny());

        if (!(user.isCanAdmin() || user.isCanAdminAny()) || user.equals(fragment.user) || fragment.user.isCanAdminAny()) {
            binding.add.setEnabled(fragment.user.isCanAdminAny() || fragment.user.isCanAdd());
            binding.add.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setCanAdd(isChecked);
                StorageSystemManager.set(fragment.system);
            });

            binding.edit.setEnabled(fragment.user.isCanAdminAny() || fragment.user.isCanEdit());
            binding.edit.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setCanEdit(isChecked);
                StorageSystemManager.set(fragment.system);
            });
            binding.editAny.setEnabled(fragment.user.isCanAdminAny() || fragment.user.isCanEditAny());
            binding.editAny.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setCanEditAny(isChecked);
                StorageSystemManager.set(fragment.system);
            });

            binding.delete.setEnabled(fragment.user.isCanAdminAny() || fragment.user.isCanDelete());
            binding.delete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setCanDelete(isChecked);
                StorageSystemManager.set(fragment.system);
            });
            binding.deleteAny.setEnabled(fragment.user.isCanAdminAny() || fragment.user.isCanDeleteAny());
            binding.deleteAny.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setCanDeleteAny(isChecked);
                StorageSystemManager.set(fragment.system);
            });

            binding.admin.setEnabled(fragment.user.isCanAdminAny() || fragment.user.isCanAdmin());
            binding.admin.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setCanAdmin(isChecked);
                StorageSystemManager.set(fragment.system);
            });
            binding.adminAny.setEnabled(fragment.user.isCanAdminAny());
            binding.adminAny.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setCanAdminAny(isChecked);
                StorageSystemManager.set(fragment.system);
            });
        }
        else {
            binding.add.setEnabled(false);
            binding.edit.setEnabled(false);
            binding.editAny.setEnabled(false);
            binding.delete.setEnabled(false);
            binding.deleteAny.setEnabled(false);
            binding.admin.setEnabled(false);
            binding.adminAny.setEnabled(false);
        }

        setStyle(DialogFragment.STYLE_NORMAL, R.style.HomeStorage_Dialog);
        return binding.getRoot();
    }
}
