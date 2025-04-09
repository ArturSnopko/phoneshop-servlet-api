package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.StringUtil;

import java.io.IOException;

public class DeleteCartItemServlet extends HttpServlet {
    private CartService cartService;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long productId = parseProductId(request);

        Cart cart = cartService.getCart(request);
        cartService.delete(cart, productId);

        response.sendRedirect(request.getContextPath() + "/cart?message= Cart item removed successfully");
    }

    private Long parseProductId(HttpServletRequest request) {
        String productId = request.getPathInfo();
        long id;
        productId = StringUtil.isEmpty(productId) ? "-1" : productId.substring(1);
        try {
            id = Long.parseLong(productId);
        } catch (NumberFormatException e) {
            log(e.getMessage());
            id = -1L;
        }
        if (id == -1){
            throw new IllegalArgumentException("Invalid product ID: " + productId);
        }
        return id;
    }
}
