package com.example.savingsapp03Push.controller;

import com.example.savingsapp03Push.dao.SavingDao;
import com.example.savingsapp03Push.model.Saving;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SavingController {

    @Autowired
    @Qualifier("savingDao")
    private SavingDao savingDao;

    @GetMapping("/")
    public String home(Model model) {
        List<Saving> savings = savingDao.findAll();
        model.addAttribute("savings", savings);
        return "home";
    }

    @GetMapping("/add")
    public String addSaving(Model model) {
        model.addAttribute("saving", new Saving());
        return "add";
    }

    @PostMapping("/add")
    public String saveSaving(@ModelAttribute Saving saving, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "add";
        }
        if (savingDao.findById(saving.getCustomerNumber()) != null) {
            model.addAttribute("errorMessage", "The record you are trying to add is already existing. Choose a different customer number.");
            model.addAttribute("saving", saving);
            return "add";
        }
        savingDao.save(saving);
        return "redirect:/";
    }

    @GetMapping("/edit/{customerNumber}")
    public String editSaving(@PathVariable String customerNumber, Model model) {
        Saving saving = savingDao.findById(customerNumber);
        model.addAttribute("saving", saving);
        return "edit";
    }

    @PostMapping("/edit")
    public String updateSaving(@ModelAttribute Saving saving, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }
        savingDao.update(saving);
        return "redirect:/";
    }

    @GetMapping("/delete/{customerNumber}")
    public String deleteSaving(@PathVariable String customerNumber) {
        savingDao.delete(customerNumber);
        return "redirect:/";
    }

    @GetMapping("/project/{customerNumber}")
    public String projectInvestment(@PathVariable String customerNumber, Model model) {
        Saving saving = savingDao.findById(customerNumber);
        model.addAttribute("saving", saving);

        // Calculate compound interest for each year
        double interestRate = saving.getSavingsType().equals("Savings-Deluxe") ? 0.15 : 0.10;
        double startingAmount = saving.getInitialDeposit();
        List<YearAmount> yearAmounts = new ArrayList<>();
        for (int i = 0; i < saving.getNumberOfYears(); i++) {
            double interest = startingAmount * interestRate;
            double endingBalance = startingAmount + interest;
            yearAmounts.add(new YearAmount(startingAmount, interest, endingBalance));
            startingAmount = endingBalance;
        }
        model.addAttribute("yearAmounts", yearAmounts);
        return "project";
    }

    // Helper class to hold the year amount details
    public static class YearAmount {
        private double startingAmount;
        private double interest;
        private double endingBalance;

        public YearAmount(double startingAmount, double interest, double endingBalance) {
            this.startingAmount = startingAmount;
            this.interest = interest;
            this.endingBalance = endingBalance;
        }

        public double getStartingAmount() {
            return startingAmount;
        }

        public double getInterest() {
            return interest;
        }

        public double getEndingBalance() {
            return endingBalance;
        }
    }
}
