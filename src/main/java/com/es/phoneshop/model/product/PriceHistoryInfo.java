package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public class PriceHistoryInfo implements Cloneable, Serializable {
    private Date dateFrom;
    private BigDecimal price;
    private Currency currency;

    public PriceHistoryInfo(Date dateFrom, BigDecimal price, Currency currency) {
        this.dateFrom = dateFrom;
        this.price = price;
        this.currency = currency;
    }

    @Override
    public PriceHistoryInfo clone() {
        return new PriceHistoryInfo(new Date(this.dateFrom.getTime()), this.price, this.currency);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }
}
