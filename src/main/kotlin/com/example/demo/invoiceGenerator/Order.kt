package com.example.demo.invoiceGenerator

data class Order(
    val customerName: String,
    val shipmentItems: List<ShipmentItem>
)