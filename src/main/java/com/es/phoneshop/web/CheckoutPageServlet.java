package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.services.cart.CartService;
import com.es.phoneshop.services.cart.DefaultCartService;
import com.es.phoneshop.services.order.DefaultOrderService;
import com.es.phoneshop.services.order.OrderService;
import com.es.phoneshop.utils.FieldValidation;
import com.es.phoneshop.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        request.setAttribute(WebUtils.RequestAttributes.ORDER, orderService.constructOrderByCart(cart));
        request.setAttribute(WebUtils.RequestAttributes.PAYMENT_METHODS, orderService.getPaymentMethods());

        request.getRequestDispatcher(WebUtils.PagePaths.CHECKOUT).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        Cart cart = cartService.getCart(request.getSession());
        Order order = orderService.constructOrderByCart(cart);
        request.setAttribute(WebUtils.RequestAttributes.ORDER, orderService.constructOrderByCart(cart));
        Map<String, String> errorMessageByProductInCart = new HashMap<>();
        processOrderFields(request, errorMessageByProductInCart, order);

        if (errorMessageByProductInCart.isEmpty()) {
            UUID orderUUID = orderService.placeOrder(order);
            cartService.clearCart(cart);
            response.sendRedirect(request.getContextPath() + WebUtils.UrlPaths.ORDER_OVERVIEW + orderUUID);
        } else {
            request.setAttribute(WebUtils.RequestAttributes.PAYMENT_METHODS, orderService.getPaymentMethods());
            request.setAttribute(WebUtils.RequestAttributes.ERRORS, errorMessageByProductInCart);
            request.getRequestDispatcher(WebUtils.PagePaths.CHECKOUT).forward(request, response);
        }
    }

    private void processOrderFields(HttpServletRequest request, Map<String, String> errors, Order order) {
        String firstName = request.getParameter(WebUtils.RequestParams.FIRST_NAME);
        String lastName = request.getParameter(WebUtils.RequestParams.LAST_NAME);
        String deliveryAddress = request.getParameter(WebUtils.RequestParams.DELIVERY_ADDRESS);
        String phone = request.getParameter(WebUtils.RequestParams.PHONE);
        String paymentMethodValue = request.getParameter(WebUtils.RequestParams.PAYMENT_METHOD);
        String deliveryDateStr = request.getParameter(WebUtils.RequestParams.DELIVERY_DATE);

        FieldValidation.validateName(firstName)
                .ifPresentOrElse(error -> errors.put(WebUtils.RequestParams.FIRST_NAME, error),
                        () -> order.setFirstName(firstName));
        FieldValidation.validateName(lastName)
                .ifPresentOrElse(error -> errors.put(WebUtils.RequestParams.LAST_NAME, error),
                        () -> order.setLastName(lastName));
        FieldValidation.validatePhone(phone)
                .ifPresentOrElse(error -> errors.put(WebUtils.RequestParams.PHONE, error),
                        () -> order.setPhone(phone));
        FieldValidation.validateAddress(deliveryAddress)
                .ifPresentOrElse(error -> errors.put(WebUtils.RequestParams.DELIVERY_ADDRESS, error),
                        () -> order.setDeliveryAddress(deliveryAddress));
        FieldValidation.validateNotEmptyString(paymentMethodValue)
                .ifPresentOrElse(error -> errors.put(WebUtils.RequestParams.PAYMENT_METHOD, error),
                        () -> order.setPaymentMethod(PaymentMethod.valueOf(paymentMethodValue)));
        FieldValidation.validateDateInFuture(deliveryDateStr)
                .ifPresentOrElse(error -> errors.put(WebUtils.RequestParams.DELIVERY_DATE, error),
                        () -> order.setDeliveryDate(LocalDate.parse(deliveryDateStr)));
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}

