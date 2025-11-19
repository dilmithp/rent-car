package com.carrentalsystem.controller;

import com.carrentalsystem.models.Customer;
import com.carrentalsystem.service.BookingService;
import com.carrentalsystem.service.CustomerService;
import com.carrentalsystem.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final BookingService bookingService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Dashboard statistics
        model.addAttribute("totalVehicles", vehicleService.getTotalVehicles());
        model.addAttribute("availableVehicles", vehicleService.countByStatus("Available"));
        model.addAttribute("bookedVehicles", vehicleService.countByStatus("Booked"));

        model.addAttribute("totalCustomers", customerService.countByStatus("Active"));

        model.addAttribute("totalBookings", bookingService.getTotalBookings());
        model.addAttribute("pendingBookings", bookingService.countByStatus("Pending"));
        model.addAttribute("confirmedBookings", bookingService.countByStatus("Confirmed"));
        model.addAttribute("pendingPayments", bookingService.countByPaymentStatus("Pending"));

        // Recent bookings
        model.addAttribute("recentBookings", bookingService.findRecentBookings());

        return "admin/dashboard";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        // Get all customers
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("customer", new Customer());
        model.addAttribute("editMode", false);
        return "admin/customers";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            model.addAttribute("customer", customer);
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("editMode", true);
            return "admin/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/customers";
        }
    }

    @PostMapping("/customers/add")
    public String addCustomer(@Valid @ModelAttribute Customer customer,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the validation errors");
            return "redirect:/admin/customers";
        }

        try {
            customerService.createCustomer(customer, null, null); // Username and password will be generated
            redirectAttributes.addFlashAttribute("success", "Customer added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/update")
    public String updateCustomer(@Valid @ModelAttribute Customer customer,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the validation errors");
            return "redirect:/admin/customers";
        }

        try {
            customerService.updateCustomer(customer);
            redirectAttributes.addFlashAttribute("success", "Customer updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/customers";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomerGet(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Customer deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/customers";
    }
}

