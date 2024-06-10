package ru.alexander.homestorage.services;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.PropertyName;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.DataFormatException;

import ru.alexander.homestorage.model.OnFinishListener;
import ru.alexander.homestorage.model.OnLoadListener;
import ru.alexander.homestorage.model.storage.Category;
import ru.alexander.homestorage.model.storage.Item;
import ru.alexander.homestorage.model.storage.Storage;
import ru.alexander.homestorage.model.storage.StorageSystem;
import ru.alexander.homestorage.utils.Deflate;

public class ProfileManager {
    private static FirebaseFirestore db;
    private static List<StorageSystem> systems = null;
    private static Profile profile;
    private static String uid = null;
    public static void init(FirebaseFirestore db) {
        ProfileManager.db = db;
        StorageSystemManager.init(db);
    }

    public static List<StorageSystem> getStorageSystems() {
        return systems;
    }


    public static void create(String uid, Profile profile, OnFinishListener listener) {
        ProfileManager.profile = profile;
        Task<Void> task = db.document("users/" + uid).set(profile);
        if (listener != null) task.addOnCompleteListener(task1 -> listener.onFinish());
    }
    public static void open(String uid) throws IOException {
        open(uid, null);
    }
    public static void open(String uid, OnFinishListener listener) throws IOException {
        if (uid == null) throw new IOException("UID error!");

        AtomicInteger counter = new AtomicInteger();
        OnLoadListener<StorageSystem> l = value -> {
            systems.add(value);
            if (counter.decrementAndGet() == 0)
                listener.onFinish();
        };

        systems = new ArrayList<>();

        counter.incrementAndGet();

        load(uid, profile -> {
            if (profile != null) {
                ProfileManager.profile = profile;
                for (Link link : profile.getStorages()) {
                    counter.incrementAndGet();
                    try {
                        StorageSystemManager.get(link.uid, link.name, l);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (counter.decrementAndGet() == 0)
                listener.onFinish();
        });

        ProfileManager.uid = uid;
    }


    public static void update(boolean updateStorages) throws IOException {
        update(updateStorages, null);
    }
    public static void update(boolean updateStorages, OnFinishListener listener) throws IOException {
        if (uid == null)
            throw new IOException("Database not opened!");

        profile.getStorages().clear();
        systems.forEach(system -> {
            profile.getStorages().add(new Link(system.getAuthor(), system.getName()));
            if (updateStorages)
                StorageSystemManager.set(system);
        });

        Task<Void> task = db.document("users/" + uid).set(profile);
        if (listener != null) task.addOnCompleteListener(task1 -> listener.onFinish());
    }

    public static void close(boolean updateStorages) throws IOException {
        close(updateStorages, null);
    }
    public static void close(boolean updateStorages, OnFinishListener listener) throws IOException {
        update(updateStorages, listener);
        systems = null;
        uid = null;
    }

    public static Profile get() {
        return profile;
    }

    public static void load(String uid, OnLoadListener<Profile> listener) {
        Task<DocumentSnapshot> task = db.document("users/" + uid).get();
        task.addOnSuccessListener(snapshot -> listener.onLoad(snapshot.toObject(Profile.class)));
        task.addOnFailureListener(e -> listener.onLoad(null));
    }

    public static Path transfer(byte[] data, Context context) throws DataFormatException {
        ByteBuffer buffer = ByteBuffer.wrap(Deflate.decompress(Base64.decode(data, Base64.DEFAULT)));
        if (buffer.getInt() != 746357) {
            if (context != null)
                Toast.makeText(context, "Wrong format!", Toast.LENGTH_SHORT).show();
            return null;
        }
        byte[] arr = new byte[buffer.getShort()];
        buffer.get(arr);
        String systemUUID = new String(arr);
        StorageSystem system = systems.stream()
                .filter(s -> s.getUUID().equals(systemUUID)).findFirst().orElse(null);
        if (system == null) {
            if (context != null)
                Toast.makeText(context, "System not found!", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!buffer.hasRemaining())
            return new Path(system, null, null);


        arr = new byte[buffer.getShort()];
        buffer.get(arr);
        String storageUUID = new String(arr);
        Storage storage = system.getStorages().stream()
                .filter(s -> s.getUUID().equals(storageUUID)).findFirst().orElse(null);
        if (storage == null) {
            if (context != null)
                Toast.makeText(context, "Storage not found!", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!buffer.hasRemaining())
            return new Path(system, storage, null);

        arr = new byte[buffer.getShort()];
        buffer.get(arr);
        String itemUUID = new String(arr);
        Item item = storage.getItems().stream()
                .filter(s -> s.getUUID().equals(itemUUID)).findFirst().orElse(null);
        if (item == null) {
            if (context != null)
                Toast.makeText(context, "Item not found!", Toast.LENGTH_SHORT).show();
            return null;
        }
        return new Path(system, storage, item);
    }
    public static class Path {
        private final StorageSystem system;
        private final Storage storage;
        private final Item item;

        public Path(StorageSystem system, Storage storage, Item item) {
            this.system = system;
            this.storage = storage;
            this.item = item;
        }

        public StorageSystem getSystem() {
            return system;
        }

        public Storage getStorage() {
            return storage;
        }

        public Item getItem() {
            return item;
        }
    }

    public static class Profile {
        @PropertyName("username")
        private String username;
        private List<Link> storages;
        private List<Category> categories;

        public Profile() {
        }


        public Profile(String username) {
            this.username = username;
            storages = new ArrayList<>();
            categories = new ArrayList<>();
        }

        @PropertyName("storages")
        public List<Link> getStorages() {
            return storages;
        }
        @PropertyName("categories")
        public List<Category> getCategories() {
            return categories;
        }

        @PropertyName("username")
        public String getUsername() {
            return username;
        }

        @PropertyName("username")
        public void setUsername(String username) {
            this.username = username;
        }
    }
    public static class Link {
        private String uid;
        private String name;

        public Link() {
        }

        public Link(String uid, String name) {
            this.uid = uid;
            this.name = name;
        }

        @PropertyName("uid")
        public String getUid() {
            return uid;
        }

        @PropertyName("name")
        public String getName() {
            return name;
        }
    }
}
