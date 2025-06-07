package org.example.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Slf4j
@Service
public class ImageService {

    private Path uploadDir =Paths.get("uploads/img");

    @PostConstruct
    public void setUp() {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("Directorio de imágenes creado en: {}", uploadDir.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("No se pudo crear el directorio para las imágenes", e);
            throw new RuntimeException("No se pudo crear el directorio para las imágenes", e);
        }
    }


    public String saveImage(MultipartFile file) {
        validateImage(file);
        
        try {
            // Generar nombre único para el archivo
            String filename = generateUniqueFilename(file.getOriginalFilename());
            
            // Copiar el archivo al directorio de imágenes
            Path destinationFile = uploadDir.resolve(filename)
                    .normalize()
                    .toAbsolutePath();
            
            // Verificar que el destino está dentro del directorio permitido
            if (!destinationFile.getParent().equals(uploadDir.toAbsolutePath())) {
                throw new SecurityException("No se puede almacenar el archivo fuera del directorio designado");
            }

            // Guardar el archivo
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            
            log.info("Imagen guardada exitosamente: {}", filename);
            return filename;
            
        } catch (IOException e) {
            log.error("Error al guardar la imagen", e);
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage());
        }
    }


    public void deleteImage(String filename) {
        try {
            Path file = uploadDir.resolve(filename);
            Files.deleteIfExists(file);
            log.info("Imagen eliminada exitosamente: {}", filename);
        } catch (IOException e) {
            log.error("Error al eliminar la imagen: {}", filename, e);
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
        }
    }


    public Path getImagePath(String filename) {
        return uploadDir.resolve(filename).normalize();
    }


    public boolean imageExists(String filename) {
        Path file = uploadDir.resolve(filename);
        return Files.exists(file);
    }


    private String generateUniqueFilename(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }


    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isImageContentType(contentType)) {
            throw new IllegalArgumentException("El archivo debe ser una imagen (JPEG, PNG, GIF)");
        }

        // Validar tamaño máximo (por ejemplo, 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB en bytes
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido de 5MB");
        }
    }


    private boolean isImageContentType(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif");
    }

}