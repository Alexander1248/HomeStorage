package ru.alexander.homestorage.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.ActivityCoreBinding;

public class CoreActivity extends AppCompatActivity {
    private ActivityCoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoreBinding.inflate(getLayoutInflater());

        View host = binding.getRoot().findViewById(R.id.control_nav_host);
        NavController controller = Navigation.findNavController(host);
        NavigationUI.setupWithNavController(binding.controlNavigation, controller);
        setContentView(binding.getRoot());
    }
}