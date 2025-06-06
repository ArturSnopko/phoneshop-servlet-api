package com.es.phoneshop.exceptions;

import com.es.phoneshop.model.product.Product;

public class OutOfStockException extends RuntimeException {
   private final Product product;
   private final int stockRequested;
   private final int stockAvailable;

  public OutOfStockException(Product product, int stockRequested, int stockAvailable) {
    this.product = product;
    this.stockRequested = stockRequested;
    this.stockAvailable = stockAvailable;
  }

  public Product getProduct() {
    return product;
  }
  public int getStockRequested() {
    return stockRequested;
  }
  public int getStockAvailable() {
    return stockAvailable;
  }
}
