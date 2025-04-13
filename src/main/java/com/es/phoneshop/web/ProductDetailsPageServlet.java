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
import com.es.phoneshop.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;
    private RecentViewProductsService recentViewProductsService;

    @Override
    public void init() throws ServletException {
        super.init();
        productDao = ArrayListProductDao.getInstance();
        cartService = new DefaultCartService();
        recentViewProductsService = new DefaultRecentViewProductsService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        long id = Long.parseLong(request.getPathInfo().substring(1));
        Optional<Product> productOptional = productDao.getProduct(id);
        productOptional.ifPresentOrElse(
                product -> handleGetExistsProduct(request, response, product),
                () -> handleProductByIdNotExists(request, response)
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response
    ) throws IOException, ServletException {
        long id = Long.parseLong(request.getPathInfo().substring(1));
        Optional<Product> productOptional = productDao.getProduct(id);
        productOptional.ifPresentOrElse(
                product -> handlePostExistsProduct(request, response, product),
                () -> handleProductByIdNotExists(request, response)
        );
    }

    @SneakyThrows
    private void handleGetExistsProduct(
            HttpServletRequest request, HttpServletResponse response, Product product
    ) {
        RecentViewProducts recentViewProducts = recentViewProductsService.getRecentViewProducts(request.getSession());
        recentViewProductsService.updateRecentViewProducts(recentViewProducts, product);

        request.setAttribute(WebUtils.RequestAttributes.PRODUCT, product);
        request.setAttribute(WebUtils.RequestAttributes.CART, cartService.getCart(request.getSession()));
        request.getRequestDispatcher(WebUtils.PagePaths.PRODUCT_DETAILS).forward(request, response);
    }

    @SneakyThrows
    private void handlePostExistsProduct(
            HttpServletRequest request, HttpServletResponse response, Product product
    ) {
        String quantityStr = request.getParameter(WebUtils.RequestParams.QUANTITY);
        Long productId = product.getId();
        try {
            NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
            int quantity = numberFormat.parse(quantityStr).intValue();
            quantityStr = String.valueOf(quantity);
            if (quantity <= 0) {
                response.sendRedirect(request.getContextPath() + WebUtils.UrlPaths.PRODUCT_DETAILS + productId
                        + "?error=" + quantityStr + "+is+0+or+negative+number&productQuantity=" + quantityStr);
                return;
            }
            Cart cart = cartService.getCart(request.getSession());
            cartService.add(cart, productId, quantity);
        } catch (ParseException e) {
            response.sendRedirect(request.getContextPath() + WebUtils.UrlPaths.PRODUCT_DETAILS + productId
                    + "?error=" + quantityStr + "+not+a+number&productQuantity=" + quantityStr);

            return;
        } catch (OutOfStockException e) {
            response.sendRedirect(request.getContextPath() + WebUtils.UrlPaths.PRODUCT_DETAILS + productId
                    + "?error=" + e.getMessage() + "&productQuantity=" + quantityStr);

            return;
        }
        response.sendRedirect(request.getContextPath() + WebUtils.UrlPaths.PRODUCT_DETAILS + productId + "?message="
                + quantityStr + "+" + product.getDescription() + "+added+to+cart&productQuantity=" + quantityStr);
    }

    @SneakyThrows
    private void handleProductByIdNotExists(
            HttpServletRequest request, HttpServletResponse response
    ) {
        response.sendError(404);
        request.getRequestDispatcher(WebUtils.PagePaths.NOT_FOUND_PRODUCT).forward(request, response);
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setRecentViewProductsService(RecentViewProductsService recentViewProductsService) {
        this.recentViewProductsService = recentViewProductsService;
    }
}
