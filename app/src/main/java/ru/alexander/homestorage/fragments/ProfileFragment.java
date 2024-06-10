package ru.alexander.homestorage.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.FragmentCategoriesBinding;
import ru.alexander.homestorage.databinding.FragmentProfileBinding;
import ru.alexander.homestorage.databinding.ToolbarTopLayoutBinding;
import ru.alexander.homestorage.services.ProfileManager;


public class ProfileFragment extends Fragment {
    private FirebaseAuth auth;
    private FragmentProfileBinding binding;

    public ProfileFragment() {
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        ProfileManager.Profile profile = ProfileManager.get();

        binding.username.setText(profile.getUsername());
        binding.email.setText(auth.getCurrentUser().getEmail());

        return binding.getRoot();
    }
}