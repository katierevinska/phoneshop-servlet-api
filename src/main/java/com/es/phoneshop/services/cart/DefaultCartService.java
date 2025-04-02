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
            Optional<Product> productOptional = productDao.getProduct(productId);
            productOptional.ifPresent(product -> getCartItemById(cart, productId).ifPresentOrElse(
                    cartItem -> handleProductExistsInCart(cartItem, product, cartItem.getQuantity() + quantity),
                    () -> handleProductNotExistsInCart(cart, product, quantity))
            );
            recalculateTotalQuantity(cart);
        } finally {
            cartLock.unlock();
        }
    }

    @Override
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        if (productId == null) {
            return;
        }
        cartLock.lock();
        try {
            Optional<Product> productOptional = productDao.getProduct(productId);
            productOptional.ifPresent(
                    product -> getCartItemById(cart, productId).ifPresentOrElse(
                            cartItem -> handleProductExistsInCart(cartItem, product, quantity),
                            () -> handleProductNotExistsInCart(cart, product, quantity))
            );
            recalculateTotalQuantity(cart);
        } finally {
            cartLock.unlock();
        }
    }

    @Override
    public void delete(Cart cart, Long productId) {
        cartLock.lock();
        try {
            cart.getItems().removeIf(cartItem -> productId.equals(cartItem.getProduct().getId()));
            recalculateTotalQuantity(cart);
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
        if (quantity > product.getStock()) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }
        cartItem.setQuantity(quantity);
    }

    @SneakyThrows
    private void handleProductNotExistsInCart(Cart cart, Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }
        cart.getItems().add(new CartItem(product, quantity));
    }

    private void recalculateTotalQuantity(Cart cart) {
        int totalQuantity = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        cart.setTotalQuantity(totalQuantity);
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }
}
