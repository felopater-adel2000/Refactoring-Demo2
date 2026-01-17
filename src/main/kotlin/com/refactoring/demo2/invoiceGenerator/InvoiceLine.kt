package com.refactoring.demo2.invoiceGenerator

data class InvoiceLine(
    val itemCost: Int,
    val itemQuantity: Int,
    val itemWeight: Double,
    val productName: String
)