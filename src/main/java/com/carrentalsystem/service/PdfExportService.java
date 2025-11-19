package com.carrentalsystem.service;

import com.carrentalsystem.models.Booking;
import com.carrentalsystem.models.Vehicle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public byte[] exportBookings(List<Booking> bookings) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Paragraph title = new Paragraph("Booking Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add generation date
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now().format(DATE_FORMATTER), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);

        // Create table
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 2, 2, 2, 2, 2, 2, 2});

        // Add headers
        addTableHeader(table);

        // Add data rows
        for (Booking booking : bookings) {
            addBookingRow(table, booking);
        }

        document.add(table);

        // Add summary
        addBookingSummary(document, bookings);

        document.close();
        return out.toByteArray();
    }

    public byte[] exportVehicles(List<Vehicle> vehicles) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Add title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Paragraph title = new Paragraph("Vehicle Inventory Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add generation date
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph date = new Paragraph("Generated on: " + java.time.LocalDate.now().format(DATE_FORMATTER), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);

        // Create table
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{2, 2, 2, 2, 1, 2, 2});

        // Add headers
        addVehicleTableHeader(table);

        // Add data rows
        for (Vehicle vehicle : vehicles) {
            addVehicleRow(table, vehicle);
        }

        document.add(table);

        // Add summary
        addVehicleSummary(document, vehicles);

        document.close();
        return out.toByteArray();
    }

    public byte[] exportBookingReceipt(Booking booking) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Company header
        Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLUE);
        Paragraph company = new Paragraph("Smart Car Rental System", companyFont);
        company.setAlignment(Element.ALIGN_CENTER);
        company.setSpacingAfter(10);
        document.add(company);

        // Receipt title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
        Paragraph title = new Paragraph("Booking Receipt", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Booking details
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        document.add(new Paragraph("Booking ID: " + booking.getBookingId(), boldFont));
        document.add(new Paragraph("Date: " + booking.getBookingDate().format(DATE_FORMATTER), normalFont));
        document.add(Chunk.NEWLINE);

        // Customer details
        document.add(new Paragraph("Customer Information:", boldFont));
        document.add(new Paragraph("Name: " + booking.getCustomer().getFullName(), normalFont));
        document.add(new Paragraph("Email: " + booking.getCustomer().getEmail(), normalFont));
        document.add(new Paragraph("Phone: " + booking.getCustomer().getPhone(), normalFont));
        document.add(Chunk.NEWLINE);

        // Vehicle details
        document.add(new Paragraph("Vehicle Information:", boldFont));
        document.add(new Paragraph("Vehicle: " + booking.getVehicle().getBrand() + " " + booking.getVehicle().getModel(), normalFont));
        document.add(new Paragraph("Type: " + booking.getVehicle().getVehicleType(), normalFont));
        document.add(new Paragraph("Vehicle Number: " + booking.getVehicle().getVehicleNumber(), normalFont));
        document.add(Chunk.NEWLINE);

        // Rental details
        document.add(new Paragraph("Rental Period:", boldFont));
        document.add(new Paragraph("Start Date: " + booking.getStartDate().format(DATE_FORMATTER), normalFont));
        document.add(new Paragraph("End Date: " + booking.getEndDate().format(DATE_FORMATTER), normalFont));
        document.add(new Paragraph("Total Days: " + booking.getTotalDays(), normalFont));
        document.add(Chunk.NEWLINE);

        // Payment details
        document.add(new Paragraph("Payment Information:", boldFont));
        document.add(new Paragraph("Daily Rate: LKR " + booking.getVehicle().getDailyRate(), normalFont));
        document.add(new Paragraph("Total Amount: LKR " + booking.getTotalAmount(), boldFont));
        document.add(new Paragraph("Payment Status: " + booking.getPaymentStatus(), normalFont));
        document.add(new Paragraph("Booking Status: " + booking.getBookingStatus(), normalFont));
        document.add(Chunk.NEWLINE);

        // Footer
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph footer = new Paragraph("\nThank you for choosing Smart Car Rental System!", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return out.toByteArray();
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

        String[] headers = {"ID", "Customer", "Vehicle", "Start Date", "End Date", "Days", "Amount", "Status"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(Color.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addBookingRow(PdfPTable table, Booking booking) {
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        table.addCell(new Phrase(String.valueOf(booking.getBookingId()), cellFont));
        table.addCell(new Phrase(booking.getCustomer().getFullName(), cellFont));
        table.addCell(new Phrase(booking.getVehicle().getBrand() + " " + booking.getVehicle().getModel(), cellFont));
        table.addCell(new Phrase(booking.getStartDate().format(DATE_FORMATTER), cellFont));
        table.addCell(new Phrase(booking.getEndDate().format(DATE_FORMATTER), cellFont));
        table.addCell(new Phrase(String.valueOf(booking.getTotalDays()), cellFont));
        table.addCell(new Phrase("LKR " + booking.getTotalAmount(), cellFont));
        table.addCell(new Phrase(booking.getBookingStatus(), cellFont));
    }

    private void addVehicleTableHeader(PdfPTable table) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

        String[] headers = {"Vehicle No.", "Brand", "Model", "Type", "Year", "Daily Rate", "Status"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(Color.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addVehicleRow(PdfPTable table, Vehicle vehicle) {
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

        table.addCell(new Phrase(vehicle.getVehicleNumber(), cellFont));
        table.addCell(new Phrase(vehicle.getBrand(), cellFont));
        table.addCell(new Phrase(vehicle.getModel(), cellFont));
        table.addCell(new Phrase(vehicle.getVehicleType(), cellFont));
        table.addCell(new Phrase(String.valueOf(vehicle.getYear()), cellFont));
        table.addCell(new Phrase("LKR " + vehicle.getDailyRate(), cellFont));
        table.addCell(new Phrase(vehicle.getAvailabilityStatus(), cellFont));
    }

    private void addBookingSummary(Document document, List<Booking> bookings) throws DocumentException {
        document.add(Chunk.NEWLINE);

        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        document.add(new Paragraph("Summary", summaryFont));

        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        document.add(new Paragraph("Total Bookings: " + bookings.size(), normalFont));

        BigDecimal totalRevenue = bookings.stream()
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        document.add(new Paragraph("Total Revenue: LKR " + totalRevenue, normalFont));
    }

    private void addVehicleSummary(Document document, List<Vehicle> vehicles) throws DocumentException {
        document.add(Chunk.NEWLINE);

        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        document.add(new Paragraph("Summary", summaryFont));

        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        document.add(new Paragraph("Total Vehicles: " + vehicles.size(), normalFont));

        long available = vehicles.stream().filter(v -> "Available".equals(v.getAvailabilityStatus())).count();
        document.add(new Paragraph("Available: " + available, normalFont));

        long booked = vehicles.stream().filter(v -> "Booked".equals(v.getAvailabilityStatus())).count();
        document.add(new Paragraph("Booked: " + booked, normalFont));
    }
}

