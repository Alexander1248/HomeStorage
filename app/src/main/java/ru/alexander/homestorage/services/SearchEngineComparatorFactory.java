package ru.alexander.homestorage.services;

import java.util.Comparator;

import ru.alexander.homestorage.model.storage.Category;
import ru.alexander.homestorage.model.storage.Item;

public class SearchEngineComparatorFactory {
    public static Comparator<ProfileManager.Path> generatePathComparator(String query) {
        return Comparator.comparingInt(path -> {
            Item item = path.getItem();
            int count = 0;
            for (int i = 2; i < query.length(); i++) {
                if (item.getName().toLowerCase().contains(query.substring(i - 2, i)))
                    count += 3;

                for (Category category : item.getCategories())
                    for (String filter : category.getFilters())
                        if (filter.toLowerCase().contains(query.substring(i - 2, i)))
                            count++;
            }

            return -count;
        });
    }
    public static Comparator<Category> generateCategoryComparator(String query) {
        return Comparator.comparingInt(category -> {
            int count = 0;
            for (int i = 2; i < query.length(); i++)
                if (category.getName().toLowerCase().contains(query.substring(i - 2, i)))
                    count++;

            return -count;
        });
    }
}
