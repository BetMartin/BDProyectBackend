package org.example.service.impl;

import org.example.entity.HistoricalPrice;
import org.example.Repository.HistoricalPriceRepository;
import org.example.service.ServiceInterface.HistoricalPriceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoricalPriceServiceImpl implements HistoricalPriceService {

    private final HistoricalPriceRepository historicalPriceRepository;

    public HistoricalPriceServiceImpl(HistoricalPriceRepository historicalPriceRepository) {
        this.historicalPriceRepository = historicalPriceRepository;
    }

    @Override
    public HistoricalPrice create(HistoricalPrice historicalPrice) {
        return historicalPriceRepository.save(historicalPrice);
    }

    @Override
    public HistoricalPrice findById(Long id) {
        return historicalPriceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HistoricalPrice no encontrado con ID: " + id));
    }

    @Override
    public List<HistoricalPrice> findAll() {
        return historicalPriceRepository.findAll();
    }

    @Override
    public HistoricalPrice update(Long id, HistoricalPrice historicalPrice) {
        if (!historicalPriceRepository.existsById(id)) {
            throw new RuntimeException("HistoricalPrice no encontrado con ID: " + id);
        }
        historicalPrice.setId(id); // Establecer el ID antes de actualizar
        return historicalPriceRepository.save(historicalPrice);
    }

    @Override
    public void delete(Long id) {
        if (!historicalPriceRepository.existsById(id)) {
            throw new RuntimeException("HistoricalPrice no encontrado con ID: " + id);
        }
        historicalPriceRepository.deleteById(id);
    }
}