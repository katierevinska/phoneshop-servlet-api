package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.utils.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AdvancedSearchServlet extends HttpServlet {
    private ProductDao productDao;
    private static final String DEFAULT_SEARCH_MODE = "any";

    @Override
    public void init() throws ServletException {
        super.init();
        productDao = ArrayListProductDao.getInstance();
    }

    private void handleSearchRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter(WebUtils.RequestParams.QUERY);
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String searchMode = request.getParameter(WebUtils.RequestParams.SEARCH_MODE);

        if (searchMode == null || (!"all".equals(searchMode) && !"any".equals(searchMode))) {
            searchMode = DEFAULT_SEARCH_MODE;
        }

        Integer minPrice = null;
        Integer maxPrice = null;
        boolean inputError = false;

        if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
            try {
                minPrice = Integer.parseInt(minPriceStr);
                if (minPrice < 0) {
                    request.setAttribute("errorMinPrice", "must be a positive number");
                    inputError = true;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMinPrice", "Not a number");
                inputError = true;
            }
        }

        if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                maxPrice = Integer.parseInt(maxPriceStr);
                if (maxPrice <= 0) {
                    request.setAttribute("errorMaxPrice", "must be a positive number");
                    inputError = true;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMaxPrice", "Not a number");
                inputError = true;
            }
        }

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            request.setAttribute("errorPriceRange", "Min price cannot be greater than max price.");
            inputError = true;
        }

        List<Product> products;
        if (inputError) {
            products = Collections.emptyList();
        } else {
            if ((query == null || query.trim().isEmpty()) && minPrice == null && maxPrice == null) {
                products = Collections.emptyList();
            } else {
                products = productDao.findProductsAdvancedSearch(query, searchMode, minPrice, maxPrice);
            }
        }

        request.setAttribute(WebUtils.RequestAttributes.PRODUCTS, products);

        request.getRequestDispatcher(WebUtils.PagePaths.ADVANCED_SEARCH).forward(request, response);
    }


    @Override
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response
    ) throws ServletException, IOException {
        handleSearchRequest(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response
    ) throws IOException, ServletException {
        handleSearchRequest(request, response);
    }
}