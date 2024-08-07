package com.example.savingsapp03Push.dao;

import com.example.savingsapp03Push.model.Saving;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SavingDaoImpl implements SavingDao {

    private final JdbcTemplate jdbcTemplate;

    public SavingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS savingstable");
        jdbcTemplate.execute("CREATE TABLE savingstable (" +
                "customerNumber VARCHAR(255) PRIMARY KEY," +
                "customerName VARCHAR(255)," +
                "initialDeposit DECIMAL(10, 2)," +
                "numberOfYears INT," +
                "savingsType VARCHAR(255))");
        jdbcTemplate.update("INSERT INTO savingstable (customerNumber, customerName, initialDeposit, numberOfYears, savingsType) VALUES " +
                "('115', 'Jasper Diaz', 15000.00, 5, 'Savings-Deluxe')," +
                "('112', 'Zanip Mendez', 5000.00, 2, 'Savings-Deluxe')," +
                "('105', 'Johnny Jacobi', 7000.00, 8, 'Savings-Regular')," +
                "('113', 'Geronima Esper', 6000.00, 6, 'Savings-Regular')");
    }

    @Override
    public List<Saving> findAll() {
        return jdbcTemplate.query("SELECT * FROM savingstable", new SavingRowMapper());
    }

    @Override
    public void save(Saving saving) {
        jdbcTemplate.update("INSERT INTO savingstable (customerNumber, customerName, initialDeposit, numberOfYears, savingsType) VALUES (?, ?, ?, ?, ?)",
                saving.getCustomerNumber(), saving.getCustomerName(), saving.getInitialDeposit(), saving.getNumberOfYears(), saving.getSavingsType());
    }

    @Override
    public void update(Saving saving) {
        jdbcTemplate.update("UPDATE savingstable SET customerName = ?, initialDeposit = ?, numberOfYears = ?, savingsType = ? WHERE customerNumber = ?",
                saving.getCustomerName(), saving.getInitialDeposit(), saving.getNumberOfYears(), saving.getSavingsType(), saving.getCustomerNumber());
    }

    @Override
    public void delete(String customerNumber) {
        jdbcTemplate.update("DELETE FROM savingstable WHERE customerNumber = ?", customerNumber);
    }

    @Override
    public Saving findById(String customerNumber) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM savingstable WHERE customerNumber = ?", new SavingRowMapper(), customerNumber);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private static class SavingRowMapper implements RowMapper<Saving> {
        @Override
        public Saving mapRow(ResultSet rs, int rowNum) throws SQLException {
            Saving saving = new Saving();
            saving.setCustomerNumber(rs.getString("customerNumber"));
            saving.setCustomerName(rs.getString("customerName"));
            saving.setInitialDeposit(rs.getDouble("initialDeposit"));
            saving.setNumberOfYears(rs.getInt("numberOfYears"));
            saving.setSavingsType(rs.getString("savingsType"));
            return saving;
        }
    }
}
