package com.es.phoneshop.web;

import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.services.order.DefaultOrderService;
import com.es.phoneshop.services.order.OrderService;
import com.es.phoneshop.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class OrderOverviewPageServlet extends HttpServlet {
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        super.init();
        orderService = new DefaultOrderService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        String uuid = request.getPathInfo().substring(1);
        Optional<Order> orderOptional = orderService.getOrder(UUID.fromString(uuid));
        orderOptional.ifPresentOrElse(
                order -> handleOrderByIdExists(request, response, order),
                () -> handleOrderByIdNotExists(request, response)
        );
    }

    @SneakyThrows
    private void handleOrderByIdExists(
            HttpServletRequest request, HttpServletResponse response, Order order
    ) {
        request.setAttribute(WebUtils.RequestAttributes.ORDER, order);
        request.getRequestDispatcher(WebUtils.PagePaths.ORDER_OVERVIEW).forward(request, response);
    }

    @SneakyThrows
    private void handleOrderByIdNotExists(
            HttpServletRequest request, HttpServletResponse response
    ) {
        response.sendError(404);
        request.getRequestDispatcher(WebUtils.PagePaths.NOT_FOUND_ORDER).forward(request, response);
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}

