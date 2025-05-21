package org.example.service.ServiceInterface;

import org.example.entity.HistoricalPrice;

import java.util.List;

public interface HistoricalPriceService {
    HistoricalPrice create(HistoricalPrice historicalPrice);
    HistoricalPrice findById(Long id);
    List<HistoricalPrice> findAll();
    HistoricalPrice update(Long id, HistoricalPrice historicalPrice);
    void delete(Long id);
}