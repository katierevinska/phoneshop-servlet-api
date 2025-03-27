package com.es.phoneshop.services.product;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentViewProducts;
import jakarta.servlet.http.HttpSession;

public interface RecentViewProductsService {
    RecentViewProducts getRecentViewProducts(HttpSession cartSession);

    void updateRecentViewProducts(RecentViewProducts recentViewProducts, Product product);

    boolean setLimit(RecentViewProducts recentViewProducts, int limit);
}
