package ru.alexander.homestorage.dialogs;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.DialogAccessBinding;
import ru.alexander.homestorage.databinding.DialogAddCategoryBinding;
import ru.alexander.homestorage.databinding.DialogAddStorageBinding;
import ru.alexander.homestorage.fragments.ItemFragment;
import ru.alexander.homestorage.fragments.StorageSystemFragment;
import ru.alexander.homestorage.model.recycler.AddCategoryAdapter;
import ru.alexander.homestorage.model.storage.Storage;
import ru.alexander.homestorage.model.storage.User;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.services.SearchEngineComparatorFactory;
import ru.alexander.homestorage.services.StorageSystemManager;

public class AddCategoryDialog extends DialogFragment {

    public final ItemFragment fragment;
    public DialogAddCategoryBinding binding;

    public AddCategoryDialog(ItemFragment fragment) {
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
        binding = DialogAddCategoryBinding.inflate(inflater, container, false);

        AddCategoryAdapter adapter = new AddCategoryAdapter(this);
        binding.recycler.setAdapter(adapter);

        binding.query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.order(SearchEngineComparatorFactory.generateCategoryComparator(charSequence.toString()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setStyle(DialogFragment.STYLE_NORMAL, R.style.HomeStorage_Dialog);
        return binding.getRoot();
    }
}
