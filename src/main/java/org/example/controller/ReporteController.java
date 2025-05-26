package org.example.controller;


import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.ProductDTO;
import org.example.entity.Invoice;
import org.example.repository.InvoiceRepository;
import org.example.service.ProductPdfGenerator;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReporteController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private InvoiceRepository graficoManager;

    // Gráfico de productos por categoría (tipo torta)
    @GetMapping("/grafico-torta")
    public List<Map<String, Object>> getGraficoCategorias() {
        List<Object[]> resultados = graficoManager.findProductosMasVendidos();

        return resultados.stream()
                .map(resultado -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("producto", resultado[0]);
                    item.put("cantidad", resultado[1]);
                    return item;
                })
                .collect(Collectors.toList());
    }


    // Gráfico de ventas mensuales (tipo barra)
    @GetMapping("/grafico-barras")
    public List<Map<String, Object>> getGraficoVentas() {
        List<Object[]> resultados = graficoManager.findVentasMensuales();

        return resultados.stream()
                .map(resultado -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("año", resultado[0]);
                    item.put("mes", resultado[1]);
                    item.put("cantidadVentas", resultado[2]);
                    item.put("totalVentas", resultado[3]);
                    item.put("periodo", String.format("%d-%02d", resultado[0], resultado[1]));
                    return item;
                })
                .collect(Collectors.toList());
    }

    // Exportar a Excel
    @GetMapping("/excel")
    public void exportarExcel(
            @RequestParam("desde") String desde,
            @RequestParam("hasta") String hasta,
            HttpServletResponse response
    ) throws IOException {
        try {
            LocalDate fechaDesde = LocalDate.parse(desde);
            LocalDate fechaHasta = LocalDate.parse(hasta);

            ByteArrayInputStream stream = productService.generarReporteExcel(fechaDesde, fechaHasta);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_productos.xlsx");

            StreamUtils.copy(stream, response.getOutputStream());
        } catch (DateTimeParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha inválido. Usar yyyy-MM-dd.");
        }
    }

    // Descargar PDF
    @GetMapping("/pdf/{idProducto}")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long idProducto) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ProductDTO producto = productService.getProductoById(idProducto);
            if (producto == null) {
                return ResponseEntity.notFound().build();
            }

            ProductPdfGenerator pdfManager = new ProductPdfGenerator();
            pdfManager.generarPdfProducto(producto, outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "producto_" + idProducto + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}