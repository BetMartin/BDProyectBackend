package org.example.service;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import org.example.dto.ProductDTO;
import org.example.dto.ProductDetailDTO;
import org.example.dto.ProductSizeDTO;

import java.io.ByteArrayOutputStream;
import java.net.URL;

public class ProductPdfGenerator {
    
    public void generarPdfProducto(ProductDTO producto, ByteArrayOutputStream outputStream) {
        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph title = new Paragraph("Detalle del Producto")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Información básica
            document.add(new Paragraph("Nombre: " + producto.getProduct()));
            document.add(new Paragraph("Marca: " + producto.getBrand()));
            document.add(new Paragraph("Modelo: " + producto.getModel()));
            document.add(new Paragraph("Precio: $" + producto.getPrice()));

            // Descripción
            document.add(new Paragraph("Descripción:").setBold());
            document.add(new Paragraph(producto.getDescription()));

            // Imagen
            if (producto.getImage() != null && !producto.getImage().isEmpty()) {
                try {
                    String imagePath = "http://localhost:8080/img/" + producto.getImage();
                    ImageData imageData = ImageDataFactory.create(imagePath);
                    Image img = new Image(imageData).scaleToFit(200, 200).setMarginBottom(10);
                    img.setHorizontalAlignment(HorizontalAlignment.CENTER);

                    document.add(img);
                } catch (Exception e) {
                    document.add(new Paragraph("No se pudo cargar la imagen del producto"));
                }
            }

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }
}