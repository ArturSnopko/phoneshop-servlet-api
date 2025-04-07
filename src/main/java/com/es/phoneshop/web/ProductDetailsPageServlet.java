package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.dao.product.MapProductDao;
import com.es.phoneshop.dao.product.ProductDao;
import com.es.phoneshop.exceptions.OutOfStockException;
import com.es.phoneshop.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.utils.LimitedList;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.StringUtil;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;
    private static final String PRODUCT = "product";
    private static final String CART = "cart";
    private static final String QUANTITY = "quantity";
    private static final String ERROR = "error";
    private static final String RECENTLY_VISITED = "recentlyVisited";
    private static final int RECENTLY_VISITED_COUNT = 3;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = MapProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Product product = null;
        try {
            product = productDao.getProduct(parseProductId(request));
            request.setAttribute(PRODUCT, product);
         } catch (ProductNotFoundException e) {
            log(e.getMessage());
            throw new ProductNotFoundException(e.getMessage(), e.getId());
        }

        LimitedList<Product> recentlyVisited = (LimitedList<Product>) request.getSession().getAttribute(RECENTLY_VISITED);
        if (recentlyVisited == null) {
            recentlyVisited = new LimitedList<>(RECENTLY_VISITED_COUNT);
            request.getSession().setAttribute(RECENTLY_VISITED, recentlyVisited);
        }

        recentlyVisited.removeItem(product);
        recentlyVisited.addItem(product);

        request.setAttribute(CART, cartService.getCart(request));
        request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String quantity = request.getParameter(QUANTITY);
        Long productId = parseProductId(request);
        int quantityInt;
        try {
            NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
            quantityInt = numberFormat.parse(quantity).intValue();
        } catch (ParseException e) {
            log(e.getMessage());
            request.setAttribute(ERROR, "not a valid number");
            doGet(request, response);
            return;
        }

        Cart cart = cartService.getCart(request);
        try {
            cartService.add(cart, productId, quantityInt);
        } catch (OutOfStockException e) {
            log(e.getMessage());
            request.setAttribute(ERROR, "out of stock, maximum available " + e.getStockAvailable());
            doGet(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/products/" + productId + "?message=Product added to cart");
    }

    private Long parseProductId(HttpServletRequest request) {
        String productId = request.getPathInfo();
        long id;
        productId = StringUtil.isEmpty(productId) ? "1" : productId.substring(1);
        try {
            id = Long.parseLong(productId);
        } catch (NumberFormatException e) {
            log(e.getMessage());
            id = 1L;
        }
        return id;
    }

}
