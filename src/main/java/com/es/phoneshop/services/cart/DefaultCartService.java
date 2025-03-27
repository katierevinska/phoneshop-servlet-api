package com.es.phoneshop.services.cart;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private ProductDao productDao = ArrayListProductDao.getInstance();
    private final Lock cartLock = new ReentrantLock();

    @Override
    public Cart getCart(HttpSession httpSession) {
        cartLock.lock();
        try {
            Cart cart = (Cart) httpSession.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                cart = new Cart();
                httpSession.setAttribute(CART_SESSION_ATTRIBUTE, cart);
            }
            return cart;
        } finally {
            cartLock.unlock();
        }
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        if (productId == null) {
            return;
        }
        cartLock.lock();
        try {
            Optional<Product> product = productDao.getProduct(productId);
            product.ifPresent(
                    p -> getCartItemById(cart, productId).ifPresentOrElse(
                            ci -> handleProductExistsInCart(ci, p, quantity),
                            () -> handleProductNotExistsInCart(cart, p, quantity))
            );
        } finally {
            cartLock.unlock();
        }
    }

    private Optional<CartItem> getCartItemById(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(item -> productId.equals(item.getProduct().getId()))
                .findAny();
    }

    @SneakyThrows
    private void handleProductExistsInCart(CartItem cartItem, Product product, int quantity) {
        int totalRequestQuantity = cartItem.getQuantity() + quantity;
        if (totalRequestQuantity > product.getStock()) {
            throw new OutOfStockException(product, totalRequestQuantity, product.getStock());
        }
        cartItem.setQuantity(totalRequestQuantity);
    }

    @SneakyThrows
    private void handleProductNotExistsInCart(Cart cart, Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }
        cart.getItems().add(new CartItem(product, quantity));
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }
}
