package org.example.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.*;
import org.example.entity.*;
import org.example.Repository.*;
import org.example.service.ServiceInterface.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final HistoricalPriceRepository historicalPriceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final ProductSizesRepository productSizesRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              HistoricalPriceRepository historicalPriceRepository,
                              InvoiceDetailRepository invoiceDetailRepository,
                              ProductSizesRepository productSizesRepository,
                              InvoiceRepository invoiceRepository) {
        this.productRepository = productRepository;
        this.historicalPriceRepository = historicalPriceRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.productSizesRepository = productSizesRepository;
    }
    @Override
    public List<ProductDTO> findAllDTO() {
        List<Product> products = productRepository.findAll(); // Supongamos que tienes un repositorio
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ProductDTO findDTOById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
        return toDTO(product);
    }

    @Override
    public ProductDTO toDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        
        // Copiar atributos directos del producto
        productDTO.setId(product.getId());
        productDTO.setProduct(product.getProduct());
        productDTO.setBrand(product.getBrand());
        productDTO.setModel(product.getModel());
        productDTO.setImage(product.getImage());
        productDTO.setDescription(product.getDescription());

        // Buscar el precio más reciente
        Double latestPrice = historicalPriceRepository.findLatestPriceByProductId(product.getId())
                .map(HistoricalPrice::getPrice)
                .orElse(null);
        productDTO.setPrice(latestPrice != null ? latestPrice.toString() : null);

        // Configurar la categoría
        if (product.getProductCategory() != null) {
            ProductCategoryDTO categoryDTO = new ProductCategoryDTO();
            categoryDTO.setId(product.getProductCategory().getId());
            categoryDTO.setName(product.getProductCategory().getName());
            productDTO.setCategory(categoryDTO);
        }

        // Obtener los tamaños asociados al producto
        List<ProductSizeDTO> sizes = productSizesRepository.findSizesByProductId(product.getId())
                .stream()
                .map(size -> {
                    ProductSizeDTO sizeDTO = new ProductSizeDTO();
                    sizeDTO.setId(size.getId());
                    sizeDTO.setSize(size.getSizeNumber());
                    return sizeDTO;
                })
                .collect(Collectors.toList());
        productDTO.setSizes(sizes);

//        // Obtener los detalles de producto
//        List<ProductDetailDTO> productDetails = invoiceDetailRepository.findInvoicesByProductId(product.getId())
//                .stream()
//                .map(invoice -> {
//                    ProductDetailDTO detailDTO = new ProductDetailDTO();
//                    detailDTO.setId(invoice);
//                    detailDTO.setQuantity(productDetails.getQuantity());
//                    detailDTO.setSubtotal(invoice.getSubtotal());
//
//                    // Crear y configurar ProductStock
//                    ProductStockDTO stockDTO = new ProductStockDTO();
//                    stockDTO.setId(invoice.getProductStock().getId());
//                    stockDTO.setStock(invoice.getProductStock().getStock());
//                    stockDTO.setProduct(productDTO); // Referencia circular al producto actual
//
//                    // Configurar el tamaño en ProductStock
//                    ProductSizeDTO sizeDTO = new ProductSizeDTO();
//                    sizeDTO.setId(invoice.getProductStock().getSize().getId());
//                    sizeDTO.setSize(invoice.getProductStock().getSize().getSize());
//                    stockDTO.setSize(sizeDTO);
//
//                    detailDTO.setProductStock(stockDTO);
//
//                    // Configurar Order si existe
//                    if (invoice.getOrder() != null) {
//                        OrderDTO orderDTO = new OrderDTO();
//                        orderDTO.setId(invoice.getOrder().getId());
//                        orderDTO.setFecha(invoice.getOrder().getFecha());
//                        orderDTO.setTotal(invoice.getOrder().getTotal());
//                        detailDTO.setOrder(orderDTO);
//                    }
//
//                    return detailDTO;
//                })
//                .collect(Collectors.toList());
//        productDTO.setProductDetail(productDetails);

        return productDTO;
    }


    @Override
    public ProductDTO create(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException("El ProductDTO no puede ser nulo");
        }

        // Crear nueva entidad Product
        Product product = new Product();
        product.setProduct(productDTO.getProduct());
        product.setBrand(productDTO.getBrand());
        product.setModel(productDTO.getModel());
        product.setImage(productDTO.getImage());
        product.setDescription(productDTO.getDescription());

        // Guardar el producto
        Product savedProduct = productRepository.save(product);

        // Crear y guardar el precio histórico inicial si existe
        if (productDTO.getPrice() != null) {
            HistoricalPrice historicalPrice = new HistoricalPrice();
            historicalPrice.setProduct(savedProduct);
            historicalPrice.setPrice(Double.valueOf(productDTO.getPrice()));
            historicalPrice.setDate(LocalDate.from(java.time.LocalDateTime.now()));
            historicalPriceRepository.save(historicalPrice);
        }

        return toDTO(savedProduct);
    }

    @Override
    public ProductDTO update(Long id, ProductDTO productDTO) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    private ProductSizeDTO convertToSizeDTO(Size size) {
        ProductSizeDTO productSizeDTO = new ProductSizeDTO();
        productSizeDTO.setId(size.getId());
        productSizeDTO.setSize(size.getSizeNumber());
        return productSizeDTO;
    }

    private OrderDTO convertToInvoiceDTO(Invoice invoice) {
        OrderDTO invoiceDTO = new OrderDTO();
        invoiceDTO.setId(invoice.getId());
        invoiceDTO.setFecha(String.valueOf(invoice.getDate()));
        return invoiceDTO;
    }

    private ProductDetailDTO convertToProductDetailDTO(Invoice invoice) {
        ProductDetailDTO detailDTO = new ProductDetailDTO();
        detailDTO.setId(invoice.getId());
        return detailDTO;
    }


}