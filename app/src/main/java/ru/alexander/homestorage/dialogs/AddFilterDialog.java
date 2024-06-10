package ru.alexander.homestorage.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.io.IOException;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.DialogAddCategoryBinding;
import ru.alexander.homestorage.databinding.DialogAddFilterBinding;
import ru.alexander.homestorage.databinding.DialogAddStorageBinding;
import ru.alexander.homestorage.fragments.CategoryFragment;
import ru.alexander.homestorage.fragments.StorageSystemFragment;
import ru.alexander.homestorage.model.storage.Storage;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.services.StorageSystemManager;

public class AddFilterDialog extends DialogFragment {

    private final CategoryFragment fragment;
    public DialogAddFilterBinding binding;

    public AddFilterDialog(CategoryFragment fragment) {
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
        binding = DialogAddFilterBinding.inflate(inflater, container, false);
        binding.createButton.setOnClickListener(view -> {
            String name = binding.name.getText().toString();
            if (name.isEmpty()) {
                Toast.makeText(view.getContext(), R.string.empty_name_field, Toast.LENGTH_SHORT).show();
                return;
            }

            fragment.category.getFilters().add(name);
            fragment.adapter.notifyDataSetChanged();
            try {
                ProfileManager.update(false);
            } catch (IOException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });

        setStyle(DialogFragment.STYLE_NORMAL, R.style.HomeStorage_Dialog);

        return binding.getRoot();
    }
}
