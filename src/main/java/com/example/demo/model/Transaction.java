package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    long transactionId;
    long accountNumber;
    BigDecimal debit;
    BigDecimal credit;
    LocalDateTime date;
    String comment;
    AdditionalDetails additionalDetails;

    public Transaction(long transactionId, long accountNumber, BigDecimal debit, BigDecimal credit,
                       LocalDateTime date,
                       String comment, AdditionalDetails additionalDetails) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.debit = debit;
        this.credit = credit;
        this.date = date;
        this.comment = comment;
        this.additionalDetails = additionalDetails;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", accountNumber=" + accountNumber +
                ", debit=" + debit +
                ", credit=" + credit +
                ", date=" + date +
                ", comment='" + comment + '\'' +
                ", additionalDetails=" + additionalDetails +
                '}';
    }

    public long getTransactionId() {
        return transactionId;
    }
}
