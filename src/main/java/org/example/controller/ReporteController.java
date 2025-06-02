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
    public ResponseEntity<Map<String, Object>> getGraficoCategorias() {
        List<Object[]> resultados = graficoManager.findProductosMasVendidos();

        // Crear la estructura de datos compatible con Google Charts
        Object[][] chartData = new Object[resultados.size() + 1][];

        // Definir encabezados
        chartData[0] = new Object[]{"Producto", "Cantidad"};

        // Llenar datos
        for (int i = 0; i < resultados.size(); i++) {
            Object[] resultado = resultados.get(i);
            chartData[i + 1] = new Object[]{resultado[0], resultado[1]};
        }

        Map<String, Object> response = new HashMap<>();
        response.put("chartData", chartData);

        return ResponseEntity.ok(response);
    }


    // Gráfico de ventas mensuales (tipo barra)
    @GetMapping("/grafico-barras")
    public ResponseEntity<Map<String, Object>> getGraficoVentas() {
        List<Object[]> resultados = graficoManager.findVentasMensuales();

        // Crear la estructura de datos compatible con Google Charts
        Object[][] chartData = new Object[resultados.size() + 1][];

        // Definir encabezados
        chartData[0] = new Object[]{"Periodo", "Cantidad Ventas", "Total Ventas"};

        // Llenar datos
        for (int i = 0; i < resultados.size(); i++) {
            Object[] resultado = resultados.get(i);
            String periodo = String.format("%d-%02d", resultado[0], resultado[1]);
            chartData[i + 1] = new Object[]{periodo, resultado[2], resultado[3]};
        }

        Map<String, Object> response = new HashMap<>();
        response.put("chartData", chartData);

        return ResponseEntity.ok(response);
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