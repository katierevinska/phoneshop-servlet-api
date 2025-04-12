package com.es.phoneshop.services.cart;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.IntBinaryOperator;

public class DefaultCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private ProductDao productDao = ArrayListProductDao.getInstance();

    @Override
    public Cart getCart(HttpSession httpSession) {
        return Optional.ofNullable(
                (Cart) httpSession.getAttribute(CART_SESSION_ATTRIBUTE)
        ).orElseGet(() -> createCartOnSession(httpSession));
    }

    private Cart createCartOnSession(HttpSession httpSession) {
        Cart newCart = new Cart();
        httpSession.setAttribute(CART_SESSION_ATTRIBUTE, newCart);
        return newCart;
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        synchronized (cart) {
            changeProductQuantity(cart, productId, quantity, Integer::sum);
        }
    }

    @Override
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        synchronized (cart) {
            changeProductQuantity(cart, productId, quantity, (oldQuantity, newQuantity) -> newQuantity);
        }
    }

    @Override
    public void delete(Cart cart, Long productId) {
        synchronized (cart) {
            cart.getItems().removeIf(cartItem -> productId.equals(cartItem.getProduct().getId()));
            recalculateTotalQuantityAndPrice(cart);
        }
    }

    @Override
    public void clearCart(Cart cart) {
        synchronized (cart) {
            cart.getItems().clear();
            recalculateTotalQuantityAndPrice(cart);
        }
    }

    private Optional<CartItem> getCartItemById(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(item -> productId.equals(item.getProduct().getId()))
                .findAny();
    }

    private void changeProductQuantity(Cart cart, Long productId, int quantity, IntBinaryOperator quantityOperator) {
        if (productId == null) {
            return;
        }
        Optional<Product> productOptional = productDao.getProduct(productId);
        productOptional.ifPresent(product -> getCartItemById(cart, productId).ifPresentOrElse(
                cartItem -> changeQuantityWhenProductInCart(
                        cartItem, product, quantityOperator.applyAsInt(cartItem.getQuantity(), quantity)),
                () -> changeQuantityWhenProductNotInCart(cart, product, quantity))
        );
        recalculateTotalQuantityAndPrice(cart);
    }

    private void changeQuantityWhenProductInCart(CartItem cartItem, Product product, int quantity) {
        checkProductStock(product, quantity);
        cartItem.setQuantity(quantity);
    }

    private void changeQuantityWhenProductNotInCart(Cart cart, Product product, int quantity) {
        checkProductStock(product, quantity);
        if (cart.getItems().isEmpty()) {
            cart.setCurrency(product.getCurrency());
        }
        cart.getItems().add(new CartItem(product, quantity));
    }

    @SneakyThrows
    private void checkProductStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }
    }

    private void recalculateTotalQuantityAndPrice(Cart cart) {
        int totalQuantity = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        BigDecimal totalPrice = cart.getItems().stream()
                .map(cartItem -> cartItem.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalQuantity(totalQuantity);
        cart.setCartPrice(totalPrice);
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }
}
