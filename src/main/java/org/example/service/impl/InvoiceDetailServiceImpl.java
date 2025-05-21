package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.ProductDetailDTO;
import org.example.entity.Invoice;
import org.example.entity.InvoiceDetail;
import org.example.entity.Product;
import org.example.Repository.InvoiceDetailRepository;
import org.example.Repository.ProductRepository;
import org.example.service.ServiceInterface.InvoiceDetailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceDetailServiceImpl implements InvoiceDetailService {

    private final InvoiceDetailRepository invoiceDetailRepository;
    private final ProductRepository productRepository;

    @Override
    public List<InvoiceDetail> createInvoiceDetails(List<ProductDetailDTO> productDetails, Invoice invoice) {
        // Mapear cada ProductDetailDTO a InvoiceDetail
        return productDetails.stream().map(dto -> {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoice(invoice);
            detail.setQuantity(dto.getQuantity());

            // Encontrar el producto asociado a través de productSize
            Product product = productRepository.findById(dto.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            detail.getProductSize().setProduct(product);

            // Obtener el precio histórico más reciente del producto
            double historicalPrice = product.getHistoricalPrices().stream()
                    .max((a, b) -> a.getDate().compareTo(b.getDate()))
                    .orElseThrow(() -> new RuntimeException("No hay precios históricos disponibles"))
                    .getPrice();
            return invoiceDetailRepository.save(detail);
        }).collect(Collectors.toList());
    }
}