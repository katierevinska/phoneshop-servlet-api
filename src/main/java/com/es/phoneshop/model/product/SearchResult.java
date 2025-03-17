package com.es.phoneshop.model.product;

public class SearchResult {
    private Product product;
    private double searchCoefficient;

    public SearchResult(Product product, double searchCoefficient) {
        this.product = product;
        this.searchCoefficient = searchCoefficient;
    }

    public double getSearchCoefficient() {
        return searchCoefficient;
    }

    public void setSearchCoefficient(double searchCoefficient) {
        this.searchCoefficient = searchCoefficient;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
