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

    private void processOrderFields(HttpServletRequest request, Map<String, String> errors, Order order) {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String deliveryAddress = request.getParameter("deliveryAddress");
        String phone = request.getParameter("phone");
        String paymentMethodValue = request.getParameter("paymentMethod");
        String deliveryDateStr = request.getParameter("deliveryDate");

        FieldValidation.validateName(firstName)
                .ifPresentOrElse(error -> errors.put("firstName", error), () -> order.setFirstName(firstName));
        FieldValidation.validateName(lastName)
                .ifPresentOrElse(error -> errors.put("lastName", error), () -> order.setLastName(lastName));
        FieldValidation.validatePhone(phone)
                .ifPresentOrElse(error -> errors.put("phone", error), () -> order.setPhone(phone));
        FieldValidation.validateAddress(deliveryAddress)
                .ifPresentOrElse(error -> errors.put("deliveryAddress", error),
                        () -> order.setDeliveryAddress(deliveryAddress));
        FieldValidation.validateNotEmptyString(paymentMethodValue)
                .ifPresentOrElse(error -> errors.put("paymentMethod", error),
                        () -> order.setPaymentMethod(PaymentMethod.valueOf(paymentMethodValue)));
        FieldValidation.validateDateInFuture(deliveryDateStr)
                .ifPresentOrElse(error -> errors.put("deliveryDate", error),
                        () -> order.setDeliveryDate(LocalDate.parse(deliveryDateStr)));
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}

