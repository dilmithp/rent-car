package com.carrentalsystem.controller;

import com.carrentalsystem.models.Vehicle;
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

import java.util.List;

@Controller
@RequestMapping("/admin/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final PdfExportService pdfExportService;

    @GetMapping
    public String listVehicles(@RequestParam(required = false) String type,
                              @RequestParam(required = false) String search,
                              Model model) {
        List<Vehicle> vehicles;

        if (search != null && !search.isEmpty()) {
            vehicles = vehicleService.searchVehicles(search);
        } else if (type != null && !type.isEmpty()) {
            vehicles = vehicleService.findByType(type);
        } else {
            vehicles = vehicleService.findAll();
        }

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("selectedType", type);
        model.addAttribute("searchKeyword", search);
        model.addAttribute("editMode", false); // Add this to prevent null error

        return "admin/vehicles";
    }

    @PostMapping("/add")
    public String addVehicle(@Valid @ModelAttribute Vehicle vehicle,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the validation errors");
            return "redirect:/admin/vehicles";
        }

        try {
            vehicleService.createVehicle(vehicle);
            redirectAttributes.addFlashAttribute("success", "Vehicle added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/vehicles";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Vehicle vehicle = vehicleService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("vehicles", vehicleService.findAll());
            model.addAttribute("editMode", true);
            return "admin/vehicles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/vehicles";
        }
    }

    @PostMapping("/update")
    public String updateVehicle(@Valid @ModelAttribute Vehicle vehicle,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the validation errors");
            return "redirect:/admin/vehicles";
        }

        try {
            vehicleService.updateVehicle(vehicle);
            redirectAttributes.addFlashAttribute("success", "Vehicle updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/vehicles";
    }

    @GetMapping("/delete/{id}")
    public String deleteVehicleGet(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("success", "Vehicle deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete vehicle: " + e.getMessage());
        }

        return "redirect:/admin/vehicles";
    }

    @PostMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.deleteVehicle(id);
            redirectAttributes.addFlashAttribute("success", "Vehicle deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cannot delete vehicle: " + e.getMessage());
        }

        return "redirect:/admin/vehicles";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportVehicles() {
        try {
            List<Vehicle> vehicles = vehicleService.findAll();
            byte[] pdfBytes = pdfExportService.exportVehicles(vehicles);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "vehicles_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

