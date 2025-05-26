package org.example.config;

import org.example.entity.ProductCategory;
import org.example.entity.Size;
import org.example.repository.ProductCategoryRepository;
import org.example.repository.SizeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Order(1)

public class DataInitializer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final SizeRepository sizeRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public DataInitializer(SizeRepository sizeRepository, ProductCategoryRepository productCategoryRepository) {
        this.sizeRepository = sizeRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public void run(String... args) {
        try {
            if (sizeRepository.count() == 0) {
                logger.info("Iniciando carga de tallas...");

                List<Integer> tallas = Arrays.asList(38, 39, 40, 41, 42);

                tallas.forEach(talla -> {
                    Size size = new Size();
                    size.setSizeNumber(talla);
                    sizeRepository.save(size);
                });
                logger.info("Tallas cargadas exitosamente");
            } else {
                logger.info("La tabla de tallas ya contiene datos, saltando inicialización");
            }
        } catch (Exception e) {
            logger.error("Error durante la inicialización de tallas: " + e.getMessage());
        }
        try {
            if (productCategoryRepository.count() == 0) {
                logger.info("Iniciando carga de categorías de productos...");

                String[] categorias = {"RUNNING", "URBAN", "OUTDOOR", "TREKKING", "SKATER"};

                for (String nombreCategoria : categorias) {
                    ProductCategory category = new ProductCategory();
                    category.setName(nombreCategoria);
                    productCategoryRepository.save(category);
                }

                logger.info("Categorías de productos cargadas exitosamente");
            } else {
                logger.info("La tabla de categorías ya contiene datos, saltando inicialización");
            }
        } catch (Exception e) {
            logger.error("Error durante la inicialización de categorías: " + e.getMessage());
        }
    }
}
