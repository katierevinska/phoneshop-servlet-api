package com.es.phoneshop.web;

import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.services.order.DefaultOrderService;
import com.es.phoneshop.services.order.OrderService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderOverviewPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private OrderService orderService;
    @Mock
    private Order order;

    private final OrderOverviewPageServlet servlet = new OrderOverviewPageServlet();

    @BeforeEach
    public void setup() throws ServletException {
        servlet.init();
        servlet.setOrderService(orderService);
    }

    @Test
    void testDoGetOrderExists() throws ServletException, IOException {
        UUID orderId = UUID.randomUUID();
        when(request.getPathInfo()).thenReturn("/" + orderId);
        when(orderService.getOrder(orderId)).thenReturn(Optional.of(order));
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("order", order);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetOrderNotExists() throws ServletException, IOException {
        UUID orderId = UUID.randomUUID();
        when(request.getPathInfo()).thenReturn("/" + orderId);
        when(orderService.getOrder(orderId)).thenReturn(Optional.empty());
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).getRequestDispatcher("/WEB-INF/pages/notFoundOrder.jsp");
        verify(requestDispatcher).forward(request, response);
    }
}