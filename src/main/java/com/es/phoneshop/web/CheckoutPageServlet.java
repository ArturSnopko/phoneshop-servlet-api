package com.es.phoneshop.web;

import com.es.phoneshop.enums.PaymentMethod;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.order.DefaultOrderService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.StringUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CheckoutPageServlet extends HttpServlet {
    private CartService cartService;
    private OrderService orderService;
    private static final String ORDER = "order";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String DELIVERY_DATE = "deliveryDate";
    private static final String DELIVERY_ADDRESS = "deliveryAddress";
    private static final String PHONE = "phone";
    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String ERRORS = "errors";
    private static final String PAYMENT_METHODS = "paymentMethods";


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        request.setAttribute(ORDER, orderService.getOrder(cart));
        request.setAttribute(PAYMENT_METHODS, orderService.getPaymentMethods());
        request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        Order order = orderService.getOrder(cart);

        Map<String, String> errors = new HashMap<>();

        setRequiredParameter(request, FIRST_NAME, errors, order::setFirstName, val -> val.matches("^[A-ZА-Я][a-zа-я]+(-[A-ZА-Я][a-zа-я]+)?$"));
        setRequiredParameter(request, LAST_NAME, errors, order::setLastName, val -> val.matches("^[A-ZА-Я][a-zа-я]+(-[A-ZА-Я][a-zа-я]+)?$"));
        setRequiredParameter(request, PHONE, errors, order::setPhone,val -> val.matches("^\\+375\\d{9}$"));
        setRequiredParameter(request, DELIVERY_ADDRESS, errors, order::setDeliveryAddress, val -> val.length() >= 5);
        setDeliveryDate(request, errors, order);
        setPaymentMethod(request, errors, order);

        if (errors.isEmpty()){
            cart.clear();
            orderService.placeOrder(order);
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getSecureId());
        } else {
            request.setAttribute(ERRORS, errors);
            request.setAttribute(PAYMENT_METHODS, orderService.getPaymentMethods());
            request.setAttribute(ORDER, order);
            request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
        }
    }

    private void setRequiredParameter(HttpServletRequest request, String parameter, Map<String, String> errors, Consumer<String> consumer, Predicate<String> predicate) {
        String value = request.getParameter(parameter);
        if (StringUtil.isEmpty(value)) {
            errors.put(parameter, "Value is required");
        } else if (!predicate.test(value)) {
            errors.put(parameter, "Value is invalid");
        } else {
            consumer.accept(value);
        }
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter(PAYMENT_METHOD);
        try {
            if (StringUtil.isEmpty(value)) {
                errors.put(PAYMENT_METHOD, "Value is required");
            } else {
                order.setPaymentMethod(PaymentMethod.valueOf(value));
            }
        } catch (IllegalArgumentException e) {
            errors.put(PAYMENT_METHOD, "Value is invalid");
        }
    }

    private void setDeliveryDate(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter(DELIVERY_DATE);
        if (StringUtil.isEmpty(value)) {
            errors.put(DELIVERY_DATE, "Value is required");
        } else {
            try {
                LocalDate date = LocalDate.parse(value);
                if (date.isBefore(LocalDate.now())){
                    errors.put(DELIVERY_DATE, "Date in the past");
                } else {
                    order.setDeliveryDate(date);
                }
            } catch (DateTimeParseException e) {
                errors.put(DELIVERY_DATE, "Incorrect date format");
            }
        }
    }
}
