package com.es.phoneshop.dao;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArrayListProductDaoTest {
    private ArrayList<Product> products;

    @Mock
    private Product product1;

    @Mock
    private Product product2;

    private final ArrayListProductDao testInstance = ArrayListProductDao.getInstance();

    @BeforeEach
    void setup() {
        products = mock(ArrayList.class);
        testInstance.setProductsList(products);
    }

    @Test
    void testFindProductsEmptyResults() {
        when(products.stream()).thenReturn(Stream.of(product1, product2));

        when(product1.getPrice()).thenReturn(null);
        when(product2.getPrice()).thenReturn(null);

        List<Product> result = testInstance.findProducts(
                "smt", SortField.DESCRIPTION.toString(), SortOrder.DESC.toString());

        assertEquals(0, result.size());
    }

    @Test
    void testFindProductsPriceSorting() {
        when(product1.getPrice()).thenReturn(BigDecimal.valueOf(20));
        when(product1.getStock()).thenReturn(5);

        when(product2.getPrice()).thenReturn(BigDecimal.valueOf(10));
        when(product2.getStock()).thenReturn(10);

        when(products.stream()).thenReturn(Stream.of(product1, product2));

        List<Product> result = testInstance.findProducts(
                "",
                SortField.PRICE.toString(),
                SortOrder.ASC.toString()
        );

        assertEquals(2, result.size());
        assertEquals(product2, result.get(0));
        assertEquals(product1, result.get(1));
    }

    @Test
    void testFindProductsDescriptionSorting() {
        when(product1.getDescription()).thenReturn("Apple iPhone 14");
        when(product1.getPrice()).thenReturn(BigDecimal.valueOf(10));
        when(product1.getStock()).thenReturn(5);

        when(product2.getDescription()).thenReturn("Samsung Galaxy S22");
        when(product2.getPrice()).thenReturn(BigDecimal.valueOf(20));
        when(product2.getStock()).thenReturn(10);

        when(products.stream()).thenReturn(Stream.of(product1, product2));

        List<Product> result = testInstance.findProducts(
                null,
                SortField.DESCRIPTION.toString(),
                SortOrder.DESC.toString()
        );

        assertEquals(2, result.size());
        assertEquals(product2, result.get(0));
        assertEquals(product1, result.get(1));
    }

    @Test
    void testGetProductNullId() {
        Optional<Product> result = testInstance.getProduct(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductNotExistId() {
        when(products.stream()).thenReturn(Stream.of(product1, product2));
        when(product1.getId()).thenReturn(2L);
        when(product2.getId()).thenReturn(3L);

        Optional<Product> result = testInstance.getProduct(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductExistId() {
        when(products.stream()).thenReturn(Stream.of(product1, product2));
        when(product1.getId()).thenReturn(1L);
        when(product1.clone()).thenReturn(product1);

        Optional<Product> result = testInstance.getProduct(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result
                .map(Product::getId)
                .get());
    }

    @Test
    void testGetProductEmptyList() {
        when(products.stream()).thenReturn(Stream.of());

        Optional<Product> result = testInstance.getProduct(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveNullProduct() {
        testInstance.save(null);
        verify(products, never()).add(any(Product.class));
    }

    @Test
    void shouldSaveProductIfIdIsNull() {
        when(product1.getId()).thenReturn(null);

        testInstance.save(product1);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(product1).setId(idCaptor.capture());
        Long capturedId = idCaptor.getValue();
        assertNotNull(capturedId);

        verify(products).add(product1);
    }

    @Test
    void shouldSaveProductIfIdNotExist() {
        when(products.stream()).thenReturn(Stream.of());
        when(product1.getId()).thenReturn(12L);

        testInstance.save(product1);

        verify(product1).setId(any());
        verify(products).add(product1);
    }

    @Test
    void shouldUpdateProductIfIdExist() {
        when(products.stream()).thenReturn(Stream.of(product1));
        when(products.removeIf(any())).thenReturn(true);

        when(product1.getId()).thenReturn(1L);
        when(product2.getId()).thenReturn(1L);
        when(product1.clone()).thenReturn(product1);

        testInstance.save(product2);

        verify(products).removeIf(any());
        verify(products).add(product2);
    }

    @Test
    void shouldDeleteProductNullId() {
        testInstance.delete(null);
        verifyNoInteractions(products);
    }

    @Test
    void shouldDeleteProductNotNullId() {
        testInstance.delete(1L);

        verify(products).removeIf(any());
    }

    @Test
    void shouldAddAllProductsOnSaveAll() {
        ArrayListProductDao spyInstance = spy(testInstance);

        spyInstance.saveAll(List.of(product1, product2));

        verify(spyInstance).save(product1);
        verify(spyInstance).save(product2);
    }
}
