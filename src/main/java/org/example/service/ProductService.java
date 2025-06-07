package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.dto.ProductCategoryDTO;
import org.example.dto.ProductDTO;
import org.example.dto.ProductSizeDTO;
import org.example.entity.*;
import org.example.mapper.ProductCategoryMapper;
import org.example.mapper.ProductMapper;
import org.example.repository.InvoiceRepository;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ImageService imageService;
    private final PrecioHistoricoService precioHistoricoService;
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductCategoryService productCategoryService;
    private final InvoiceRepository invoiceRepository;
    
   

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        List<Product> products = productRepository.findByActivoTrue();

        // Convertimos cada producto en ProductDTO y le asignamos la categoría.
        return products.stream()
                .map(product -> {
                    ProductDTO dto = productMapper.toDto(product);
                    Double lastPrice = product.precioActual();
                    dto.setPrice(lastPrice != null ? lastPrice.toString() : "0.0");

                    // Asignar la categoría al ProductDTO.
                    if (product.getProductCategory() != null) {
                        ProductCategory category = product.getProductCategory();
                        category.setProducts(null); // Establecemos products a null para evitar la recursión
                        dto.setCategory(productCategoryMapper.toDto(category));
                    }
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        ProductDTO dto = productMapper.toDto(product);

        // Asignar la categoría al ProductDTO
        if (product.getProductCategory() != null) {
            ProductCategory category = product.getProductCategory();
            category.setProducts(null); // Establecemos products a null para evitar la recursión
            dto.setCategory(productCategoryMapper.toDto(category));
        }
        Double lastPrice = product.precioActual();
        dto.setPrice(lastPrice != null ? lastPrice.toString() : "0.0");


        return dto;
    }

@Transactional
public ProductDTO create(ProductDTO productDTO, MultipartFile imagen) {
    // Guardar la imagen y obtener su nombre
    String imageName = null;
    if (imagen != null && !imagen.isEmpty()) {
        imageName = imageService.saveImage(imagen);
    }

    // Obtener la categoría
    ProductCategoryDTO categoryDTO = productCategoryService.findById(productDTO.getCategory().getId());

    // Crear entidad
    Product product = productMapper.toEntity(productDTO);

    // Setear valores adicionales
    product.setImage(imageName);
    product.setProductCategory(productCategoryMapper.toEntity(categoryDTO));
    
    // **Forzar activo en true para nuevo producto**
    product.setActivo(true);

    // Guardar producto
    Product savedProduct = productRepository.save(product);

    // Crear precio histórico inicial si aplica
    if (productDTO.getPrice() != null && Double.parseDouble(productDTO.getPrice()) > 0) {
        HistoricalPrice precioHistorico = new HistoricalPrice();
        precioHistorico.setProduct(savedProduct);
        precioHistorico.setPrice(Double.valueOf(productDTO.getPrice()));
        precioHistoricoService.save(precioHistorico);
    }

    return productMapper.toDto(savedProduct);
}

    public ProductDTO update(Long id, ProductDTO productDTO, MultipartFile imagen) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        // Manejar la imagen
        if (imagen != null && !imagen.isEmpty()) {
            // Eliminar imagen anterior si existe
            if (existingProduct.getImage() != null) {
                imageService.deleteImage(existingProduct.getImage());
            }
            String newImageName = imageService.saveImage(imagen);
            existingProduct.setImage(newImageName);
        }

        // Actualizar los campos del producto
        existingProduct.setProduct(productDTO.getProduct());
        existingProduct.setBrand(productDTO.getBrand());
        existingProduct.setModel(productDTO.getModel());
        existingProduct.setDescription(productDTO.getDescription());

        // Si viene una categoría nueva, actualizarla
        if (productDTO.getCategory() != null) {
            ProductCategoryDTO categoryDTO = productCategoryService.findById(productDTO.getCategory().getId());
            existingProduct.setProductCategory(productCategoryMapper.toEntity(categoryDTO));
        }

        //Si viene precio nuevo actualizar precio
        validateAndUpdatePrice(id, Double.parseDouble(productDTO.getPrice()));


        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(updatedProduct);
    }

