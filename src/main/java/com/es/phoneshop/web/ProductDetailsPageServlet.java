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

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getPathInfo();
        productId = null == productId ? "1" : productId.substring(1);
        request.setAttribute("product", productDao.getProduct(Long.valueOf(productId)));
        request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
    }

}
