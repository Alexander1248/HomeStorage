package ru.alexander.homestorage.dialogs;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.DialogLinkBinding;
import ru.alexander.homestorage.fragments.StorageSystemFragment;
import ru.alexander.homestorage.model.recycler.UsersAdapter;
import ru.alexander.homestorage.model.storage.StorageSystem;
import ru.alexander.homestorage.utils.ByteHex;
import ru.alexander.homestorage.utils.Deflate;

public class LinkDialog extends DialogFragment {

    public DialogLinkBinding binding;
    private final StorageSystemFragment fragment;

    public LinkDialog(StorageSystemFragment fragment) {
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
        binding = DialogLinkBinding.inflate(inflater, container, false);
        StorageSystem system = fragment.system;
        byte[] name = system.getName().getBytes(StandardCharsets.UTF_8);
        byte[] uid = system.getAuthor().getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + name.length + uid.length);

        buffer.putShort((short) name.length);
        buffer.put(name);

        buffer.putShort((short) uid.length);
        buffer.put(uid);


        String hex = ByteHex.toHex(Deflate.compress(buffer.array()));
        binding.link.setText(hex);
        binding.copy.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Storage Link", hex);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), R.string.text_copied_to_clipboard, Toast.LENGTH_SHORT).show();
        });

        UsersAdapter adapter = new UsersAdapter(fragment);
        binding.users.setAdapter(adapter);



        setStyle(DialogFragment.STYLE_NORMAL, R.style.HomeStorage_Dialog);
        return binding.getRoot();
    }
}
