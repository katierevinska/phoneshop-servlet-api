package com.es.phoneshop.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class HiddenDeleteHttpMethodFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String method = request.getParameter("method");

        if ("delete".equalsIgnoreCase(method) && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
            HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getMethod() {
                    return "DELETE";
                }
            };
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

}