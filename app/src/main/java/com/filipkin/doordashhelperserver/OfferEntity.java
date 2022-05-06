package com.filipkin.doordashhelperserver;

public class OfferEntity {

    private double amount;
    private double tip;
    private double subtotal;
    private int driveTime;
    private boolean confident;
    private String store;
    private double hourly;

    public OfferEntity() {
    }

    public OfferEntity(double amount, double tip, double subtotal, int driveTime, boolean confident, String store) {
        this.amount = amount;
        this.tip = tip;
        this.subtotal = subtotal;
        this.driveTime = driveTime;
        this.confident = confident;
        this.store = store;
        this.hourly = Math.round((amount / (driveTime/60.0))*100.0)/100.0;
    }

    public double getAmount() {
        return amount;
    }

    public double getTip() {
        return tip;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public int getDriveTime() {
        return driveTime;
    }

    public String getStore() {
        return store;
    }

    public boolean isConfident() {
        return confident;
    }

    public double getHourly() {
        return hourly;
    }
}
