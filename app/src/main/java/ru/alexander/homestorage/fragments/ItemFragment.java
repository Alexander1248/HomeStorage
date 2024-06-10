package ru.alexander.homestorage.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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

import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.function.Supplier;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.FragmentItemBinding;
import ru.alexander.homestorage.dialogs.AddCategoryDialog;
import ru.alexander.homestorage.model.recycler.CategoryMarkAdapter;
import ru.alexander.homestorage.model.storage.Item;
import ru.alexander.homestorage.model.storage.Storage;
import ru.alexander.homestorage.model.storage.StorageSystem;
import ru.alexander.homestorage.model.storage.User;
import ru.alexander.homestorage.services.StorageSystemManager;
import ru.alexander.homestorage.utils.Deflate;
import ru.alexander.homestorage.utils.QR;

public class ItemFragment extends Fragment {
    private FragmentItemBinding binding;
    private boolean inEditMode = false;
    public StorageSystem system;
    private Storage storage;
    public Item item;
    private User user;
    public CategoryMarkAdapter adapter;

    public ItemFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ItemFragmentArgs args = ItemFragmentArgs.fromBundle(getArguments());
        system = args.getStorageSystem();
        storage = args.getStorage();
        item = args.getItem();
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
        binding = FragmentItemBinding.inflate(inflater, container, false);
        binding.itemName.setText(item.getName());
        binding.name.setText(item.getName());
        binding.count.setText(String.format(Locale.getDefault(), "%d", item.getCount()));
        binding.name.setEnabled(inEditMode);


        binding.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                item.setName(charSequence.toString());
                binding.itemName.setText(item.getName());
                StorageSystemManager.set(system);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    item.setCount(Integer.parseInt(charSequence.toString()));
                    StorageSystemManager.set(system);
                } catch (Exception ignored) {}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        adapter = new CategoryMarkAdapter(this);
        binding.recycler.setAdapter(adapter);

        AddCategoryDialog dialog = new AddCategoryDialog(this);
        binding.addCategory.setOnClickListener(view ->
                dialog.show(getChildFragmentManager(), "add"));


        binding.edit.setOnClickListener(view -> {
            if (!user.isCanEditAny() && !(user.isCanEdit() && storage.getAuthor().equals(user.getUid()))) {
                Toast.makeText(getContext(), R.string.you_have_not_enough_access_rights, Toast.LENGTH_SHORT).show();
                return;
            }
            inEditMode = !inEditMode;
            if (inEditMode)
                binding.edit.setImageResource(R.drawable.save);
            else
                binding.edit.setImageResource(R.drawable.edit);

            binding.name.setEnabled(inEditMode);
            binding.count.setEnabled(inEditMode);
            binding.addCategory.setVisibility(inEditMode ? View.VISIBLE : View.GONE);
            adapter.edit = inEditMode;
            adapter.notifyDataSetChanged();
        });

        binding.edit.setOnLongClickListener(view -> {
            byte[] systemName = system.getUUID().getBytes();
            byte[] storageName = storage.getUUID().getBytes();
            byte[] itemName = item.getUUID().getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(10 + systemName.length + storageName.length + itemName.length);
            buffer.putInt(746357);
            buffer.putShort((short) systemName.length);
            buffer.put(systemName);
            buffer.putShort((short) storageName.length);
            buffer.put(storageName);
            buffer.putShort((short) itemName.length);
            buffer.put(itemName);
            String data = new String(Base64.encode(Deflate.compress(buffer.array()), Base64.DEFAULT));

            QR.generate(system.getName() + "_" + storage.getName() + "_" + item.getName() + ".png", data);
            Toast.makeText(getContext(), R.string.qr_code_saved_in_downloads, Toast.LENGTH_SHORT).show();
            return true;
        });

        binding.back.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putSerializable("storage_system", system);
            args.putSerializable("storage", storage);
            args.putString("user", user.getUid());

            Navigation.findNavController(binding.getRoot()).navigate(R.id.storageFragment, args);
        });

        return binding.getRoot();
    }
}