public void delete(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    product.setActivo(false); // Baja lógica
    productRepository.save(product);
}


    public void updatePrice(Long productId, Double newPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));

        HistoricalPrice precioHistorico = new HistoricalPrice();
        precioHistorico.setProduct(product);
        precioHistorico.setPrice(newPrice);
        precioHistoricoService.save(precioHistorico);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findByCategory(Long categoryId) {
        return productRepository.findByProductCategoryId(categoryId).stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Double getCurrentPrice(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));
        return product.precioActual();
    }

    @Transactional(readOnly = true)
    public List<HistoricalPrice> getPriceHistory(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));
        return precioHistoricoService.findByProduct(product);
    }

    //Filtrar productos por categoria
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsByCategoryName(String categoryName) {
        // Obtenemos la lista de productos según el nombre de la categoría.
        List<Product> products = productRepository.findByProductCategoryName(categoryName);

        // Convertimos cada producto en ProductDTO, le asignamos la categoría y las tallas disponibles.
        return products.stream()
                .map(product -> {
                    ProductDTO dto = productMapper.toDto(product);

                    // Asignar la categoría al ProductDTO.
                    if (product.getProductCategory() != null) {
                        ProductCategory category = product.getProductCategory();
                        category.setProducts(null); // Establecemos products a null para evitar la recursión
                        dto.setCategory(productCategoryMapper.toDto(category));
                    }


                    // Asignar las tallas disponibles al ProductDTO.
                    List<ProductSizeDTO> availableSizes = product.getTallesDisponibles();
                    dto.setSizes(availableSizes);

                    return dto;
                })
                .toList();
    }

    @Transactional
    public ProductDTO assignCategoryToProduct(Long productId, Long categoryId) {
        // Buscar el producto desde el repositorio
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        // Buscar la categoría desde el repositorio
        ProductCategory category = productRepository.findCategoryById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + categoryId));
        category.setProducts(null);

        // Asignar la categoría al producto
        product.setProductCategory(category);

        // Mapear el producto a DTO y retornarlo
        return productMapper.toDto(product);
    }

    @Transactional
    public ProductDTO assignAvailableSizesToProduct(Long productId) {
        // Buscar el producto desde el repositorio
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productId));

        // Obtener las tallas disponibles
        List<ProductSizeDTO> availableSizes = product.getProductForSales().stream()
                .filter(productForSale -> productForSale.stockActualProductSize() > 0)
                .map(productForSale -> ProductSizeDTO.builder()
                        .id(productForSale.getSize().getId())
                        .size(productForSale.getSize().getSizeNumber())
                        .build())
                .collect(Collectors.toList());

        if (availableSizes.isEmpty()) {
            throw new RuntimeException("No hay tallas disponibles para el producto con ID: " + productId);
        }

        // Crear el DTO y asignar las tallas
        ProductDTO productDTO = productMapper.toDto(product);

        // Asegurarnos que la categoría no tenga productos anidados
        if (product.getProductCategory() != null) {
            ProductCategory category = product.getProductCategory();
            category.setProducts(null);
            productDTO.setCategory(productCategoryMapper.toDto(category));
        }

        productDTO.setSizes(availableSizes);

        return productDTO;
    }

    @Transactional
    protected void validateAndUpdatePrice(Long productId, Double newPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Double currentPrice = product.precioActual();

        // Si el precio es diferente o no hay precio actual, crear nuevo registro histórico
        if (currentPrice == null || !newPrice.equals(currentPrice)) {
            HistoricalPrice historicalPrice = new HistoricalPrice();
            historicalPrice.setProduct(product);
            historicalPrice.setPrice(newPrice);
            historicalPrice.setDate(LocalDate.now());
            precioHistoricoService.save(historicalPrice);

            // Actualizar la lista de historicalPrices del producto
            if (product.getHistoricalPrices() == null) {
                product.setHistoricalPrices(new ArrayList<>());
            }
            product.getHistoricalPrices().add(historicalPrice);
            productRepository.save(product);
        }
    }

    public ByteArrayInputStream generarReporteExcel(LocalDate desde, LocalDate hasta) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte de Ventas Mensuales");

            // Estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Estilo para números
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            // Crear encabezados
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Año");
            header.createCell(1).setCellValue("Mes");
            header.createCell(2).setCellValue("Cantidad de Ventas");
            header.createCell(3).setCellValue("Total Ventas ($)");
            header.createCell(4).setCellValue("Promedio por Venta ($)");

            // Aplicar estilo a encabezados
            for (Cell cell : header) {
                cell.setCellStyle(headerStyle);
            }

            // Obtener datos de ventas mensuales
            List<Object[]> ventasMensuales = invoiceRepository.findVentasMensualesPorFecha(desde, hasta);

            // Llenar datos
            int rowIdx = 1;
            for (Object[] venta : ventasMensuales) {
                Row row = sheet.createRow(rowIdx++);

                // Año
                row.createCell(0).setCellValue(((Number) venta[0]).intValue());

                // Mes
                int mes = ((Number) venta[1]).intValue();
                row.createCell(1).setCellValue(obtenerNombreMes(mes));

                // Cantidad de ventas
                row.createCell(2).setCellValue(((Number) venta[2]).intValue());

                // Total ventas
                Cell cellTotal = row.createCell(3);
                cellTotal.setCellValue(((Number) venta[3]).doubleValue());
                cellTotal.setCellStyle(numberStyle);

                // Promedio por venta
                Cell cellPromedio = row.createCell(4);
                double promedio = ((Number) venta[3]).doubleValue() / ((Number) venta[2]).doubleValue();
                cellPromedio.setCellValue(promedio);
                cellPromedio.setCellStyle(numberStyle);
            }

            // Agregar fila de totales
            Row totalRow = sheet.createRow(rowIdx);
            totalRow.createCell(0).setCellValue("TOTAL");
            totalRow.getCell(0).setCellStyle(headerStyle);

            // Fórmulas para totales
            Cell totalVentas = totalRow.createCell(2);
            totalVentas.setCellFormula("SUM(C2:C" + rowIdx + ")");
            totalVentas.setCellStyle(numberStyle);

            Cell totalMonto = totalRow.createCell(3);
            totalMonto.setCellFormula("SUM(D2:D" + rowIdx + ")");
            totalMonto.setCellStyle(numberStyle);

            Cell promedioTotal = totalRow.createCell(4);
            promedioTotal.setCellFormula("D" + (rowIdx + 1) + "/C" + (rowIdx + 1));
            promedioTotal.setCellStyle(numberStyle);

            // Autoajustar columnas
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // Metodo auxiliar para convertir número de mes a nombre
    private String obtenerNombreMes(int mes) {
        return new DateFormatSymbols(new Locale("es", "ES")).getMonths()[mes - 1];
    }

    public ProductDTO getProductoById(Long id) {
        Optional<Product> producto = productRepository.findById(id);
        if (producto.isPresent()) {
            ProductDTO productoDTO = convertirADTO(producto.get());
            return productoDTO;
        }
        return null;
    }

    private ProductDTO convertirADTO(Product producto) {
        ProductDTO dto = new ProductDTO();
        dto.setId(producto.getId());
        dto.setProduct(producto.getProduct());
        dto.setBrand(producto.getBrand());
        dto.setModel(producto.getModel());
        dto.setImage(producto.getImage());
        dto.setPrice(producto.precioActual().toString());
        dto.setDescription(producto.getDescription());
        return dto;
    }

}