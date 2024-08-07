package com.example.savingsapp03Push.controller;

import com.example.savingsapp03Push.dao.SavingDao;
import com.example.savingsapp03Push.model.Saving;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class SavingControllerTest {

    private Saving saving;
    private MockMvc mockMvc;

    @Mock
    private SavingDao savingDao;

    @Mock
    private View mockView;

    @InjectMocks
    private SavingController savingController;

    @BeforeEach
    void setUp() {
        saving = new Saving();
        saving.setCustomerNumber("123");
        saving.setCustomerName("John Doe");
        saving.setInitialDeposit(1000.00);
        saving.setNumberOfYears(5);
        saving.setSavingsType("Savings-Deluxe");

        mockMvc = standaloneSetup(savingController).setSingleView(mockView).build();
    }

    @Test
    void home() throws Exception {
        List<Saving> savingsList = new ArrayList<>();
        savingsList.add(saving);
        when(savingDao.findAll()).thenReturn(savingsList);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("savings", savingsList))
                .andExpect(view().name("home"))
                .andExpect(model().attribute("savings", hasSize(1)));

        verify(savingDao, times(1)).findAll();
        verifyNoMoreInteractions(savingDao);
    }

    @Test
    void addSaving() throws Exception {
        mockMvc.perform(get("/add"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("saving"))
                .andExpect(view().name("add"));
    }

    @Test
    void saveSaving_NewCustomer() throws Exception {
        when(savingDao.findById(anyString())).thenReturn(null);

        mockMvc.perform(post("/add")
                        .flashAttr("saving", saving))
                .andExpect(status().isOk());

        verify(savingDao, times(1)).save(any(Saving.class));
        verifyNoMoreInteractions(savingDao);
    }

    @Test
    void saveSaving_ExistingCustomer() throws Exception {
        when(savingDao.findById(anyString())).thenReturn(saving);

        mockMvc.perform(post("/add")
                        .flashAttr("saving", saving))
                .andExpect(status().isOk())
                .andExpect(view().name("add"))
                .andExpect(model().attribute("errorMessage", "The record you are trying to add is already existing. Choose a different customer number."));

        verify(savingDao, never()).save(any(Saving.class));
    }

    @Test
    void editSaving() throws Exception {
        when(savingDao.findById(anyString())).thenReturn(saving);

        mockMvc.perform(get("/edit/{customerNumber}", "123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("saving", saving))
                .andExpect(view().name("edit"));
    }

    @Test
    void updateSaving() throws Exception {
        mockMvc.perform(post("/edit")
                        .flashAttr("saving", saving))
                .andExpect(status().isOk());

        verify(savingDao, times(1)).update(any(Saving.class));
        verifyNoMoreInteractions(savingDao);
    }

    @Test
    void deleteSaving() throws Exception {
        mockMvc.perform(get("/delete/{customerNumber}", "123"))
                .andExpect(status().isOk());

        verify(savingDao, times(1)).delete("123");
        verifyNoMoreInteractions(savingDao);
    }

    @Test
    void projectInvestment() throws Exception {
        when(savingDao.findById(anyString())).thenReturn(saving);

        mockMvc.perform(get("/project/{customerNumber}", "123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("saving", saving))
                .andExpect(view().name("project"))
                .andExpect(model().attributeExists("yearAmounts"));

        verify(savingDao, times(1)).findById("123");
        verifyNoMoreInteractions(savingDao);
    }
}
