package com.es.phoneshop.model.product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Optional<Product> getProduct(Long id);
    List<Product> findProducts();
    Long save(Product product);
    void delete(Long id);
    void saveAll(List<Product> products);
}
