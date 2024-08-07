package com.example.savingsapp03Push.dao;

import com.example.savingsapp03Push.model.Saving;
import java.util.List;

public interface SavingDao {
    List<Saving> findAll();
    void save(Saving saving);
    void update(Saving saving);
    void delete(String customerNumber);
    Saving findById(String customerNumber);
}
