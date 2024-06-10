package ru.alexander.homestorage.model.storage;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Category implements Serializable {
    private String uuid;
    @PropertyName("name")
    private String name;
    private List<String> filters;

    public Category() {
    }

    public Category(String name, List<String> filters) {
        uuid = UUID.randomUUID().toString();
        this.name = name;
        this.filters = filters;
    }

    public Category(String name) {
        this(name, new ArrayList<>());
    }

    @PropertyName("uuid")
    public String getUUID() {
        return uuid;
    }
    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("filters")
    public List<String> getFilters() {
        return filters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category that = (Category) o;
        return Objects.equals(name, that.name) && Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, filters);
    }
}
