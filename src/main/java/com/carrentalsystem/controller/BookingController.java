package com.carrentalsystem.controller;

import com.carrentalsystem.models.Booking;
import com.carrentalsystem.service.BookingService;
import com.carrentalsystem.service.CustomerService;
import com.carrentalsystem.service.PdfExportService;
import com.carrentalsystem.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final PdfExportService pdfExportService;

    private static final String UPLOAD_DIR = "uploads/payment_slips/";

    @GetMapping
    public String listBookings(@RequestParam(required = false) String status, Model model) {
        List<Booking> bookings;

        if (status != null && !status.isEmpty()) {
            bookings = bookingService.findByStatus(status);
        } else {
            bookings = bookingService.findAll();
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("booking", new Booking());
        model.addAttribute("vehicles", vehicleService.findAllAvailable());
        model.addAttribute("customers", customerService.findAllActive());
        model.addAttribute("selectedStatus", status);

        return "admin/bookings";
    }

    @GetMapping("/pending-payments")
    public String pendingPayments(Model model) {
        model.addAttribute("bookings", bookingService.findPendingPayments());
        return "admin/pending-payments";
    }

    @PostMapping("/add")
    public String addBooking(@Valid @ModelAttribute Booking booking,
                            @RequestParam Integer vehicleId,
                            @RequestParam Integer customerId,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the validation errors");
            return "redirect:/admin/bookings";
        }

        try {
            booking.setVehicle(vehicleService.findById(vehicleId).orElseThrow());
            booking.setCustomer(customerService.findById(customerId).orElseThrow());
            bookingService.createBooking(booking);
            redirectAttributes.addFlashAttribute("success", "Booking created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/bookings";
    }

    @GetMapping("/view/{id}")
    public String viewBooking(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Booking booking = bookingService.findByIdWithDetails(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
            model.addAttribute("booking", booking);
            return "admin/booking-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/bookings";
        }
    }

    @PostMapping("/update-status/{id}")
    public String updateBookingStatus(@PathVariable Integer id,
                                     @RequestParam String status,
                                     RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBookingStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Booking status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/bookings";
    }

    @PostMapping("/update-payment/{id}")
    public String updatePaymentStatus(@PathVariable Integer id,
                                     @RequestParam String paymentStatus,
                                     RedirectAttributes redirectAttributes) {
        try {
            bookingService.updatePaymentStatus(id, paymentStatus);
            redirectAttributes.addFlashAttribute("success", "Payment status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/bookings";
    }

    @GetMapping("/delete/{id}")
    public String deleteBookingGet(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.deleteBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete booking: " + e.getMessage());
        }

        return "redirect:/admin/bookings";
    }

    @PostMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.deleteBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete booking: " + e.getMessage());
        }

        return "redirect:/admin/bookings";
    }

    @GetMapping("/payment-slip/{filename}")
    public ResponseEntity<byte[]> viewPaymentSlip(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            byte[] fileBytes = Files.readAllBytes(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportBookings(@RequestParam(required = false) String status) {
        try {
            List<Booking> bookings;
            if (status != null && !status.isEmpty()) {
                bookings = bookingService.findByStatus(status);
            } else {
                bookings = bookingService.findAll();
            }

            byte[] pdfBytes = pdfExportService.exportBookings(bookings);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bookings_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/receipt/{id}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Integer id) {
        try {
            Booking booking = bookingService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            byte[] pdfBytes = pdfExportService.exportBookingReceipt(booking);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "booking_receipt_" + id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

