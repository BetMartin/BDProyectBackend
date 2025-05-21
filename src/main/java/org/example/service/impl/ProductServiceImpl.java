package org.example.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.OrderDTO;
import org.example.dto.ProductDTO;
import org.example.dto.ProductDetailDTO;
import org.example.dto.SizeDTO;
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
        productDTO.setPrice(historicalPriceRepository.findLatestPriceByProductId(product.getId())
                .map(HistoricalPrice::getPrice)
                .orElse(null));

        // Sumar la cantidad total vendida
        productDTO.setQuantitySold(invoiceDetailRepository.findTotalQuantitySoldByProductId(product.getId()));

        // Obtener los tamaños asociados al producto
        List<SizeDTO> sizes = productSizesRepository.findSizesByProductId(product.getId())
                .stream()
                .map(this::convertToSizeDTO)
                .collect(Collectors.toList());
        productDTO.setSize(sizes);

        // Obtener los detalles de las órdenes
        List<Invoice> invoices = invoiceDetailRepository.findInvoicesByProductId(product.getId());
        productDTO.setOrderDetail(
                invoices.stream()
                        .map(this::convertToProductDetailDTO)
                        .collect(Collectors.toList())
        );

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
            historicalPrice.setPrice(productDTO.getPrice());
            historicalPrice.setDate(LocalDate.from(java.time.LocalDateTime.now()));
            historicalPriceRepository.save(historicalPrice);
        }

        // Guardar los tamaños si existen
        if (productDTO.getSize() != null && !productDTO.getSize().isEmpty()) {
            productDTO.getSize().forEach(sizeDTO -> {
                ProductSizes productSize = new ProductSizes();
                productSize.setProduct(savedProduct);
                // Aquí asumimos que tienes una forma de obtener o crear el Size
                Size size = new Size();
                size.setSizeNumber(sizeDTO.getSize());
                productSize.setSize(size);
                productSizesRepository.save(productSize);
            });
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

    private SizeDTO convertToSizeDTO(Size size) {
        SizeDTO sizeDTO = new SizeDTO();
        sizeDTO.setId(size.getId());
        sizeDTO.setSize(size.getSizeNumber());
        return sizeDTO;
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