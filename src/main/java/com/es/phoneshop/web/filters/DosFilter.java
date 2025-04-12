package com.es.phoneshop.web.filters;

import com.es.phoneshop.security.DefaultDosProtectionService;
import com.es.phoneshop.security.DosProtectionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DosFilter extends HttpFilter {
    private final DosProtectionService dosProtectionService = DefaultDosProtectionService.getInstance();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        if (dosProtectionService.isAllowed(request.getRemoteAddr())) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).setStatus(429);
        }
    }
}
