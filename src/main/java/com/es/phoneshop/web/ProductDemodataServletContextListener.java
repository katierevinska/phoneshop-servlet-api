package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.services.product.SampleProductsFactory;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ProductDemodataServletContextListener implements ServletContextListener {
    private final ProductDao productDao;

    public ProductDemodataServletContextListener() {
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        boolean insertDemoData = Boolean.parseBoolean(event.getServletContext().getInitParameter("insertDemoData"));
        if (insertDemoData) {
            productDao.saveAll(SampleProductsFactory.createProducts());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
