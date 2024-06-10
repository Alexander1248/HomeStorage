package ru.alexander.homestorage.model.storage;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.UUID;

import ru.alexander.homestorage.services.ProfileManager;

public class StorageSystem implements Serializable {
    private String uuid;
    private String name;
    private List<User> users;
    private List<Storage> storages;
    private String author;

    public StorageSystem() {
    }

    public StorageSystem(String name, User author) {
        uuid = UUID.randomUUID().toString();
        this.name = name;
        this.users = new ArrayList<>();
        this.storages = new ArrayList<>();
        this.author = author.getUid();

        users.add(author);
    }

    public void order(PriorityQueue<ProfileManager.Path> queue) {
        storages.forEach(storage -> storage.order(this, queue));
    }


    @PropertyName("uuid")
    public String getUUID() {
        return uuid;
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }
    @PropertyName("users")
    public List<User> getUsers() {
        return users;
    }

    @PropertyName("storages")
    public List<Storage> getStorages() {
        return storages;
    }

    @PropertyName("author")
    public String getAuthor() {
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageSystem that = (StorageSystem) o;
        return Objects.equals(name, that.name) && Objects.equals(users, that.users) && Objects.equals(storages, that.storages) && Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, users, storages, author);
    }
}
