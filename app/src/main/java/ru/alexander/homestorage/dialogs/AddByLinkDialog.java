package ru.alexander.homestorage.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.nio.ByteBuffer;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.DialogAddByLinkBinding;
import ru.alexander.homestorage.fragments.StorageSystemsFragment;
import ru.alexander.homestorage.model.storage.User;
import ru.alexander.homestorage.services.StorageSystemManager;
import ru.alexander.homestorage.services.ProfileManager;
import ru.alexander.homestorage.utils.ByteHex;
import ru.alexander.homestorage.utils.Deflate;

public class AddByLinkDialog extends DialogFragment {

    public DialogAddByLinkBinding binding;
    private final StorageSystemsFragment fragment;

    public AddByLinkDialog(StorageSystemsFragment fragment) {
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
        binding = DialogAddByLinkBinding.inflate(inflater, container, false);
        binding.createButton.setOnClickListener(view -> {
            String link = binding.name.getText().toString();
            if (link.isEmpty()) {
                Toast.makeText(view.getContext(), R.string.empty_link_field, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                ByteBuffer buffer = ByteBuffer.wrap(Deflate.decompress(ByteHex.fromHex(link)));

                byte[] arr = new byte[buffer.getShort()];
                buffer.get(arr);
                String name = new String(arr);

                arr = new byte[buffer.getShort()];
                buffer.get(arr);
                String uid = new String(arr);

                StorageSystemManager.get(uid, name, system -> {
                    User user = new User.Builder(fragment.auth.getCurrentUser().getUid()).build();
                    if (!system.getUsers().contains(user))
                        system.getUsers().add(user);

                    fragment.adapter.storages().add(system);

                    fragment.adapter.notifyDataSetChanged();
                    try {
                        ProfileManager.update(false);
                        StorageSystemManager.set(system);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    binding.createButton.setActivated(false);
                    dismiss();
                    binding.createButton.setActivated(true);
                });
            } catch (Exception e) {
                Toast.makeText(view.getContext(), R.string.wrong_link_format, Toast.LENGTH_SHORT).show();
            }
        });
        setStyle(DialogFragment.STYLE_NORMAL, R.style.HomeStorage_Dialog);
        return binding.getRoot();
    }
}
