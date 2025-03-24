package com.es.phoneshop.web;

import com.es.phoneshop.dao.MapProductDao;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.enums.SortField;
import com.es.phoneshop.enums.SortOrder;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {
    private ProductDao productDao;
    private static final String QUERY = "query";
    private static final String SORT = "sort";
    private static final String SORT_ORDER = "order";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = MapProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter(QUERY);

        String sortField = request.getParameter(SORT);
        String sortOrder = request.getParameter(SORT_ORDER);

        request.setAttribute("products", productDao.findProducts(query,
                Optional.ofNullable(sortField).map(String::toUpperCase).map(SortField::valueOf).orElse(null),
                Optional.ofNullable(sortOrder).map(String::toUpperCase).map(SortOrder::valueOf).orElse(null)
        ));
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

}
