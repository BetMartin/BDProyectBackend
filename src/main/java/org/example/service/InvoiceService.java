package org.example.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.example.dto.OrderDTO;
import org.example.dto.ProductDetailDTO;
import org.example.entity.*;
import org.example.mapper.OrderMapper;
import org.example.repository.InvoiceRepository;
import org.example.repository.ProductForSaleRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailService invoiceDetailService;
    private final ProductForSaleRepository productForSaleRepository;
    private final ProductStockService productStockService;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {
        // Utiliza el metodo personalizado que incluye FETCH para cargar los detalles
        List<Invoice> invoices = invoiceRepository.findAllWithDetails();
        return orderMapper.toDtoList(invoices);
    }

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        return invoiceRepository.findByIdWithDetails(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));

    }

    @Transactional
    public OrderDTO createInvoiceWithDetails(OrderDTO invoiceDTO) {

        // Buscar el usuario
        User user = userRepository.findById(invoiceDTO.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + invoiceDTO.getUser().getId()));


        // Crear instancia de Invoice
        Invoice invoice = new Invoice();
        invoice.setDate(LocalDate.parse(invoiceDTO.getFecha()));
        invoice.setPerson(user.getPerson());

        invoice.setDetails(new ArrayList<>());

        // Procesar cada detalle del JSON
        for (ProductDetailDTO detailDTO : invoiceDTO.getDetalles()) {
            // Buscar combinación de product y size en ProductForSale
            Optional<ProductForSale> productForSaleOpt = productForSaleRepository
                    .findByProductIdAndSizeId(
                            detailDTO.getProductStock().getProduct().getId(),
                            detailDTO.getProductStock().getSize().getId()
                    );
            if (productForSaleOpt.isEmpty()) {
                throw new RuntimeException("No existe un registro de ProductForSale para el producto con ID: "
                        + detailDTO.getProductStock().getProduct().getId()
                        + " y talle con ID: " + detailDTO.getProductStock().getSize().getId());
            }

            ProductForSale productForSale = productForSaleOpt.get();

            // Validar si es posible crear el detalle (suficiente stock)
            int stockActual = productForSale.stockActualProductSize();
            if (detailDTO.getQuantity() > stockActual) {
                throw new RuntimeException("Stock insuficiente para el producto con ID: "
                        + detailDTO.getProductStock().getProduct().getId()
                        + " y talle con ID: " + detailDTO.getProductStock().getSize().getId()
                        + ". Stock actual: " + stockActual + ", requerido: " + detailDTO.getQuantity());
            }

            // Crear y guardar el detalle
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            invoiceDetail.setInvoice(invoice);
            invoiceDetail.setProductForSale(productForSale);
            invoiceDetail.setQuantity(detailDTO.getQuantity());
            invoice.getDetails().add(invoiceDetail);

            // Actualizar el stock registrando el movimiento
            ProductStock nuevoStock = new ProductStock();
            nuevoStock.setProductForSale(productForSale);
            nuevoStock.setStock(stockActual - detailDTO.getQuantity());
            productStockService.save(nuevoStock);
        }

        // Guardar la factura
        invoiceRepository.save(invoice);

        return orderMapper.toDto(invoice);
    }

    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));

        // Eliminar primero los detalles
        if (invoice.getDetails() != null) {
            for (InvoiceDetail detail : invoice.getDetails()) {
                invoiceDetailService.delete(detail.getId());
            }
        }

        invoiceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Double calculateTotal(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .map(Invoice::getTotal)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + invoiceId));
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return invoiceRepository.findAll().stream()
                .filter(invoice -> !invoice.getDate().isBefore(startDate) && !invoice.getDate().isAfter(endDate))
                .map(orderMapper::toDto)
                .toList();
    }

    private void validateInvoice(Invoice invoice) {
        if (invoice.getDate() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (invoice.getPerson() == null) {
            throw new IllegalArgumentException("La persona es obligatoria");
        }
        if (invoice.getDetails() == null || invoice.getDetails().isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un detalle");
        }
    }
    public byte[] generateInvoicePdf(Invoice invoice) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        try {
            // Título de la factura
            Paragraph title = new Paragraph("FACTURA")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Información de la empresa
            Paragraph companyInfo = new Paragraph("Mi Empresa S.A.\nRFC: XXXX000000XXX\nDirección: Calle Principal #123\nTeléfono: (123) 456-7890")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(companyInfo);

            document.add(new Paragraph("\n"));

            // Información de la factura
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            infoTable.addCell(createCell("Factura #:", true));
            infoTable.addCell(createCell(invoice.getId().toString(), false));

            infoTable.addCell(createCell("Fecha:", true));
            infoTable.addCell(createCell(invoice.getDate().toString(), false));

            infoTable.addCell(createCell("Cliente:", true));
            infoTable.addCell(createCell(invoice.getPerson().getFirstName(), false));

            infoTable.addCell(createCell("Email:", true));
            infoTable.addCell(createCell(invoice.getPerson().getUser().getUsername(), false));

            document.add(infoTable);

            document.add(new Paragraph("\n"));

            // Detalle de los productos
            Table productTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            productTable.addHeaderCell(createHeaderCell("Producto"));
            productTable.addHeaderCell(createHeaderCell("Cantidad"));
            productTable.addHeaderCell(createHeaderCell("Precio"));
            productTable.addHeaderCell(createHeaderCell("Subtotal"));

            for (InvoiceDetail detail : invoice.getDetails()) {
                String productName = detail.getProductForSale().getProduct().getProduct(); // Asumiendo que este es el nombre
                productTable.addCell(createCell(productName, false));
                productTable.addCell(createCell(String.valueOf(detail.getQuantity()), false));

                // Calcular el precio unitario si no está directamente disponible
                double unitPrice = detail.getSubtotal() / detail.getQuantity();
                productTable.addCell(createCell(String.format("$%.2f", unitPrice), false));
                productTable.addCell(createCell(String.format("$%.2f", detail.getSubtotal()), false));
            }

            document.add(productTable);

            document.add(new Paragraph("\n"));

            // Totales
            Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{4, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Obtener el subtotal (sin IVA)
            double subtotal = invoice.getTotal() / 1.16; // Asumiendo IVA del 16%
            double tax = invoice.getTotal() - subtotal;

            totalsTable.addCell(createCell("Subtotal:", true).setTextAlignment(TextAlignment.RIGHT));
            totalsTable.addCell(createCell(String.format("$%.2f", subtotal), false));

            totalsTable.addCell(createCell("IVA (16%):", true).setTextAlignment(TextAlignment.RIGHT));
            totalsTable.addCell(createCell(String.format("$%.2f", tax), false));

            totalsTable.addCell(createCell("TOTAL:", true).setTextAlignment(TextAlignment.RIGHT).setFontSize(12));
            totalsTable.addCell(createCell(String.format("$%.2f", invoice.getTotal()), true));

            document.add(totalsTable);

            // Información de pago
            document.add(new Paragraph("\n"));
            Paragraph paymentInfo = new Paragraph("Pago realizado a través de MercadoPago")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(paymentInfo);

            Paragraph thankYou = new Paragraph("¡Gracias por su compra!")
                    .setFontSize(12)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(thankYou);

        } finally {
            document.close();
        }

        return baos.toByteArray();
    }

    // Métodos auxiliares para crear celdas
    private Cell createCell(String content, boolean isBold) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (isBold) {
            cell.setBold();
        }
        return cell;
    }

    private Cell createHeaderCell(String content) {
        Cell cell = new Cell().add(new Paragraph(content).setBold());
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cell.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        return cell;
    }

}