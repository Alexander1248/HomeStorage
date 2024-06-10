package ru.alexander.homestorage.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.nio.ByteBuffer;
import java.util.PriorityQueue;
import java.util.function.Supplier;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.FragmentStorageSystemBinding;
import ru.alexander.homestorage.databinding.ToolbarTopLayoutBinding;
import ru.alexander.homestorage.dialogs.AddStorageDialog;
import ru.alexander.homestorage.dialogs.LinkDialog;
import ru.alexander.homestorage.model.recycler.SearchItemAdapter;
import ru.alexander.homestorage.model.recycler.StorageAdapter;
import ru.alexander.homestorage.model.storage.StorageSystem;
import ru.alexander.homestorage.model.storage.User;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.services.SearchEngineComparatorFactory;
import ru.alexander.homestorage.utils.Deflate;
import ru.alexander.homestorage.utils.QR;

public class StorageSystemFragment extends Fragment {
    public final FirebaseAuth auth;
    public StorageSystem system;
    public User user;

    private FragmentStorageSystemBinding binding;
    private ToolbarTopLayoutBinding toolbar;
    public StorageAdapter adapter;


    public StorageSystemFragment() {
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        system = StorageSystemFragmentArgs.fromBundle(getArguments()).getStorageSystem();
        String uid = auth.getCurrentUser().getUid();
        try {
            user = system.getUsers().stream().filter(u -> u.getUid().equals(uid)).findFirst()
                    .orElseThrow((Supplier<Throwable>) () ->
                            new RuntimeException("Storage not contains data about this user!"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }


//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                try {
//                    ProfileManager.close(true, () -> {
//                        setEnabled(false);
//                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
//                    });
//                } catch (IOException e) {
//                    setEnabled(false);
//                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
//                }
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStorageSystemBinding.inflate(inflater, container, false);
        adapter = new StorageAdapter(this);

        binding.recycler.setAdapter(adapter);
        binding.open.setOnClickListener(v -> openFABMenu());

        AddStorageDialog addStorageDialog = new AddStorageDialog(this);
        binding.add.setOnClickListener(v -> {
            closeFABMenu();
            addStorageDialog.show(getChildFragmentManager(), "add");
        });

        LinkDialog linkDialog = new LinkDialog(this);
        binding.link.setOnClickListener(v -> {
            closeFABMenu();
            linkDialog.show(getChildFragmentManager(), "link");
        });

        binding.recycler.setOnTouchListener((v, event) -> {
            closeFABMenu();
            return true;
        });

        if (user.isCanAdd() || user.isCanAdmin() || user.isCanAdminAny())
            binding.open.setVisibility(View.VISIBLE);
         else
            binding.open.setVisibility(View.GONE);



        toolbar = ToolbarTopLayoutBinding.bind(binding.toolbar.getRoot());
        toolbar.back.setOnClickListener(view -> {
            NavController controller = Navigation.findNavController(binding.getRoot());
            while(controller.getPreviousBackStackEntry() != null)
                controller.popBackStack();
        });

        SearchItemAdapter searchItemAdapter = new SearchItemAdapter(this);
        toolbar.results.setAdapter(searchItemAdapter);
        toolbar.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                PriorityQueue<ProfileManager.Path> queue = new PriorityQueue<>(SearchEngineComparatorFactory.generatePathComparator(query));
                system.order(queue);
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
            ByteBuffer buffer = ByteBuffer.allocate(6 + systemName.length);
            buffer.putInt(746357);
            buffer.putShort((short) systemName.length);
            buffer.put(systemName);
            String data = new String(Base64.encode(Deflate.compress(buffer.array()), Base64.DEFAULT));

            QR.generate(system.getName() + ".png", data);
            Toast.makeText(getContext(), R.string.qr_code_saved_in_downloads, Toast.LENGTH_SHORT).show();
            return true;
        });

        return binding.getRoot();
    }

    private void openFABMenu() {
        if (user.isCanAdd())
            binding.add.setVisibility(View.VISIBLE);

        if (user.isCanAdmin() || user.isCanAdminAny())
            binding.link.setVisibility(View.VISIBLE);

        binding.open.setVisibility(View.GONE);
    }
    private void closeFABMenu() {
        binding.add.setVisibility(View.GONE);
        binding.link.setVisibility(View.GONE);
        binding.open.setVisibility(View.VISIBLE);
    }
}