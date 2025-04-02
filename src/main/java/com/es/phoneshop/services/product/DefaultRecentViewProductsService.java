package com.es.phoneshop.services.product;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentViewProducts;
import jakarta.servlet.http.HttpSession;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultRecentViewProductsService implements RecentViewProductsService {
    private static final String RECENT_VIEW_PRODUCTS_ATTRIBUTE =
            DefaultRecentViewProductsService.class.getName() + ".RecentViewProducts";
    private final Lock recentViewProductsLock = new ReentrantLock();

    @Override
    public RecentViewProducts getRecentViewProducts(HttpSession httpSession) {
        recentViewProductsLock.lock();
        try {
            RecentViewProducts recentViewProducts = (RecentViewProducts) httpSession.getAttribute(
                    RECENT_VIEW_PRODUCTS_ATTRIBUTE);
            if (recentViewProducts == null) {
                recentViewProducts = new RecentViewProducts();
                httpSession.setAttribute(RECENT_VIEW_PRODUCTS_ATTRIBUTE, recentViewProducts);
            }
            return recentViewProducts;
        } finally {
            recentViewProductsLock.unlock();
        }

    }

    @Override
    public synchronized void updateRecentViewProducts(RecentViewProducts recentViewProducts, Product product) {
        recentViewProductsLock.lock();
        try {
            int limit = recentViewProducts.getLimit();
            LinkedList<Product> products = recentViewProducts.getProducts();
            if (limit == 0) {
                return;
            }
            Long newProductId = product.getId();
            products.removeIf(pr -> newProductId.equals(pr.getId()));
            while (products.size() >= limit) {
                products.removeLast();
            }
            products.addFirst(product);
        } finally {
            recentViewProductsLock.unlock();
        }
    }

    @Override
    public boolean setLimit(RecentViewProducts recentViewProducts, int limit) {
        if (limit < 0) {
            return false;
        }
        recentViewProductsLock.lock();
        try {
            while (recentViewProducts.getProducts().size() > limit) {
                recentViewProducts.getProducts().removeLast();
            }
            recentViewProducts.setLimit(limit);
            return true;
        } finally {
            recentViewProductsLock.unlock();
        }
    }
}
