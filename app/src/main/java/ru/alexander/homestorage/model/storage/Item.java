package ru.alexander.homestorage.model.storage;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Item implements Serializable {
    private String uuid;
    private String name;
    private int count;
    private List<Category> categories;

    public Item() {
    }

    public Item(String name, int count, List<Category> categories) {
        uuid = UUID.randomUUID().toString();
        this.name = name;
        this.count = count;
        this.categories = categories;
    }

    public Item(String name, int count) {
        this(name, count, new ArrayList<>());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @PropertyName("uuid")
    public String getUUID() {
        return uuid;
    }
    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("count")
    public int getCount() {
        return count;
    }

    @PropertyName("categories")
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return count == item.count && Objects.equals(name, item.name) && Objects.equals(categories, item.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, count, categories);
    }
}
