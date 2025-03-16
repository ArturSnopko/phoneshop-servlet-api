package com.es.phoneshop.model.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ArrayListProductDao implements ProductDao {
    private static ProductDao instance;

    public static synchronized ProductDao getInstance() {
        if (instance == null) {
            instance = new ArrayListProductDao();
        }
        return instance;
    }

    private final List<Product> productList;
    private long currentId;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private ArrayListProductDao() {
        productList = new ArrayList<>();
        currentId = 1L;
    }

    @Override
    public Product getProduct(Long id) throws ProductNotFoundException {
        lock.readLock().lock();
        try {
            return productList.stream()
                    .filter(product -> id.equals(product.getId()))
                    .findAny()
                    .orElseThrow(() -> new ProductNotFoundException("Product with id = " + id + " wasn't found", id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        lock.readLock().lock();
        try {
            Comparator <Product> comparator = Comparator.comparing((Product product)-> {
                if (SortField.description == sortField) {
                    return (Comparable) product.getDescription();
                } else {
                    return (Comparable) product.getPrice();
                }
            });

            List<String> splitQuery = (query == null || query.isEmpty())
                    ? List.of()
                    : Arrays.asList(query.split("\\s+"));

            return productList.stream()
                    .filter(product -> splitQuery.isEmpty() || splitQuery.stream().anyMatch(part -> product.getDescription().contains(part)))
                    .filter(product -> product.getStock() > 0)
                    .filter(product -> product.getPrice() != null)
                    .sorted(( (sortField == null) ?
                            Comparator.comparingInt((Product product) ->
                            (int) splitQuery.stream().filter(part -> product.getDescription().contains(part)).count()).reversed()
                            : (sortOrder == SortOrder.desc ? comparator.reversed() : comparator)
                            .thenComparing(Comparator.comparingInt((Product product) ->
                            (int) splitQuery.stream().filter(part -> product.getDescription().contains(part)).count()).reversed())
                    ))

                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(Product product) {
        lock.writeLock().lock();
        try{
            if (product.getId() != null) {
                for (int i = 0; i < productList.size(); i++) {
                    if (productList.get(i).getId().equals(product.getId())) {
                        productList.set(i, product);
                        return;
                    }
                }
            }
            product.setId(currentId++);
            productList.add(product);
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void delete(Long id) throws ProductNotFoundException {
        lock.writeLock().lock();
        try {
            if (!productList.removeIf(product -> id.equals(product.getId()))){
                throw new ProductNotFoundException("Product with id = " + id + " wasn't found", id);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear(){
        productList.clear();
    }
}
