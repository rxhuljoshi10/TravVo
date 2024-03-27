package com.example.onlinebusticketing;

import java.io.Serializable;

public class TicketData implements Serializable {
    public String source;
    public String destination;
    public int fullPrice;
    public int halfPrice;
    public int fullCounter;
    public int halfCounter;
    public int totalFullPrice;
    public int totalHalfPrice;
    public int totalPrice;

    public TicketData(String source, String destination, int fullPrice, int halfPrice, int fullCounter, int halfCounter, int totalFullPrice, int totalHalfPrice, int totalPrice) {
        this.source = source;
        this.destination = destination;
        this.fullPrice = fullPrice;
        this.halfPrice = halfPrice;
        this.fullCounter = fullCounter;
        this.halfCounter = halfCounter;
        this.totalFullPrice = totalFullPrice;
        this.totalHalfPrice = totalHalfPrice;
        this.totalPrice = totalPrice;
    }
}
