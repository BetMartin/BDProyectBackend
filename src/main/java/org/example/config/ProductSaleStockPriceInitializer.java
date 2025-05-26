package org.example.config;

import org.example.entity.ProductForSale;
import org.example.entity.ProductStock;
import org.example.entity.HistoricalPrice;
import org.example.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Order(4)
public class ProductSaleStockPriceInitializer implements CommandLineRunner {

    @Autowired
    private ProductForSaleRepository productForSaleRepository;
    @Autowired
    private ProductStockRepository productStockRepository;
    @Autowired
    private HistoricalPriceRepository historicalPriceRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SizeRepository sizeRepository;

    private final Logger logger = LoggerFactory.getLogger(ProductSaleStockPriceInitializer.class);

    @Override
    public void run(String... args) {
        try {
            // Inicializar ProductForSale
            if (productForSaleRepository.count() == 0) {
                logger.info("Iniciando carga de productos para venta...");
                
                Object[][] productosVenta = {
                    {1L, 1L, 1L},
                    {2L, 1L, 2L},
                    {3L, 2L, 2L},
                    {4L, 6L, 5L},
                    {5L, 6L, 2L}
                };

                for (Object[] pv : productosVenta) {
                    ProductForSale productForSale = new ProductForSale();
                    productRepository.findById((Long) pv[1])
                        .ifPresent(productForSale::setProduct);
                    sizeRepository.findById((Long) pv[2])
                        .ifPresent(productForSale::setSize);
                    productForSaleRepository.save(productForSale);
                }
                logger.info("Productos para venta cargados exitosamente");
            }

            // Inicializar ProductStock
            if (productStockRepository.count() == 0) {
                logger.info("Iniciando carga de stock de productos...");
                
                Object[][] stocks = {
                    {"2024-01-01", 10, 1L},
                    {"2024-01-02", 5, 2L},
                    {"2024-01-03", 15, 1L},
                    {"2024-01-04", 8, 1L},
                    {"2024-01-05", 20, 1L},
                    {"2025-05-25", 10, 4L},
                    {"2025-05-25", 5, 4L},
                    {"2025-05-25", 15, 4L},
                    {"2025-05-25", 13, 4L},
                    {"2025-05-25", 13, 4L},
                    {"2025-05-25", 16, 4L},
                    {"2025-05-25", 19, 4L},
                    {"2025-05-25", 22, 5L},
                    {"2025-05-25", 21, 5L},
                    {"2025-05-25", 8, 4L},
                    {"2025-05-25", 21, 5L},
                    {"2025-05-25", 8, 4L}
                };

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                
                for (Object[] stock : stocks) {
                    ProductStock productStock = new ProductStock();
                    productStock.setDate(LocalDate.parse((String) stock[0], formatter));
                    productStock.setStock((Integer) stock[1]);
                    productForSaleRepository.findById((Long) stock[2])
                        .ifPresent(productStock::setProductForSale);
                    productStockRepository.save(productStock);
                }
                logger.info("Stock de productos cargado exitosamente");
            }

            // Inicializar HistoricalPrice
            if (historicalPriceRepository.count() == 0) {
                logger.info("Iniciando carga de precios históricos...");
                
                Object[][] precios = {
                    {"2023-12-01", 15000.0, 1L},
                    {"2023-12-05", 8000.0, 2L},
                    {"2023-12-10", 3000.0, 3L},
                    {"2023-12-15", 2000.0, 4L},
                    {"2023-12-20", 45000.0, 5L},
                    {"2025-05-24", 69.5, 6L},
                    {"2025-05-24", 150000.0, 7L},
                    {"2025-05-24", 69000.0, 6L},
                    {"2025-05-24", 69000.0, 6L},
                    {"2025-05-24", 89000.0, 6L},
                    {"2025-05-24", 69000.0, 6L},
                    {"2025-05-24", 99000.0, 6L},
                    {"2025-05-24", 185000.0, 8L}
                };

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                
                for (Object[] precio : precios) {
                    HistoricalPrice historicalPrice = new HistoricalPrice();
                    historicalPrice.setDate(LocalDate.parse((String) precio[0], formatter));
                    historicalPrice.setPrice((Double) precio[1]);
                    productRepository.findById((Long) precio[2])
                        .ifPresent(historicalPrice::setProduct);
                    historicalPriceRepository.save(historicalPrice);
                }
                logger.info("Precios históricos cargados exitosamente");
            }

        } catch (Exception e) {
            logger.error("Error en la inicialización: " + e.getMessage());
            e.printStackTrace();
        }
    }
}