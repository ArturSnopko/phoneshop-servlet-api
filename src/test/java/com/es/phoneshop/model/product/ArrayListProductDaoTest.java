package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;
    Currency usd = Currency.getInstance("USD");

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
        ((ArrayListProductDao)productDao).clear();
    }

    @Test
    public void testFindProducts() {
        Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        assertFalse(productDao.findProducts("", null, null).isEmpty());
    }

    @Test
    public void testFindProductsWithZeroStockLevel(){
        Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(100), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        assertTrue(productDao.findProducts("", null, null).isEmpty());
    }

    @Test
    public void testFindProductsWithNullPrice(){
       Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(0), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
       productDao.save(product);
       assertTrue(productDao.findProducts("", null, null).isEmpty());
    }

    @Test (expected = ProductNotFoundException.class)
    public void testGetProductWhileEmpty() throws ProductNotFoundException {
        productDao.getProduct(1234L);
    }

    @Test
    public void testAddProductWithoutID() throws ProductNotFoundException {
        Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        assertTrue(product.getId() > 0);
        Product result = productDao.getProduct(product.getId());
        assertNotNull(result);
        assertEquals("some-test-product1", result.getCode());

        Product product2 = new Product("some-test-product2", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        product2.setId(product.getId());
        productDao.save(product2);
        Product result2 = productDao.getProduct(product.getId());
        assertNotNull(result2);
        assertEquals("some-test-product2", result2.getCode());
    }

    @Test
    public void testAddProductWithID() throws ProductNotFoundException {
        Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        Product product2 = new Product("some-test-product2", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        product2.setId(product.getId());
        productDao.save(product2);
        Product result2 = productDao.getProduct(product.getId());
        assertNotNull(result2);
        assertEquals("some-test-product2", result2.getCode());
    }


    @Test
    public void testDeleteProduct() throws ProductNotFoundException {
        assertTrue(productDao.findProducts("", null, null).isEmpty());
        Product product = new Product("some-test-product1", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        assertFalse(productDao.findProducts("", null, null).isEmpty());
        productDao.delete(product.getId());
        assertTrue(productDao.findProducts("", null, null).isEmpty());
    }

}
