package com.es.phoneshop.web;

import com.es.phoneshop.model.product.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;
    private static final String PRODUCT = "product";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = MapProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getPathInfo();
        productId = null == productId ? "1" : productId.substring(1);
        try {
            Long id = Long.parseLong(productId);
        } catch (NumberFormatException e) {
            log(e.getMessage());
            productId = "1";
        }
        try {
            request.setAttribute(PRODUCT, productDao.getProduct(Long.valueOf(productId)));
        } catch (ProductNotFoundException e) {
            log(e.getMessage());
            throw new ProductNotFoundException(e.getMessage(), e.getId());
        }
        request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
    }

}
