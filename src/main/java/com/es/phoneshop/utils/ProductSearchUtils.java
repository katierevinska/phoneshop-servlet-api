package com.es.phoneshop.utils;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.SearchResult;

import java.util.List;
import java.util.stream.Stream;

public final class ProductSearchUtils {
    private ProductSearchUtils() {
    }

    public static SearchResult createSearchResult(Product product, String query) {
        if (query == null || query.isEmpty()) {
            return new SearchResult(product, 0);
        }
        List<String> descriptionWords = Stream.of(product.getDescription().split(" "))
                .map(String::toLowerCase)
                .toList();
        double count = Stream.of(query.split(" "))
                .filter(word -> descriptionWords.contains(word.toLowerCase()))
                .count();
        return new SearchResult(product, 1 - count / descriptionWords.size());
    }
}
