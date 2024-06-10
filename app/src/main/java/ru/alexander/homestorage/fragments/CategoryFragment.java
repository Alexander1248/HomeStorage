package ru.alexander.homestorage.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.io.IOException;
import java.util.Locale;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.FragmentCategoriesBinding;
import ru.alexander.homestorage.databinding.FragmentCategoryBinding;
import ru.alexander.homestorage.dialogs.AddCategoryDialog;
import ru.alexander.homestorage.dialogs.AddFilterDialog;
import ru.alexander.homestorage.model.recycler.CategoryMarkAdapter;
import ru.alexander.homestorage.model.recycler.StringMarkAdapter;
import ru.alexander.homestorage.model.storage.Category;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.services.StorageSystemManager;


public class CategoryFragment extends Fragment {
    private FragmentCategoryBinding binding;
    public Category category;
    public StringMarkAdapter adapter;


    private boolean inEditMode = false;

    public CategoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CategoryFragmentArgs args = CategoryFragmentArgs.fromBundle(getArguments());
        category = args.getCategory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        binding.itemName.setText(category.getName());
        binding.name.setText(category.getName());
        binding.name.setEnabled(inEditMode);


        binding.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                category.setName(charSequence.toString());
                binding.itemName.setText(category.getName());
                try {
                    ProfileManager.update(false);
                } catch (IOException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        adapter = new StringMarkAdapter(this);
        binding.recycler.setAdapter(adapter);

        AddFilterDialog dialog = new AddFilterDialog(this);
        binding.addCategory.setOnClickListener(view ->
                dialog.show(getChildFragmentManager(), "add"));

        binding.edit.setOnClickListener(view -> {
            inEditMode = !inEditMode;
            if (inEditMode)
                binding.edit.setImageResource(R.drawable.save);
            else
                binding.edit.setImageResource(R.drawable.edit);

            binding.name.setEnabled(inEditMode);
            binding.addCategory.setVisibility(inEditMode ? View.VISIBLE : View.GONE);
            adapter.edit = inEditMode;
            adapter.notifyDataSetChanged();
        });
        binding.back.setOnClickListener(view ->
                Navigation.findNavController(binding.getRoot()).navigateUp());
        return binding.getRoot();
    }
}