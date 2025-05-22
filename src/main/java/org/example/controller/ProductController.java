package org.example.controller;

import org.example.dto.ProductDTO;
import org.example.service.ServiceInterface.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;

    // Directorio base para guardar imágenes de productos
    @Value("${product.image.directory}")
    private String imageDirectory;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Obtener todos los productos (DTOs)
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.findAllDTO();
        return ResponseEntity.ok(products);
    }

    // Obtener un producto por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.findDTOById(id);
        return ResponseEntity.ok(product);
    }

    // Crear un nuevo producto con imagen
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart("image") MultipartFile imageFile) {

        // Manejo de la imagen: guardado en el directorio
        String imagePath = saveImage(imageFile);
        productDTO.setImage(imagePath); // Agregar al DTO la ruta de la imagen

        // Crear el producto
        ProductDTO createdProduct = productService.create(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    // Actualizar un producto y su imagen
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        // Manejo condicional de la imagen
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile); // Guardar nueva imagen
            productDTO.setImage(imagePath); // Actualizar la ruta en el DTO
        }

        // Actualizar el producto
        ProductDTO updatedProduct = productService.update(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    // Eliminar un producto por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Metodo para guardar imágenes en el directorio
    private String saveImage(MultipartFile imageFile) {
        // Verificar que no esté vacío y la configuración del directorio esté correcta
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("No se puede subir una imagen vacía.");
        }

        try {
            // Crear el directorio si no existe
            File directory = new File(imageDirectory);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("Directorio creado: " + imageDirectory);
                } else {
                    throw new IOException("No se pudo crear el directorio para guardar imágenes.");
                }
            }

            // Crear el archivo donde se guardará la imagen
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            File file = new File(directory, fileName);

            // Guardar el archivo
            imageFile.transferTo(file);
            return file.getAbsolutePath(); // Retornar la ruta absoluta donde se guardó
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
        }
    }
}