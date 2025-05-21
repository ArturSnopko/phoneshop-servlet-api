package com.es.phoneshop.dao.product;

import com.es.phoneshop.dao.AbstractMapDao;
import com.es.phoneshop.enums.SearchQueryOptions;
import com.es.phoneshop.enums.SortField;
import com.es.phoneshop.enums.SortOrder;
import com.es.phoneshop.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.AdditionalParams;
import com.es.phoneshop.model.product.Product;
import org.eclipse.jetty.util.StringUtil;

import java.math.BigDecimal;
import java.util.*;


public class MapProductDao extends AbstractMapDao<Long, Product, ProductNotFoundException> implements ProductDao {
    private static class Holder {
        private static final MapProductDao INSTANCE = new MapProductDao();
    }

    public static MapProductDao getInstance() {
        return Holder.INSTANCE;
    }

    private static final String PRODUCT_NOT_FOUND = "Product wasn't found, id:";

    private MapProductDao() {
        super(new HashMap<>(),PRODUCT_NOT_FOUND);
    }

    @Override
    public Product getProduct(Long id) throws ProductNotFoundException {
        return super.get(id);
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder, AdditionalParams additionalParams) {
        lock.readLock().lock();
        try {
            List<String> splitQuery = (StringUtil.isEmpty(query))
                    ? List.of()
                    : Arrays.asList(query.toLowerCase().split("\\s+"));

            Comparator <Product> fieldComparator = Comparator.comparing((Product product)-> {
                if (SortField.DESCRIPTION == sortField) {
                    return (Comparable) product.getDescription();
                } else {
                    return (Comparable) (product.getPrice() == null? 0 : product.getPrice());
                }
            });
            Comparator <Product> productComparator = SortOrder.DESC == sortOrder ? fieldComparator.reversed() : fieldComparator;

            Comparator <Product> searchComparator = Comparator.comparingInt((Product product) ->
            (int) splitQuery.stream().filter(part -> product.getDescription().toLowerCase().contains(part)).count()).reversed();

            Comparator <Product> finalComparator = sortField == null ? searchComparator : productComparator.thenComparing(searchComparator);

            boolean anyMatch = additionalParams.QueryParam == null || SearchQueryOptions.valueOf(additionalParams.QueryParam.toUpperCase(Locale.ROOT)).equals(SearchQueryOptions.ANY);

            return dataMap.values().stream()
                    .filter(product -> filterByQuery(splitQuery, product, anyMatch))
                    .filter(product -> filterByCost(additionalParams.MinPrice, additionalParams.MaxPrice, product))
                    .sorted(finalComparator)
                    .toList();
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
                    : Arrays.asList(query.toLowerCase().split("\\s+"));

            Comparator <Product> fieldComparator = Comparator.comparing((Product product)-> {
                if (SortField.DESCRIPTION == sortField) {
                    return (Comparable) product.getDescription();
                } else {
                    return (Comparable) (product.getPrice() == null? 0 : product.getPrice());
                }
            });
            Comparator <Product> productComparator = SortOrder.DESC == sortOrder ? fieldComparator.reversed() : fieldComparator;

            Comparator <Product> searchComparator = Comparator.comparingInt((Product product) ->
                    (int) splitQuery.stream().filter(part -> product.getDescription().toLowerCase().contains(part)).count()).reversed();

            Comparator <Product> finalComparator = sortField == null ? searchComparator : productComparator.thenComparing(searchComparator);


            return dataMap.values().stream()
                    .filter(product -> filterByQuery(splitQuery, product, true))
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
                product.setId(currentId.getAndAdd(1));
            }
            dataMap.put(product.getId(), product);
        } finally {
            lock.writeLock().unlock();
        }

    }

    @Override
    public void delete(Long id) throws ProductNotFoundException {
        lock.writeLock().lock();
        try {
            if (dataMap.remove(id) == null) {
                throw new ProductNotFoundException("Product with id = " + id + " wasn't found", id);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear(){
        super.clear();
    }

    private boolean filterByQuery(List<String> splitQuery, Product product, boolean any){
        if (product.getStock() <= 0 || product.getPrice() == null) {
            return false;
        }

        if (splitQuery.isEmpty()) {
            return true;
        }

        String description = product.getDescription().toLowerCase();

        if (any) {
            return splitQuery.stream().anyMatch(description::contains);
        } else {
            return splitQuery.stream().allMatch(description::contains);
        }
    }

    private boolean filterByCost(String minPrice, String maxPrice, Product product) {
        if (StringUtil.isEmpty(minPrice) || StringUtil.isEmpty(maxPrice)) {
            return true;
        }

        return product.getPrice().compareTo(BigDecimal.valueOf(Long.parseLong(maxPrice))) <= 0 &&
                product.getPrice().compareTo(BigDecimal.valueOf(Long.parseLong(minPrice))) >= 0;
    }

    @Override
    protected ProductNotFoundException getException(String mes, Long key) throws ProductNotFoundException {
        return new ProductNotFoundException(mes,key);
    }

}
