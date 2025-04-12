package com.es.phoneshop.services.product;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentViewProducts;
import jakarta.servlet.http.HttpSession;

import java.util.LinkedList;
import java.util.Optional;

public class DefaultRecentViewProductsService implements RecentViewProductsService {
    private static final String RECENT_VIEW_PRODUCTS_ATTRIBUTE =
            DefaultRecentViewProductsService.class.getName() + ".RecentViewProducts";

    @Override
    public RecentViewProducts getRecentViewProducts(HttpSession httpSession) {
        return Optional.ofNullable(
                (RecentViewProducts) httpSession.getAttribute(RECENT_VIEW_PRODUCTS_ATTRIBUTE)
        ).orElseGet(() -> createRecentViewProductsOnSession(httpSession));
    }

    private RecentViewProducts createRecentViewProductsOnSession(HttpSession httpSession) {
        RecentViewProducts recentViewProducts = new RecentViewProducts();
        httpSession.setAttribute(RECENT_VIEW_PRODUCTS_ATTRIBUTE, recentViewProducts);
        return recentViewProducts;
    }

    @Override
    public void updateRecentViewProducts(RecentViewProducts recentViewProducts, Product product) {
        synchronized (recentViewProducts) {
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
        }
    }

    @Override
    public boolean setLimit(RecentViewProducts recentViewProducts, int limit) {
        if (limit < 0) {
            return false;
        }
        synchronized (recentViewProducts) {
            while (recentViewProducts.getProducts().size() > limit) {
                recentViewProducts.getProducts().removeLast();
            }
            recentViewProducts.setLimit(limit);
            return true;
        }
    }
}
