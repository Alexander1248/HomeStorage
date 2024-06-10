package ru.alexander.homestorage.model.storage;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.UUID;

import ru.alexander.homestorage.services.ProfileManager;

public class Storage implements Serializable {
    private String uuid;
    private String name;
    private List<Item> items;
    private String author;

    public Storage() {
    }

    public Storage(String name, User author) {
        uuid = UUID.randomUUID().toString();
        this.name = name;
        items = new ArrayList<>();
        this.author = author.getUid();
    }

    @PropertyName("uuid")
    public String getUUID() {
        return uuid;
    }
    @PropertyName("name")
    public String getName() {
        return name;
    }
    @PropertyName("items")
    public List<Item> getItems() {
        return items;
    }

    @PropertyName("author")
    public String getAuthor() {
        return author;
    }


    public void order(StorageSystem system, PriorityQueue<ProfileManager.Path> queue) {
        items.forEach(item -> queue.add(new ProfileManager.Path(system, this, item)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Storage storage = (Storage) o;
        return Objects.equals(name, storage.name) && Objects.equals(items, storage.items) && Objects.equals(author, storage.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, items, author);
    }
}
