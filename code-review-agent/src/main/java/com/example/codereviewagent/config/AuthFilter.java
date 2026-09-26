package com.example.codereviewagent.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * A simple gatekeeper that runs before every request.
 *
 * - If someone tries to open home.html, review.html, or call /api/review
 *   WITHOUT being logged in, they get sent back to the login page
 *   (or a 401 for API calls).
 * - Everything else (login.html, the login API, css/js files) is left alone.
 */
@Component
public class AuthFilter implements Filter {

    private static final String SESSION_KEY = "loggedInUser";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();

        boolean isProtectedPage = path.equals("/home.html") || path.equals("/review.html");
        boolean isProtectedApi = path.equals("/api/review");

        if (isProtectedPage || isProtectedApi) {
            HttpSession session = request.getSession(false);
            boolean loggedIn = session != null && session.getAttribute(SESSION_KEY) != null;

            if (!loggedIn) {
                if (isProtectedApi) {
                    // API call - respond with 401 so the frontend JS can handle it
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Not logged in\"}");
                } else {
                    // Page request - just redirect to the login page
                    response.sendRedirect("/login.html");
                }
                return; // stop here, don't continue to the controller
            }
        }

        // Not a protected path, or user is logged in - continue as normal
        chain.doFilter(servletRequest, servletResponse);
    }
}
