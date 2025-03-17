package com.es.phoneshop.dao;

import com.es.phoneshop.model.product.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Optional<Product> getProduct(Long id);
    List<Product> findProducts(
            String query, String sortFieldStr, String sortOrderStr
    );
    Long save(Product product);
    void delete(Long id);
    void saveAll(List<Product> products);
}
