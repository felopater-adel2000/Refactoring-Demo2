package com.refactoring.demo2.invoiceGenerator

data class ShipmentItem(
    val productID: String, // id of product
    val quantity: Int,     // number of items
    val weight: Double
)