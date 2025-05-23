package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ProductDTO;
import org.example.dto.ProductSizeDTO;
import org.example.entity.HistoricalPrice;
import org.example.entity.Product;
import org.example.entity.ProductCategory;
import org.example.mapper.ProductCategoryMapper;
import org.example.mapper.ProductMapper;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
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

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        List<Product> products = productRepository.findAll();

        // Convertimos cada producto en ProductDTO y le asignamos la categoría.
        return products.stream()
                .map(product -> {
                    ProductDTO dto = productMapper.toDto(product);
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
    public Optional<ProductDTO> findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto);
    }

    public ProductDTO create(ProductDTO productDTO, MultipartFile imagen) {
        // Guardar la imagen y obtener su nombre
        String imageName = null;
        if (imagen != null && !imagen.isEmpty()) {
            imageName = imageService.saveImage(imagen);
        }

        // Crear y guardar el producto
        Product product = productMapper.toEntity(productDTO);
        product.setImage(imageName);
        Product savedProduct = productRepository.save(product);

        // Crear el precio histórico inicial
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
            existingProduct.setProductCategory(productCategoryMapper.toEntity(productDTO.getCategory()));
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(updatedProduct);
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        // Eliminar la imagen asociada si existe
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            imageService.deleteImage(product.getImage());
        }

        productRepository.deleteById(id);
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
}