package com.es.phoneshop.web;

import com.es.phoneshop.dao.product.MapProductDao;

import com.es.phoneshop.dao.product.ProductDao;
import com.es.phoneshop.enums.SortField;
import com.es.phoneshop.enums.SortOrder;
import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.StringUtil;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;

    private static final String QUERY = "query";
    private static final String SORT = "sort";
    private static final String SORT_ORDER = "order";
    private static final String PRODUCT_ID = "productId";
    private static final String QUANTITY = "quantity";
    private static final String ERRORS = "errors";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = MapProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getParameter(PRODUCT_ID);
        String quantity = request.getParameter(QUANTITY);

        Map<Long, String> errors = new HashMap<>();
        Long productIdInt = parseProductId(productId);
        int quantityInt;
        try {
            NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
            quantityInt = numberFormat.parse(quantity).intValue();
            cartService.add(cartService.getCart(request), productIdInt, quantityInt);
        } catch (ParseException | OutOfStockException e) {
            log(e.getMessage());
            handleError(errors, productIdInt, e);
        }

        if (errors.isEmpty()){
            response.sendRedirect(request.getContextPath() + "/products?message= Added to cart successfully");
        } else {
            request.setAttribute(ERRORS, errors);
            doGet(request, response);
        }
    }

    private Long parseProductId(String productId) {
        long id;
        productId = StringUtil.isEmpty(productId) ? "1" : productId;
        try {
            id = Long.parseLong(productId);
        } catch (NumberFormatException e) {
            log(e.getMessage());
            id = 1L;
        }
        return id;
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
