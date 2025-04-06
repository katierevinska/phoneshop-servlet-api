package com.es.phoneshop.utils;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.SearchResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSearchUtilsTest {

    @Mock
    private Product product;
    @Test
    void testCreateSearchResultEmptyQuery() {
        SearchResult result = ProductSearchUtils.createSearchResult(product, "");

        assertEquals(0, result.getSearchCoefficient());
        assertSame(product, result.getProduct());
    }

    @Test
    void testCreateSearchResultQueryNotEmpty() {
        when(product.getDescription()).thenReturn("Samsung Galaxy S21");

        SearchResult result = ProductSearchUtils.createSearchResult(product, "Samsung Galaxy");

        assertEquals(1 - 2.0 / 3.0, result.getSearchCoefficient(), 0.0001);
    }
}