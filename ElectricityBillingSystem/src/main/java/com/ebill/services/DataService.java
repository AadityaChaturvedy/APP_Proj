package com.ebill.services;

import com.ebill.models.Bill;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DataService {
    private static DataService instance;
    private final ObservableList<Bill> bills;
    private final ObjectMapper objectMapper;
    private final Path dataPath;

    private DataService() {
        bills = FXCollections.observableArrayList();
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        dataPath = Paths.get( "data.json");
        System.out.println("Data file path: " + dataPath.toAbsolutePath());
        loadBills();
    }

    public static synchronized DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    private void loadBills() {
        try {
            if (!Files.exists(dataPath)) {
                try (InputStream is = getClass().getResourceAsStream("/data.json")) {
                    if (is == null) {
                        System.err.println("Could not find sample data.json in resources.");
                        return;
                    }
                    Files.copy(is, dataPath);
                }
            }
            List<Bill> loadedBills = objectMapper.readValue(dataPath.toFile(), new TypeReference<>() {});
            bills.setAll(loadedBills);
        } catch (Exception e) {
            System.err.println("Error loading bills: " + e.getMessage());
            bills.clear();
        }
    }

    public void saveBills() {
        try {
            objectMapper.writeValue(dataPath.toFile(), bills);
        } catch (Exception e) {
            System.err.println("Error saving bills: " + e.getMessage());
        }
    }

    public ObservableList<Bill> getBills() {
        return bills;
    }

    public void addBill(Bill bill) {
        bill.setMeterNumber(getNextMeterNumber());
        bills.add(bill);
        saveBills();
    }

    public void updateBill(Bill bill) {
        saveBills();
    }

    public void deleteBill(Bill bill) {
        bills.remove(bill);
        saveBills();
    }

    public Optional<Bill> findBillByMeterNumber(int meterNumber) {
        return bills.stream()
                .filter(b -> b.getMeterNumber() == meterNumber)
                .findFirst();
    }

    public int getNextMeterNumber() {
        return bills.stream()
                .map(Bill::getMeterNumber)
                .max(Comparator.naturalOrder())
                .orElse(1000) + 1;
    }
}