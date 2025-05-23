package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.HistoricalPrice;
import org.example.entity.Product;
import org.example.repository.HistoricalPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PrecioHistoricoService {
    
    private final HistoricalPriceRepository precioHistoricoRepository;

    @Transactional(readOnly = true)
    public List<HistoricalPrice> findAll() {
        return precioHistoricoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<HistoricalPrice> findById(Long id) {
        return precioHistoricoRepository.findById(id);
    }

    public HistoricalPrice save(HistoricalPrice precioHistorico) {
        validatePrecioHistorico(precioHistorico);
        precioHistorico.setDate(LocalDate.now());
        return precioHistoricoRepository.save(precioHistorico);
    }

    public HistoricalPrice update(Long id, HistoricalPrice precioHistorico) {
        if (!precioHistoricoRepository.existsById(id)) {
            throw new RuntimeException("Precio histórico no encontrado con id: " + id);
        }
        validatePrecioHistorico(precioHistorico);
        precioHistorico.setId(id);
        return precioHistoricoRepository.save(precioHistorico);
    }

    public void delete(Long id) {
        if (!precioHistoricoRepository.existsById(id)) {
            throw new RuntimeException("Precio histórico no encontrado con id: " + id);
        }
        precioHistoricoRepository.deleteById(id);
    }

    private void validatePrecioHistorico(HistoricalPrice precioHistorico) {
        if (precioHistorico.getPrice() == null || precioHistorico.getPrice() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0");
        }
        if (precioHistorico.getProduct() == null) {
            throw new IllegalArgumentException("El producto es obligatorio");
        }
    }

    @Transactional(readOnly = true)
    public List<HistoricalPrice> findByProduct(Product product) {
        return precioHistoricoRepository.findByProductOrderByFechaDesc(product);
    }

    @Transactional(readOnly = true)
    public Optional<HistoricalPrice> findLastPriceByProduct(Product product) {
        return precioHistoricoRepository.findFirstByProductOrderByFechaDesc(product);
    }

}