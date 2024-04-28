package com.example.onlinebusticketing;

import java.io.Serializable;

public class TicketData implements Serializable {
    public String source, destination, status;
    public int fullPrice;
    public int halfPrice;
    public int fullCounter;
    public int halfCounter;
    public int totalFullPrice;
    public int totalHalfPrice;
    public int totalPrice;

    String tDate, tTime, bookingId, tid;

    public TicketData(String tid, String bid, String source, String destination, int fullPrice, int halfPrice, int fullCounter, int halfCounter, int totalFullPrice, int totalHalfPrice, int totalPrice, String tDate, String tTime, String status) {
        this.tid = tid;
        this.bookingId = bid;
        this.source = source;
        this.destination = destination;
        this.fullPrice = fullPrice;
        this.halfPrice = halfPrice;
        this.fullCounter = fullCounter;
        this.halfCounter = halfCounter;
        this.totalFullPrice = totalFullPrice;
        this.totalHalfPrice = totalHalfPrice;
        this.totalPrice = totalPrice;
        this.tDate = tDate;
        this.tTime = tTime;
        this.status = status;
    }

    public TicketData(String bookingId, String source, String destination, int totalPrice, String tDate, String tTime, String status) {
        this.source = source;
        this.destination = destination;
        this.totalPrice = totalPrice;
        this.tDate = tDate;
        this.tTime = tTime;
        this.bookingId = bookingId;
        this.status = status;
    }
}
