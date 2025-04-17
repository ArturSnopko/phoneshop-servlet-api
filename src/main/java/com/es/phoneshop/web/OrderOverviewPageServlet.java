package com.es.phoneshop.web;

import com.es.phoneshop.dao.order.MapOrderDao;
import com.es.phoneshop.dao.order.OrderDao;
import com.es.phoneshop.exceptions.OrderNotFoundException;
import com.es.phoneshop.model.order.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {
    OrderDao orderDao;
    private static final String ORDER = "order";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = MapOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String secureOrderId = request.getPathInfo().substring(1);

        Order order;
        try {
            order = orderDao.getOrderBySecureId(secureOrderId);
        } catch (OrderNotFoundException e) {
            log(e.getMessage());
            throw new OrderNotFoundException("Product with id = " +secureOrderId + " wasn't found", secureOrderId);
        }
        request.setAttribute(ORDER, order);
        request.getRequestDispatcher("/WEB-INF/pages/orderOverview.jsp").forward(request, response);
    }
}
