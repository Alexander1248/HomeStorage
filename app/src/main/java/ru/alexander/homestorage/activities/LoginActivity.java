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

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        binding.logIn.setOnClickListener(this::SignIn);
        binding.register.setOnClickListener(view ->
                startActivity(new Intent(this, RegistrationActivity.class)));
//                Navigation.findNavController(view).navigate(R.id.toRegFromLogIn));
//                Navigation.findNavController(view).navigate(R.id.toControlFromLogIn));
        setContentView(binding.getRoot());
    }

    private void SignIn(View view) {
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(view.getContext(), R.string.empty_email_field, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(view.getContext(), R.string.empty_password_field, Toast.LENGTH_SHORT).show();
            return;
        }
        Task<AuthResult> result = auth.signInWithEmailAndPassword(email, password);
        result.addOnSuccessListener(authResult ->
                startActivity(new Intent(this, CoreActivity.class)));
//                Navigation.findNavController(view).navigate(R.id.toControlFromLogIn));
        result.addOnFailureListener(e -> {
            Toast.makeText(view.getContext(), R.string.log_in_error, Toast.LENGTH_SHORT).show();
            Log.i("Auth", e.getMessage());
        });
    }
}