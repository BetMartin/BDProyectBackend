package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.ProductDetailDTO;
import org.example.entity.Invoice;
import org.example.entity.InvoiceDetail;
import org.example.entity.ProductForSale;
import org.example.entity.ProductStock;
import org.example.mapper.ProductDetailMapper;
import org.example.repository.InvoiceDetailRepository;
import org.example.repository.ProductForSaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceDetailService {

    private final InvoiceDetailRepository invoiceDetailRepository;
    private final ProductDetailMapper productDetailMapper;
    private final ProductStockService productStockService;
    private final ProductForSaleRepository productForSaleRepository;

    @Transactional(readOnly = true)
    public List<ProductDetailDTO> findAll() {
        return productDetailMapper.toDtoList(invoiceDetailRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<ProductDetailDTO> findById(Long id) {
        return invoiceDetailRepository.findById(id)
                .map(productDetailMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<ProductDetailDTO> findByOrderId(Long orderId) {
        return invoiceDetailRepository.findByInvoiceId(orderId)
                .stream()
                .map(productDetailMapper::toDto)
                .collect(Collectors.toList());
    }
    public ProductDetailDTO create(ProductDetailDTO dto) {
        InvoiceDetail invoiceDetail = productDetailMapper.toEntity(dto);

        // Validar y actualizar stock
        // Buscar si existe la combinación producto-talle
        Optional<ProductForSale> existingProductForSale = productForSaleRepository
                .findByProductIdAndSizeId(dto.getProductStock().getProduct().getId(), dto.getProductStock().getSize().getId());

        ProductStock nuevoStock = invoiceDetail.actualizarStock();
        productStockService.save(nuevoStock);
        
        InvoiceDetail savedDetail = invoiceDetailRepository.save(invoiceDetail);
        return productDetailMapper.toDto(savedDetail);
    }

    public ProductDetailDTO update(Long id, ProductDetailDTO dto) {
        if (!invoiceDetailRepository.existsById(id)) {
            throw new RuntimeException("Detalle de factura no encontrado con id: " + id);
        }

        // Obtener el detalle actual
        InvoiceDetail currentDetail = invoiceDetailRepository.findById(id).get();
        InvoiceDetail newDetail = productDetailMapper.toEntity(dto);
        newDetail.setId(id);

        // Si la cantidad cambió, actualizar el stock
        if (!currentDetail.getQuantity().equals(newDetail.getQuantity())) {
            // Revertir el stock anterior
            ProductStock revertStock = new ProductStock();
            revertStock.setProductForSale(currentDetail.getProductForSale());
            revertStock.setStock(currentDetail.getProductForSale().stockActualProductSize() + currentDetail.getQuantity());
            productStockService.save(revertStock);

            // Aplicar el nuevo stock
            ProductStock nuevoStock = newDetail.actualizarStock();
            productStockService.save(nuevoStock);
        }

        InvoiceDetail updatedDetail = invoiceDetailRepository.save(newDetail);
        return productDetailMapper.toDto(updatedDetail);
    }

    public void delete(Long id) {
        InvoiceDetail detail = invoiceDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de factura no encontrado con id: " + id));

        // Revertir el stock
        ProductStock revertStock = new ProductStock();
        revertStock.setProductForSale(detail.getProductForSale());
        revertStock.setStock(detail.getProductForSale().stockActualProductSize() + detail.getQuantity());
        productStockService.save(revertStock);

        invoiceDetailRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductDetailDTO> findByInvoice(Long invoiceId) {
        return invoiceDetailRepository.findAll().stream()
                .filter(detail -> detail.getInvoice().getId().equals(invoiceId))
                .map(productDetailMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Integer getTotalQuantitySoldByProduct(Long productId) {
        return invoiceDetailRepository.findTotalQuantitySoldByProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByProduct(Long productId) {
        return invoiceDetailRepository.findInvoicesByProductId(productId);
    }

    @Transactional(readOnly = true)
    public Double calculateSubtotal(Long detailId) {
        return invoiceDetailRepository.findById(detailId)
                .map(InvoiceDetail::getSubtotal)
                .orElseThrow(() -> new RuntimeException("Detalle de factura no encontrado con id: " + detailId));
    }

    private void validateInvoiceDetail(InvoiceDetail detail) {
        if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }
        if (detail.getProductForSale() == null) {
            throw new IllegalArgumentException("El producto y talle son obligatorios");
        }
        if (detail.getInvoice() == null) {
            throw new IllegalArgumentException("La factura es obligatoria");
        }
    }
}