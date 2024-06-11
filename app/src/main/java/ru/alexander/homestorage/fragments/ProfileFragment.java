package ru.alexander.homestorage.fragments;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
    private static final String CHANNEL_ID = "your_profile_channel";
    private static final int NOTIFICATION_ID = 1;
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
        showNotification();
        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        return binding.getRoot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, show the notification
            showNotification();
        }
    }
    private void showNotification() {
        // Check if the app has permission to show notifications
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            ProfileManager.Profile profile = ProfileManager.get();
            String description = getString(R.string.username) + ": " + profile.getUsername();
            description += getString(R.string.email) + ": " + auth.getCurrentUser().getEmail();

            // Create a notification channel for Android 8.0 (Oreo) and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Ваш профиль", importance);
                channel.setDescription(description);
                NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            // Create a notification and show it
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Ваш профиль")
                    .setContentText(description)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else {
            // Permission not granted, request permission or handle accordingly
        }
    }

}