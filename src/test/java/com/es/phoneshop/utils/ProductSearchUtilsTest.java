package com.es.phoneshop.utils;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.SearchResult;
import com.es.phoneshop.utils.ProductSearchUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductSearchUtilsTest {

    @Test
    void testCreateSearchResultEmptyQuery() {
        Product product = new Product();
        product.setDescription("Some product description");

        SearchResult result = ProductSearchUtils.createSearchResult(product, "");

        assertEquals(0, result.getSearchCoefficient());
        assertSame(product, result.getProduct());
    }

    @Test
    void testCreateSearchResultQueryNotEmpty() {
        Product product = new Product();
        product.setDescription("Samsung Galaxy S21");

        String query = "Samsung Galaxy";

        SearchResult result = ProductSearchUtils.createSearchResult(product, query);

        assertEquals(1 - 2.0 / 3.0, result.getSearchCoefficient(), 0.0001);
    }
}