package com.es.phoneshop.utils;

public final class WebUtils {

    private WebUtils() {
    }

    public static final class PagePaths {
        public static final String CHECKOUT = "/WEB-INF/pages/checkout.jsp";
        public static final String PRODUCTS_LIST = "/WEB-INF/pages/productList.jsp";
        public static final String ADVANCED_SEARCH = "/WEB-INF/pages/advancedSearch.jsp";
        public static final String NOT_FOUND_PRODUCT = "/WEB-INF/pages/notFoundProduct.jsp";
        public static final String CART = "/WEB-INF/pages/cart.jsp";
        public static final String MINICART = "/WEB-INF/pages/minicart.jsp";
        public static final String ORDER_OVERVIEW = "/WEB-INF/pages/orderOverview.jsp";
        public static final String NOT_FOUND_ORDER = "/WEB-INF/pages/notFoundOrder.jsp";
        public static final String PRODUCT_DETAILS = "/WEB-INF/pages/productDetails.jsp";
    }

    public static final class UrlPaths {
        public static final String CART = "/cart";
        public static final String ORDER_OVERVIEW = "/order/overview/";

        public static final String PRODUCT_DETAILS = "/products/";

        public static final String PRODUCTS_LIST = "/products";
    }

    public static final class RequestAttributes {
        public static final String ORDER = "order";
        public static final String CART = "cart";
        public static final String PAYMENT_METHODS = "paymentMethods";
        public static final String PRODUCT = "product";
        public static final String PRODUCTS = "products";
        public static final String ERRORS = "errors";
        public static final String MESSAGE = "message";
        public static final String NOT_FOUND_ID = "notFoundId";
        public static final String RECENT_VIEW_PRODUCTS = "recentViewProducts";
    }

    public static final class SessionAttributes {
        public static final String ERROR = "error";
        public static final String ERRORS = "errors";
        public static final String MESSAGE = "message";
        public static final String PRODUCT_ID = "productId";
        public static final String QUANTITY = "quantity";
    }

    public static final class RequestParams {
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String DELIVERY_ADDRESS = "deliveryAddress";
        public static final String DELIVERY_DATE = "deliveryDate";
        public static final String PAYMENT_METHOD = "paymentMethod";
        public static final String SEARCH_MODE = "searchMode";
        public static final String PHONE = "phone";
        public static final String QUANTITY = "quantity";
        public static final String QUERY = "query";
        public static final String SORT = "sort";
        public static final String ORDER = "order";
    }
}