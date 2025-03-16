package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.dao.ProductDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;

    void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void init() throws ServletException {
        super.init();
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        long id = Long.parseLong(request.getPathInfo().substring(1));
        Optional<Product> product = productDao.getProduct(id);
        if (product.isEmpty()) {
            handleFailure(request, response, id);
            return;
        }
        handleSuccess(request, response, product.get());
    }

    private void handleSuccess(
            HttpServletRequest request, HttpServletResponse response, Product product
    ) throws ServletException, IOException {
        request.setAttribute("product", product);
        request.getRequestDispatcher("/WEB-INF/pages/productDetails.jsp").forward(request, response);
    }

    private void handleFailure(
            HttpServletRequest request, HttpServletResponse response, long id
    ) throws ServletException, IOException {
        request.setAttribute("notFoundId", id);
        response.sendError(404);
        request.getRequestDispatcher("/WEB-INF/pages/notFoundProduct.jsp").forward(request, response);
    }
}
