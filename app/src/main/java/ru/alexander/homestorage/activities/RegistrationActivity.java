package ru.alexander.homestorage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.ActivityRegistrationBinding;
import ru.alexander.homestorage.services.ProfileManager;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        ProfileManager.init(FirebaseFirestore.getInstance());
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());

        binding.registerButton.setOnClickListener(this::SignUp);
        setContentView(binding.getRoot());
    }

    private void SignUp(View view) {
        String username = binding.username.getText().toString();
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        String passwordConfirmation = binding.passwordConfirmation.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(view.getContext(), R.string.empty_username_field, Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(view.getContext(), R.string.empty_email_field, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(view.getContext(), R.string.empty_password_field, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConfirmation)) {
            Toast.makeText(view.getContext(), R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return;
        }
        Task<AuthResult> result = auth.createUserWithEmailAndPassword(email, password);
        result.addOnSuccessListener(authResult -> {
            ProfileManager.Profile profile = new ProfileManager.Profile(username);
            ProfileManager.create(authResult.getUser().getUid(), profile,
                    () -> startActivity(new Intent(this, CoreActivity.class)));
//                    () -> Navigation.findNavController(view).navigate(R.id.toControlFromReg));
        });
        result.addOnFailureListener(e -> {
            Toast.makeText(view.getContext(), R.string.registration_error, Toast.LENGTH_SHORT).show();
            Log.i("Auth", e.getMessage());
        });
    }
}