package ru.alexander.homestorage.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.PriorityQueue;

import ru.alexander.homestorage.databinding.FragmentStorageSystemsBinding;
import ru.alexander.homestorage.databinding.ToolbarTopLayoutBinding;
import ru.alexander.homestorage.dialogs.AddByLinkDialog;
import ru.alexander.homestorage.dialogs.AddStorageSystemDialog;
import ru.alexander.homestorage.model.recycler.SearchItemAdapter;
import ru.alexander.homestorage.model.recycler.StorageSystemAdapter;
import ru.alexander.homestorage.model.storage.Item;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.services.SearchEngineComparatorFactory;
import ru.alexander.homestorage.utils.QR;

public class StorageSystemsFragment extends Fragment {

    public final FirebaseAuth auth;
    private FragmentStorageSystemsBinding binding;
    private ToolbarTopLayoutBinding toolbar;
    public StorageSystemAdapter adapter;


    public StorageSystemsFragment() {
        auth = FirebaseAuth.getInstance();
        ProfileManager.init(FirebaseFirestore.getInstance());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStorageSystemsBinding.inflate(inflater, container, false);
        binding.open.setVisibility(View.GONE);
        QR.init(auth);

        try {
            ProfileManager.open(auth.getCurrentUser().getUid(), () -> {
                binding.open.setVisibility(View.VISIBLE);

                adapter = new StorageSystemAdapter();
                binding.recycler.setAdapter(adapter);
            });
        } catch (IOException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return binding.getRoot();
        }

        binding.open.setOnClickListener(v -> openFABMenu());

        AddStorageSystemDialog addStorageSystemDialog = new AddStorageSystemDialog(this);
        binding.add.setOnClickListener(v -> {
            closeFABMenu();
            addStorageSystemDialog.show(getChildFragmentManager(), "add");
        });

        AddByLinkDialog addByLinkDialog = new AddByLinkDialog(this);
        binding.link.setOnClickListener(v -> {
            closeFABMenu();
            addByLinkDialog.show(getChildFragmentManager(), "add");
        });

        binding.recycler.setOnTouchListener((v, event) -> {
            closeFABMenu();
            return true;
        });


        toolbar = ToolbarTopLayoutBinding.bind(binding.toolbar.getRoot());
        toolbar.back.setOnClickListener(view ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        SearchItemAdapter searchItemAdapter = new SearchItemAdapter(this);
        toolbar.results.setAdapter(searchItemAdapter);
        toolbar.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                PriorityQueue<ProfileManager.Path> queue = new PriorityQueue<>(SearchEngineComparatorFactory.generatePathComparator(query));
                ProfileManager.getStorageSystems().forEach(system -> system.order(queue));
                searchItemAdapter.load(queue, 5);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                return false;
            }
        });
        binding.recycler.setOnTouchListener((view, event) -> {
            searchItemAdapter.clear();
            return false;
        });

        ActivityResultLauncher<Intent> qrStartForResult = QR.register(this);
        toolbar.qr.setOnClickListener(view -> {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            qrStartForResult.launch(intent);
        });

        return binding.getRoot();
    }

    private void openFABMenu() {
        binding.add.setVisibility(View.VISIBLE);
        binding.link.setVisibility(View.VISIBLE);
        binding.open.setVisibility(View.GONE);
    }
    private void closeFABMenu() {
        binding.add.setVisibility(View.GONE);
        binding.link.setVisibility(View.GONE);
        binding.open.setVisibility(View.VISIBLE);
    }


}