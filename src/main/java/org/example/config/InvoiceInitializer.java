package org.example.config;

import org.example.entity.Invoice;
import org.example.entity.InvoiceDetail;
import org.example.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Order(8)
public class InvoiceInitializer implements CommandLineRunner {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ProductForSaleRepository productForSaleRepository;

    private final Logger logger = LoggerFactory.getLogger(InvoiceInitializer.class);

    @Override
    public void run(String... args) {
        try {
            if (invoiceRepository.count() == 0) {
                logger.info("Iniciando carga de facturas...");

                Object[][] facturas = {
                    {"2024-02-01", 1L},
                    {"2024-02-02", 2L},
                    {"2024-02-03", 3L},
                    {"2024-02-04", 4L},
                    {"2024-02-05", 5L},
                    {"2024-02-01", 3L},
                    {"2024-02-01", 1L}
                };

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Crear y guardar las facturas
                for (Object[] factura : facturas) {
                    Invoice invoice = new Invoice();
                    invoice.setDate(LocalDate.parse((String) factura[0], formatter));
                    
                    personRepository.findById((Long) factura[1])
                        .ifPresent(invoice::setPerson);
                    
                    invoiceRepository.save(invoice);
                }
                logger.info("Facturas cargadas exitosamente");
            }

            if (invoiceDetailRepository.count() == 0) {
                logger.info("Iniciando carga de detalles de facturas...");

                Object[][] detalles = {
                    {1, 1L, 1L},
                    {2, 1L, 2L},
                    {1, 2L, 3L},
                    {3, 3L, 4L},
                    {2, 4L, 5L},
                    {1, 5L, 5L},
                    {2, 5L, 4L},
                    {1, 6L, 5L},
                    {2, 7L, 4L}
                };

                // Crear y guardar los detalles de factura
                for (Object[] detalle : detalles) {
                    InvoiceDetail invoiceDetail = new InvoiceDetail();
                    invoiceDetail.setQuantity((Integer) detalle[0]);

                    invoiceRepository.findById((Long) detalle[1])
                        .ifPresent(invoiceDetail::setInvoice);
                    
                    productForSaleRepository.findById((Long) detalle[2])
                        .ifPresent(invoiceDetail::setProductForSale);

                    invoiceDetailRepository.save(invoiceDetail);
                }
                logger.info("Detalles de facturas cargados exitosamente");
            }

        } catch (Exception e) {
            logger.error("Error en la inicialización de facturas y detalles: " + e.getMessage());
            e.printStackTrace();
        }
    }
}