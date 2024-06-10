package ru.alexander.homestorage.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

import ru.alexander.homestorage.model.OnFinishListener;
import ru.alexander.homestorage.model.OnLoadListener;
import ru.alexander.homestorage.model.storage.StorageSystem;

public class StorageSystemManager {
    private static FirebaseFirestore db;
    public static void init(FirebaseFirestore db) {
        StorageSystemManager.db = db;
    }
    public static void set(StorageSystem system) {
        set(system, null);
    }
    public static void set(StorageSystem system,
                           OnFinishListener listener) {
        Task<Void> task = db.document("users/" + system.getAuthor() + "/systems/" + system.getName()).set(system);
        if (listener != null) task.addOnCompleteListener(task1 -> listener.onFinish());
    }
    public static void get(String uid, String storageSystem, OnLoadListener<StorageSystem> listener) throws IOException {
        if (uid == null) throw new IOException("UID error!");
        if (storageSystem == null) throw new IOException("Storage system not set!");
        Task<DocumentSnapshot> task = db.document("users/" + uid + "/systems/" + storageSystem).get();
        task.addOnSuccessListener(snapshot -> listener.onLoad(snapshot.toObject(StorageSystem.class)));
    }
    public static void remove(String uid, String storageSystem) throws IOException {
        remove(uid, storageSystem, null);
    }
    public static void remove(String uid, String storageSystem,
                              OnFinishListener listener) throws IOException {
        if (uid == null) throw new IOException("UID error!");
        if (storageSystem == null) throw new IOException("Storage system not set!");
        Task<Void> task = db.document("users/" + uid + "/systems/" + storageSystem).delete();
        if (listener != null) task.addOnCompleteListener(task1 -> listener.onFinish());
    }
}
