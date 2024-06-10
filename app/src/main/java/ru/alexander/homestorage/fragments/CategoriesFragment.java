package ru.alexander.homestorage.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.PriorityQueue;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.FragmentCategoriesBinding;
import ru.alexander.homestorage.databinding.ToolbarTopLayoutBinding;
import ru.alexander.homestorage.model.recycler.CategoryAdapter;
import ru.alexander.homestorage.model.recycler.SearchCategoryAdapter;
import ru.alexander.homestorage.model.recycler.SearchItemAdapter;
import ru.alexander.homestorage.model.storage.Category;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.services.SearchEngineComparatorFactory;
import ru.alexander.homestorage.services.StorageSystemManager;
import ru.alexander.homestorage.utils.Deflate;
import ru.alexander.homestorage.utils.QR;


public class CategoriesFragment extends Fragment {


    private FragmentCategoriesBinding binding;
    private ToolbarTopLayoutBinding toolbar;

    public CategoriesFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);

        CategoryAdapter adapter = new CategoryAdapter();
        binding.recycler.setAdapter(adapter);
        binding.add.setOnClickListener(v -> {
            Category category = new Category(getString(R.string.category_add_auto) + " " + adapter.getItemCount());
            if (ProfileManager.get().getCategories().contains(category)) return;
            ProfileManager.get().getCategories().add(category);
            try {
                ProfileManager.update(false);
            } catch (IOException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            CategoriesFragmentDirections.ToCategory direction = CategoriesFragmentDirections.toCategory(category);
            Navigation.findNavController(binding.getRoot()).navigate(direction);
        });


        toolbar = ToolbarTopLayoutBinding.bind(binding.toolbar.getRoot());
        toolbar.back.setOnClickListener(view ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
        SearchCategoryAdapter searchCategoryAdapter = new SearchCategoryAdapter();
        toolbar.results.setAdapter(searchCategoryAdapter);
        toolbar.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                PriorityQueue<Category> queue = new PriorityQueue<>(
                        SearchEngineComparatorFactory.generateCategoryComparator(query));
                queue.addAll(ProfileManager.get().getCategories());
                searchCategoryAdapter.load(queue, 5);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                return false;
            }
        });
        binding.recycler.setOnTouchListener((view, event) -> {
            searchCategoryAdapter.clear();
            return false;
        });

        ActivityResultLauncher<Intent> launcher = QR.register(this);
        toolbar.qr.setOnClickListener(view -> {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            launcher.launch(intent);
        });


        return binding.getRoot();
    }
}