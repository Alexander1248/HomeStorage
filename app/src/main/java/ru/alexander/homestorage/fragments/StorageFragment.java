package ru.alexander.homestorage.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.PriorityQueue;
import java.util.function.Supplier;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.FragmentStorageBinding;
import ru.alexander.homestorage.databinding.ToolbarTopLayoutBinding;
import ru.alexander.homestorage.model.recycler.ItemAdapter;
import ru.alexander.homestorage.model.recycler.SearchItemAdapter;
import ru.alexander.homestorage.model.storage.Item;
import ru.alexander.homestorage.model.storage.Storage;
import ru.alexander.homestorage.model.storage.StorageSystem;
import ru.alexander.homestorage.model.storage.User;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.services.SearchEngineComparatorFactory;
import ru.alexander.homestorage.services.StorageSystemManager;
import ru.alexander.homestorage.utils.Deflate;
import ru.alexander.homestorage.utils.QR;

public class StorageFragment extends Fragment {
    private FragmentStorageBinding binding;
    private ToolbarTopLayoutBinding toolbar;

    public StorageSystem system;
    public Storage storage;
    public User user;
    private ItemAdapter adapter;

    public StorageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StorageFragmentArgs args = StorageFragmentArgs.fromBundle(getArguments());
        system = args.getStorageSystem();
        storage = args.getStorage();
        try {
            user = system.getUsers().stream().filter(u -> u.getUid().equals(args.getUser())).findFirst()
                    .orElseThrow((Supplier<Throwable>) () ->
                            new RuntimeException("Storage not contains data about this user!"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStorageBinding.inflate(inflater, container, false);
        adapter = new ItemAdapter(this);

        binding.recycler.setAdapter(adapter);

        if (!user.isCanAdd())
            binding.add.setVisibility(View.GONE);

        binding.add.setOnClickListener(v -> {
            Item item = new Item(getString(R.string.item_add_auto) + " " + adapter.getItemCount(), 0);
            adapter.items().add(item);
            StorageSystemManager.set(system);
            StorageFragmentDirections.ToItem direction = StorageFragmentDirections
                    .toItem(system, storage, item, user.getUid());
            Navigation.findNavController(binding.getRoot()).navigate(direction);
        });

        toolbar = ToolbarTopLayoutBinding.bind(binding.toolbar.getRoot());
        toolbar.back.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putSerializable("storage_system", system);
            args.putString("user", user.getUid());

            Navigation.findNavController(binding.getRoot()).navigate(R.id.storageSystemFragment, args);
        });

        SearchItemAdapter searchItemAdapter = new SearchItemAdapter(this);
        toolbar.results.setAdapter(searchItemAdapter);
        toolbar.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                PriorityQueue<ProfileManager.Path> queue = new PriorityQueue<>(SearchEngineComparatorFactory.generatePathComparator(query));
                storage.order(system, queue);
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

        ActivityResultLauncher<Intent> launcher = QR.register(this);
        toolbar.qr.setOnClickListener(view -> {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            launcher.launch(intent);
        });
        toolbar.qr.setOnLongClickListener(view -> {
            byte[] systemName = system.getUUID().getBytes();
            byte[] storageName = storage.getUUID().getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(8 + systemName.length + storageName.length);
            buffer.putInt(746357);
            buffer.putShort((short) systemName.length);
            buffer.put(systemName);
            buffer.putShort((short) storageName.length);
            buffer.put(storageName);
            String data = new String(Base64.encode(Deflate.compress(buffer.array()), Base64.DEFAULT));

            QR.generate( system.getName() + "_" + storage.getName() + ".png", data);
            Toast.makeText(getContext(), R.string.qr_code_saved_in_downloads, Toast.LENGTH_SHORT).show();
            return true;
        });

        return binding.getRoot();
    }

}