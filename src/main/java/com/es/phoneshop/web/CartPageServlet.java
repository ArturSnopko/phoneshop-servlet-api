package com.es.phoneshop.web;

import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
    private CartService cartService;
    private static final String CART = "cart";
    private static final String ERRORS = "errors";
    private static final String PRODUCT_IDS = "productId";
    private static final String QUANTITIES = "quantity";


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(CART, cartService.getCart(request));
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] productIds = request.getParameterValues(PRODUCT_IDS);
        String[] quantities = request.getParameterValues(QUANTITIES);

        Map<Long, String> errors = new HashMap<>();
        if (productIds != null && productIds .length != 0 && quantities != null && quantities.length != 0) {
            for (int i = 0; i < productIds.length; i++) {
                int quantityInt;
                Long productId;

                try {
                    productId = Long.valueOf(productIds[i]);
                } catch (NumberFormatException e) {
                    log(e.getMessage());
                    throw e;
                }

                try {
                    NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
                    quantityInt = numberFormat.parse(quantities[i]).intValue();
                    cartService.update(cartService.getCart(request), productId, quantityInt);
                } catch (ParseException | OutOfStockException e) {
                    handleError(errors, productId, e);
                    log(e.getMessage());
                }
            }
        } else {
            doGet(request, response);
            return;
        }

        if (errors.isEmpty()){
            response.sendRedirect(request.getContextPath() + "/cart?message= Cart updated successfully");
        } else {
            request.setAttribute(ERRORS, errors);
            doGet(request, response);
        }
    }

    private void handleError(Map<Long, String> errors, Long productId, Exception e) {
        if (e instanceof ParseException) {
            errors.put(productId, "Not a valid number");
        } else {
            if (e instanceof OutOfStockException) {
                errors.put(productId, "Out of stock, maximum available " + ((OutOfStockException)e).getStockAvailable());
            }
        }
    }

}
