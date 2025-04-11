package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.services.cart.CartService;
import com.es.phoneshop.services.cart.DefaultCartService;
import com.es.phoneshop.services.order.DefaultOrderService;
import com.es.phoneshop.services.order.OrderService;
import com.es.phoneshop.utils.FieldValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class CheckoutPageServlet extends HttpServlet {
    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        super.init();
        cartService = new DefaultCartService();
        orderService = new DefaultOrderService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        Cart cart = cartService.getCart(request.getSession());
        request.setAttribute("order", orderService.constructOrderByCart(cart));
        request.setAttribute("paymentMethods", orderService.getPaymentMethods());

        request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        Cart cart = cartService.getCart(request.getSession());
        Order order = orderService.constructOrderByCart(cart);
        request.setAttribute("order", orderService.constructOrderByCart(cart));
        Map<String, String> errorMessageByProductInCart = new HashMap<>();
        processOrderFields(request, errorMessageByProductInCart, order);

        if (errorMessageByProductInCart.isEmpty()) {
            UUID orderUUID = orderService.placeOrder(order);
            cartService.clearCart(cart);
            response.sendRedirect(request.getContextPath() + "/order/overview/" + orderUUID);
        } else {
            request.setAttribute("paymentMethods", orderService.getPaymentMethods());
            request.setAttribute("errors", errorMessageByProductInCart);
            request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
        }
    }

    private void processOrderFields(HttpServletRequest request, Map<String, String> errorMessageByProductInCart, Order order) {
        processOrderField(request, "firstName",
                FieldValidation::validateName, errorMessageByProductInCart, order::setFirstName);
        processOrderField(request, "lastName",
                FieldValidation::validateName, errorMessageByProductInCart, order::setLastName);
        processOrderField(request, "deliveryAddress",
                FieldValidation::validateAddress, errorMessageByProductInCart, order::setDeliveryAddress);
        processOrderField(request, "phone",
                FieldValidation::validatePhone, errorMessageByProductInCart, order::setPhone);
        processPaymentMethodField(request, errorMessageByProductInCart, order);
        processDeliveryDateField(request, errorMessageByProductInCart, order);
    }

    private void processOrderField(
            HttpServletRequest request, String fieldName,
            Function<String, Optional<String>> validator,
            Map<String, String> errors, Consumer<String> orderSetter
    ) {
        String fieldValue = request.getParameter(fieldName);
        validator.apply(fieldValue).ifPresentOrElse(
                error -> errors.put(fieldName, error),
                () -> orderSetter.accept(fieldValue));
    }

    private void processPaymentMethodField(HttpServletRequest request, Map<String, String> errors, Order order) {
        String paymentMethodValue = request.getParameter("paymentMethod");
        FieldValidation.validateNotEmptyString(paymentMethodValue).ifPresentOrElse(
                error -> errors.put("paymentMethod", error),
                () -> order.setPaymentMethod(PaymentMethod.valueOf(paymentMethodValue))
        );
    }

    private void processDeliveryDateField(HttpServletRequest request, Map<String, String> errors, Order order) {
        String deliveryDateStr = request.getParameter("deliveryDate");
        FieldValidation.validateDateInFuture(deliveryDateStr).ifPresentOrElse(
                error -> errors.put("deliveryDate", error),
                () -> order.setDeliveryDate(LocalDate.parse(deliveryDateStr))
        );
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}

