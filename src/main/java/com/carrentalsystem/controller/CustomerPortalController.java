package com.carrentalsystem.controller;

import com.carrentalsystem.models.Booking;
import com.carrentalsystem.models.Customer;
import com.carrentalsystem.models.User;
import com.carrentalsystem.models.Vehicle;
import com.carrentalsystem.service.BookingService;
import com.carrentalsystem.service.CustomerService;
import com.carrentalsystem.service.PdfExportService;
import com.carrentalsystem.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerPortalController {

    private final VehicleService vehicleService;
    private final BookingService bookingService;
    private final CustomerService customerService;
    private final PdfExportService pdfExportService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        Customer customer = customerService.findByUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));

        List<Booking> myBookings = bookingService.findByCustomerId(customer.getCustomerId());

        model.addAttribute("customer", customer);
        model.addAttribute("totalBookings", myBookings.size());
        model.addAttribute("pendingBookings", myBookings.stream()
                .filter(b -> "Pending".equals(b.getBookingStatus())).count());
        model.addAttribute("confirmedBookings", myBookings.stream()
                .filter(b -> "Confirmed".equals(b.getBookingStatus())).count());
        model.addAttribute("recentBookings", myBookings.stream().limit(5).toList());

        return "customer/dashboard";
    }

    @GetMapping("/browse")
    public String browseVehicles(@RequestParam(required = false) String type,
                                @RequestParam(required = false) String search,
                                Model model) {
        List<Vehicle> vehicles;

        if (search != null && !search.isEmpty()) {
            vehicles = vehicleService.searchVehicles(search);
        } else if (type != null && !type.isEmpty()) {
            vehicles = vehicleService.findByType(type);
        } else {
            vehicles = vehicleService.findAllAvailable();
        }

        // Filter only available vehicles
        vehicles = vehicles.stream()
                .filter(v -> "Available".equals(v.getAvailabilityStatus()))
                .toList();

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("selectedType", type);
        model.addAttribute("searchKeyword", search);

        return "customer/browse";
    }

    @GetMapping("/book/{vehicleId}")
    public String bookingForm(@PathVariable Integer vehicleId,
                             @AuthenticationPrincipal User user,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            Vehicle vehicle = vehicleService.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

            Customer customer = customerService.findByUserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Customer profile not found"));

            if (!"Available".equals(vehicle.getAvailabilityStatus())) {
                redirectAttributes.addFlashAttribute("error", "Vehicle is not available for booking");
                return "redirect:/customer/browse";
            }

            Booking booking = new Booking();
            booking.setVehicle(vehicle);
            booking.setCustomer(customer);
            booking.setStartDate(LocalDate.now().plusDays(1));
            booking.setEndDate(LocalDate.now().plusDays(2));

            model.addAttribute("vehicle", vehicle);
            model.addAttribute("booking", booking);

            return "customer/book-vehicle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/browse";
        }
    }

    @PostMapping("/book")
    public String createBooking(@ModelAttribute Booking booking,
                               @RequestParam Integer vehicleId,
                               @AuthenticationPrincipal User user,
                               RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findByUserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Customer profile not found"));

            Vehicle vehicle = vehicleService.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

            booking.setVehicle(vehicle);
            booking.setCustomer(customer);

            bookingService.createBooking(booking);
            redirectAttributes.addFlashAttribute("success", "Booking created successfully! Please upload payment slip.");

            return "redirect:/customer/mybookings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/browse";
        }
    }

    @GetMapping("/mybookings")
    public String myBookings(@AuthenticationPrincipal User user,
                            @RequestParam(required = false) String status,
                            Model model) {
        Customer customer = customerService.findByUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));

        List<Booking> bookings = bookingService.findByCustomerId(customer.getCustomerId());

        if (status != null && !status.isEmpty()) {
            bookings = bookings.stream()
                    .filter(b -> status.equals(b.getBookingStatus()))
                    .toList();
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("selectedStatus", status);

        return "customer/mybookings";
    }

    @GetMapping("/booking/{id}")
    public String viewBooking(@PathVariable Integer id,
                             @AuthenticationPrincipal User user,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findByUserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Customer profile not found"));

            Booking booking = bookingService.findByIdWithDetails(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Verify booking belongs to current customer
            if (!booking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access");
                return "redirect:/customer/mybookings";
            }

            model.addAttribute("booking", booking);
            return "customer/booking-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/mybookings";
        }
    }

    @PostMapping("/booking/{id}/upload-payment")
    public String uploadPaymentSlip(@PathVariable Integer id,
                                   @RequestParam("file") MultipartFile file,
                                   @AuthenticationPrincipal User user,
                                   RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findByUserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Customer profile not found"));

            Booking booking = bookingService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Verify booking belongs to current customer
            if (!booking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access");
                return "redirect:/customer/mybookings";
            }

            bookingService.uploadPaymentSlip(id, file);
            redirectAttributes.addFlashAttribute("success", "Payment slip uploaded successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/customer/mybookings";
    }

    @GetMapping("/booking/{id}/receipt")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Integer id,
                                                  @AuthenticationPrincipal User user) {
        try {
            Customer customer = customerService.findByUserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Customer profile not found"));

            Booking booking = bookingService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            // Verify booking belongs to current customer
            if (!booking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                return ResponseEntity.status(403).build();
            }

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

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user,
                         @RequestParam(required = false) String status,
                         Model model) {
        Customer customer = customerService.findByUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Customer profile not found"));

        List<Booking> bookings = bookingService.findByCustomerId(customer.getCustomerId());

        if (status != null && !status.isEmpty()) {
            bookings = bookings.stream()
                    .filter(b -> status.equals(b.getBookingStatus()))
                    .toList();
        }

        model.addAttribute("customer", customer);
        model.addAttribute("bookings", bookings);
        model.addAttribute("selectedStatus", status);

        return "customer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Customer customer,
                               @AuthenticationPrincipal User user,
                               RedirectAttributes redirectAttributes) {
        try {
            Customer existingCustomer = customerService.findByUserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Customer profile not found"));

            customer.setCustomerId(existingCustomer.getCustomerId());
            customer.setUser(existingCustomer.getUser());

            customerService.updateCustomer(customer);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/customer/profile";
    }
}

