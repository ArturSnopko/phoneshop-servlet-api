package com.es.phoneshop.model.product;

import org.eclipse.jetty.util.StringUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class MapProductDao implements ProductDao {
    private static volatile ProductDao instance;

    public static ProductDao getInstance() {
        if (instance == null) {
            synchronized (ProductDao.class) {
                if (instance == null) {
                    instance = new MapProductDao();
                }
            }
        }
        return instance;
    }

    private final Map<Long, Product> productMap;
    private long currentId;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private MapProductDao() {
        productMap = new HashMap<Long, Product>();
        currentId = 1L;
    }

    @Override
    public Product getProduct(Long id) throws ProductNotFoundException {
        lock.readLock().lock();
        try {
            Product res = productMap.get(id);
            if (res == null)
                throw  new ProductNotFoundException("Product with id = " + id + " wasn't found", id);
            return res;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        lock.readLock().lock();
        try {
            List<String> splitQuery = (StringUtil.isEmpty(query))
                    ? List.of()
                    : Arrays.asList(query.split("\\s+"));

            Comparator <Product> fieldComparator = Comparator.comparing((Product product)-> {
                if (SortField.DESCRIPTION == sortField) {
                    return (Comparable) product.getDescription();
                } else {
                    return (Comparable) (product.getPrice() == null? 0 : product.getPrice());
                }
            });
            Comparator <Product> productComparator = SortOrder.DESC == sortOrder ? fieldComparator.reversed() : fieldComparator;

            Comparator <Product> searchComparator = Comparator.comparingInt((Product product) ->
            (int) splitQuery.stream().filter(part -> product.getDescription().contains(part)).count()).reversed();

            Comparator <Product> finalComparator = sortField == null ? searchComparator : productComparator.thenComparing(searchComparator);

            return productMap.values().stream()
                    .filter(product -> filter(splitQuery, product))
                    .sorted(finalComparator)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(Product product) {
        lock.writeLock().lock();
        try{
            if (product.getId() == null) {
                product.setId(currentId++);
            }
            productMap.put(product.getId(), product);
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void delete(Long id) throws ProductNotFoundException {
        lock.writeLock().lock();
        try {
            if (productMap.remove(id) == null) {
                throw new ProductNotFoundException("Product with id = " + id + " wasn't found", id);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear(){
        lock.writeLock().lock();
        try {
            productMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean filter (List<String> splitQuery, Product product){
        boolean res = (splitQuery.isEmpty() ||
               splitQuery.stream().anyMatch(part -> product.getDescription().contains(part) &&
               product.getStock() > 0 && product.getPrice() != null));
        return res;
    }
}
