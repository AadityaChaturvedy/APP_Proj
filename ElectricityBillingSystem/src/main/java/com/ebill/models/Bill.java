package com.ebill.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;

public class Bill {
    private final StringProperty customerName;
    private final IntegerProperty meterNumber;
    private final StringProperty address;
    private final StringProperty city;
    private final StringProperty state;
    private final StringProperty phone;
    private final StringProperty email;
    private final IntegerProperty unitsConsumed;
    private final StringProperty billingMonth;
    private final StringProperty paymentStatus;

    @JsonIgnore
    private final DoubleProperty billAmount;

    public static final double RATE_PER_UNIT = 7.5;

    @JsonCreator
    public Bill(@JsonProperty("customerName") String customerName,
                @JsonProperty("meterNumber") int meterNumber,
                @JsonProperty("address") String address,
                @JsonProperty("city") String city,
                @JsonProperty("state") String state,
                @JsonProperty("phone") String phone,
                @JsonProperty("email") String email,
                @JsonProperty("unitsConsumed") int unitsConsumed,
                @JsonProperty("billingMonth") String billingMonth,
                @JsonProperty("paymentStatus") String paymentStatus) {
        this.customerName = new SimpleStringProperty(customerName);
        this.meterNumber = new SimpleIntegerProperty(meterNumber);
        this.address = new SimpleStringProperty(address);
        this.city = new SimpleStringProperty(city);
        this.state = new SimpleStringProperty(state);
        this.phone = new SimpleStringProperty(phone);
        this.email = new SimpleStringProperty(email);
        this.unitsConsumed = new SimpleIntegerProperty(unitsConsumed);
        this.billingMonth = new SimpleStringProperty(billingMonth);
        this.paymentStatus = new SimpleStringProperty(paymentStatus);
        this.billAmount = new SimpleDoubleProperty(calculateBillAmount(unitsConsumed));

        this.unitsConsumed.addListener((obs, oldVal, newVal) ->
                this.billAmount.set(calculateBillAmount(newVal.intValue())));
    }
    
    public Bill() {
        this.customerName = new SimpleStringProperty("");
        this.meterNumber = new SimpleIntegerProperty(0);
        this.address = new SimpleStringProperty("");
        this.city = new SimpleStringProperty("");
        this.state = new SimpleStringProperty("");
        this.phone = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.unitsConsumed = new SimpleIntegerProperty(0);
        this.billingMonth = new SimpleStringProperty("");
        this.paymentStatus = new SimpleStringProperty("Unpaid");
        this.billAmount = new SimpleDoubleProperty(0.0);
        
        this.unitsConsumed.addListener((obs, oldVal, newVal) ->
                this.billAmount.set(calculateBillAmount(newVal.intValue())));
    }

    private double calculateBillAmount(int units) {
        return units * RATE_PER_UNIT;
    }

    public String getCustomerName() { return customerName.get(); }
    public int getMeterNumber() { return meterNumber.get(); }
    public String getAddress() { return address.get(); }
    public String getCity() { return city.get(); }
    public String getState() { return state.get(); }
    public String getPhone() { return phone.get(); }
    public String getEmail() { return email.get(); }
    public int getUnitsConsumed() { return unitsConsumed.get(); }
    public String getBillingMonth() { return billingMonth.get(); }
    public String getPaymentStatus() { return paymentStatus.get(); }
    public double getBillAmount() { return billAmount.get(); }
    
    public void setCustomerName(String name) { this.customerName.set(name); }
    public void setMeterNumber(int number) { this.meterNumber.set(number); }
    public void setAddress(String address) { this.address.set(address); }
    public void setCity(String city) { this.city.set(city); }
    public void setState(String state) { this.state.set(state); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public void setEmail(String email) { this.email.set(email); }
    public void setUnitsConsumed(int units) { this.unitsConsumed.set(units); }
    public void setBillingMonth(String month) { this.billingMonth.set(month); }
    public void setPaymentStatus(String status) { this.paymentStatus.set(status); }

    public StringProperty customerNameProperty() { return customerName; }
    public IntegerProperty meterNumberProperty() { return meterNumber; }
    public StringProperty addressProperty() { return address; }
    public StringProperty cityProperty() { return city; }
    public StringProperty stateProperty() { return state; }
    public StringProperty phoneProperty() { return phone; }
    public StringProperty emailProperty() { return email; }
    public IntegerProperty unitsConsumedProperty() { return unitsConsumed; }
    public StringProperty billingMonthProperty() { return billingMonth; }
    public StringProperty paymentStatusProperty() { return paymentStatus; }
    public DoubleProperty billAmountProperty() { return billAmount; }
}