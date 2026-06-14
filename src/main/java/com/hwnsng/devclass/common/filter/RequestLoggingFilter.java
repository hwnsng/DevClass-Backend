package com.hwnsng.devclass.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        long start = System.currentTimeMillis();

        MDC.put("requestId", requestId);
        res.setHeader("X-Request-Id", requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("method={} uri={} status={} elapsed={}ms requestId={}",
                    req.getMethod(), req.getRequestURI(), res.getStatus(), elapsed, requestId);
            MDC.clear();
        }
    }
}
