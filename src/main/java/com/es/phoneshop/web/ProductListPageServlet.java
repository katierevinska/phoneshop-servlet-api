package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentViewProducts;
import com.es.phoneshop.services.cart.CartService;
import com.es.phoneshop.services.cart.DefaultCartService;
import com.es.phoneshop.services.product.DefaultRecentViewProductsService;
import com.es.phoneshop.services.product.RecentViewProductsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;
    private RecentViewProductsService recentViewProductsService;

    @Override
    public void init() throws ServletException {
        super.init();
        productDao = ArrayListProductDao.getInstance();
        recentViewProductsService = new DefaultRecentViewProductsService();
        cartService = new DefaultCartService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        processAllSessionAttributeFromDoPost(request);

        String query = request.getParameter("query");
        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");
        request.setAttribute("products", productDao.findProducts(query, sortField, sortOrder));
        RecentViewProducts recentViewProducts = recentViewProductsService.getRecentViewProducts(request.getSession());
        request.setAttribute("recentViewProducts", recentViewProducts.getProducts());
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response
    ) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();

        AddToListInfo infoItem = objectMapper.readValue(request.getInputStream(),
                objectMapper.getTypeFactory().constructType(AddToListInfo.class));

        Long id = Long.valueOf(infoItem.productId);
        Optional<Product> productOptional = productDao.getProduct(id);
        productOptional.ifPresentOrElse(
                product -> handlePostExistsProduct(request, response, infoItem.quantity, product),
                () -> handleProductByIdNotExists(request, response, id)
        );
    }

    @SneakyThrows
    private void handlePostExistsProduct(
            HttpServletRequest request, HttpServletResponse response, String quantityStr, Product product
    ) {
        Long productId = product.getId();

        response.setContentType("application/json");
        try {
            int quantity = Integer.valueOf(quantityStr);
            Cart cart = cartService.getCart(request.getSession());
            cartService.add(cart, productId, quantity);
            request.getSession().setAttribute("message",
                    quantityStr + " " + product.getDescription() + " added to cart");
        } catch (NumberFormatException e) {
            setErrorRedirectParams(request.getSession(),
                    quantityStr + " not a number", productId, quantityStr);
        } catch (OutOfStockException e) {
            setErrorRedirectParams(request.getSession(),
                    e.getMessage(), productId, quantityStr);
        } finally {
            response.sendRedirect(request.getContextPath() + "/products");
        }
    }

    @SneakyThrows
    private void handleProductByIdNotExists(
            HttpServletRequest request, HttpServletResponse response, long id
    ) {
        request.setAttribute("message", "product not found");
        request.setAttribute("notFoundId", id);
        response.sendError(404);
        request.getRequestDispatcher("/WEB-INF/pages/notFoundProduct.jsp").forward(request, response);
    }

    private void processAllSessionAttributeFromDoPost(HttpServletRequest request) {
        processSessionAttributeFromDoPost(request, "message");
        processSessionAttributeFromDoPost(request, "error");
        processSessionAttributeFromDoPost(request, "productId");
        processSessionAttributeFromDoPost(request, "quantity");
    }

    private void processSessionAttributeFromDoPost(HttpServletRequest request, String attributeName) {
        Object attribute = request.getSession().getAttribute(attributeName);
        if (attribute != null) {
            request.setAttribute(attributeName, attribute);
            request.getSession().setAttribute(attributeName, null);
        }
    }

    private void setErrorRedirectParams(HttpSession session, String errorMessage, Long productId, String quantityStr) {
        session.setAttribute("error", errorMessage);
        session.setAttribute("productId", productId);
        session.setAttribute("quantity", quantityStr);
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void setRecentViewProductsService(RecentViewProductsService recentViewProductsService) {
        this.recentViewProductsService = recentViewProductsService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    private static class AddToListInfo {
        public String productId;
        public String quantity;
    }
}
