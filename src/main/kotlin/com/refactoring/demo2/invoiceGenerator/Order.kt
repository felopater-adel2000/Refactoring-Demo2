package com.refactoring.demo2.invoiceGenerator

data class Order(
    val customerName: String,
    val shipmentItems: List<ShipmentItem>
)