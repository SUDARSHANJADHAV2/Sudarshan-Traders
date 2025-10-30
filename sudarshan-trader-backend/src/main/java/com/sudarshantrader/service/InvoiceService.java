package com.sudarshantrader.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.sudarshantrader.entity.Order;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    public String generateInvoicePdf(Order order) {
        try {
            String dir = "invoices";
            File d = new File(dir);
            if (!d.exists())
                d.mkdirs();
            String path = dir + "/invoice-" + order.getId() + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font body = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("Sudarshan Trader - Invoice", h1));
            document.add(new Paragraph("Order ID: " + order.getId(), body));
            document.add(new Paragraph("Buyer: " + order.getBuyer().getCompanyName(), body));
            document.add(new Paragraph(
                    "Contact: " + order.getBuyer().getContactPerson() + " | " + order.getBuyer().getPhone(), body));
            document.add(new Paragraph("Created: " + order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    body));
            document.add(Chunk.NEWLINE);

            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("SKU");
            table.addCell("Product");
            table.addCell("Qty (kg)");
            table.addCell("Line Total");

            order.getItems().forEach(item -> {
                table.addCell(item.getProduct().getSku());
                table.addCell(item.getProduct().getName());
                table.addCell(String.valueOf(item.getQtyKg()));
                table.addCell(String.format("%.2f", item.getQtyKg() * item.getPricePerKg()));
            });

            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph(String.format("Total: ₹%.2f", order.getTotalAmount()), body));
            document.add(new Paragraph(String.format("GST: ₹%.2f", order.getGstAmount()), body));
            document.add(new Paragraph(
                    String.format("Grand Total: ₹%.2f", order.getTotalAmount() + order.getGstAmount()), body));

            document.close();
            return path;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate invoice PDF", ex);
        }
    }
}
