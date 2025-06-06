package com.es.phoneshop.web;

import com.es.phoneshop.dao.product.MapProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.dao.product.ProductDao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DemoDataServletContextListener implements ServletContextListener {
    private final ProductDao productDao;
    private static final String INSERT_DEMO_DATA = "insertDemoData";
    private static final int SAMPLE_HISTORY_SIZE = 5;

    public DemoDataServletContextListener() {
        productDao = MapProductDao.getInstance();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        boolean insertDemoData = Boolean.parseBoolean(sce.getServletContext().getInitParameter(INSERT_DEMO_DATA));

        if (insertDemoData)
            for (Product product: getSampleProducts()){
                productDao.save(product);
                setSampleHistory(product);
            }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}

    public void setSampleHistory(Product product) {
        if (product.getHistoryList() != null && !product.getHistoryList().isEmpty()) {
            return;
        }

        List <PriceHistory> history = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        BigDecimal price = product.getPrice();

        for (int i = 0; i < SAMPLE_HISTORY_SIZE; i++) {
            LocalDate date = LocalDate.now().minusDays(random.nextInt(365) + (SAMPLE_HISTORY_SIZE - i) * 365);
            BigDecimal newPrice = price.multiply(BigDecimal.valueOf(0.8 + (random.nextDouble() * 0.4)));
            history.add(new PriceHistory(date, newPrice));
        }

        history.add(new PriceHistory(LocalDate.now(), price));
        product.setHistoryList(history);
    }

    public List<Product> getSampleProducts(){
        List<Product> products = new ArrayList<>();

        Currency usd = Currency.getInstance("USD");
        products.add(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        products.add(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        products.add(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        products.add(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        products.add(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        products.add(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        products.add(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        products.add(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        products.add(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        products.add(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        products.add(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        products.add(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        products.add(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));

        return products;
    }
}
