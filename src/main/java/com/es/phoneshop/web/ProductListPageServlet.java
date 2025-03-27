package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.product.RecentViewProducts;
import com.es.phoneshop.services.product.DefaultRecentViewProductsService;
import com.es.phoneshop.services.product.RecentViewProductsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ProductListPageServlet extends HttpServlet {
    private ProductDao productDao;
    private RecentViewProductsService recentViewProductsService;

    @Override
    public void init() throws ServletException {
        super.init();
        productDao = ArrayListProductDao.getInstance();
        recentViewProductsService = new DefaultRecentViewProductsService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        String query = request.getParameter("query");
        String sortField = request.getParameter("sort");
        String sortOrder = request.getParameter("order");
        request.setAttribute("products", productDao.findProducts(query, sortField, sortOrder));
        RecentViewProducts recentViewProducts = recentViewProductsService.getRecentViewProducts(request.getSession());
        request.setAttribute("recentViewProducts", recentViewProducts.getProducts());
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void setRecentViewProductsService(RecentViewProductsService recentViewProductsService) {
        this.recentViewProductsService = recentViewProductsService;
    }
}
