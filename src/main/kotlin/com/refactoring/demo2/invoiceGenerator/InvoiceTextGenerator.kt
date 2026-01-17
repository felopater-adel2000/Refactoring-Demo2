package com.refactoring.demo2.invoiceGenerator

class InvoiceTextGenerator(val order: Order, val products: Map<String, Product>) {

    fun generate(): String {
        val invoiceData = getInvoiceData()
        return presentInvoiceText(invoiceData)
    }

    private fun getInvoiceData(): InvoiceData {
        val invoiceLines = order.shipmentItems.map { getInvoiceLine(it) }
        val invoiceData = InvoiceData(
            customerName = order.customerName,
            loyaltyPoints = calculateLoyaltyPoints(),
            totalCost = calculateTotalcost(),
            invoiceLines = invoiceLines
        )
        return invoiceData
    }

    private fun presentInvoiceText(
        invoiceData: InvoiceData
    ): String {
        var result = "Shipping Invoice for ${invoiceData.customerName}\n"
        for (invoiceLine in invoiceData.invoiceLines) {
            result += getInvoiceForLineItem(invoiceLine)
        }

        result += "Total shipping cost is ${formatCurrency(calculateTotalcost())}\n"
        result += "You earned ${invoiceData.loyaltyPoints} loyalty points\n"

        return result
    }

    private fun getInvoiceLine(item: ShipmentItem): InvoiceLine = InvoiceLine(
        itemCost = calculateItemCost(item),
        itemQuantity = item.quantity,
        itemWeight = item.weight,
        productName = getProduct(item).name
    )

    private fun getInvoiceForLineItem(invoiceLine: InvoiceLine): String =
        "  ${
            invoiceLine.productName
        }: ${formatCurrency(amountInCents = invoiceLine.itemCost)} " +
                "(${invoiceLine.itemQuantity} items, ${invoiceLine.itemWeight}kg)\n"

    fun generateHTML(): String {

        var result = "<Header>Shipping Invoice for ${order.customerName}\n"
        for (item in order.shipmentItems) {
            result += "  ${
                getProduct(item).name
            }: ${formatCurrency(amountInCents = calculateItemCost(item))} " +
                    "(${item.quantity} items, ${item.weight}kg)\n"
        }

        result += "Total shipping cost is ${formatCurrency(calculateTotalcost())}\n"
        result += "You earned ${calculateLoyaltyPoints()} loyalty points\n"

        return result
    }

    private fun calculateTotalcost(): Int {
        var totalCost = 0
        for (item in order.shipmentItems) {
            totalCost += calculateItemCost(item)
        }
        return totalCost
    }

    private fun calculateLoyaltyPoints(): Int {
        var loyaltyPoints = 0
        for (item in order.shipmentItems) {
            loyaltyPoints += calculateLoyaltyPointsIncrease(item)
        }
        return loyaltyPoints
    }

    private fun getProduct(item: ShipmentItem): Product = products[item.productID]!!

    private fun calculateLoyaltyPointsIncrease(
        item: ShipmentItem
    ): Int {
        val product = getProduct(item)
        var loyaltyPoints = maxOf(item.quantity - 2, 0)
        if ("express" == product.shippingMethod) {
            loyaltyPoints += item.quantity / 3
        }
        return loyaltyPoints
    }

    private fun calculateItemCost(
        item: ShipmentItem
    ): Int {
        val product = getProduct(item)
        var itemCost = 0
        when (product.shippingMethod) {

            "standard" -> {
                itemCost = calculateStandardShippingCost(itemCost, item)
            }

            "express" -> {
                itemCost = calculateExpressShippingCost(item)
            }


            else -> {
                throw kotlin.IllegalArgumentException("unknown shipping method: ${product.shippingMethod}")
            }
        }
        return itemCost
    }

    private fun calculateExpressShippingCost(item: ShipmentItem): Int {
        var itemCost = 1200
        if (item.weight > 3.0) {
            itemCost += (250 * (item.weight - 3.0)).toInt()
        }

        itemCost += 150 * item.quantity
        return itemCost
    }

    private fun calculateStandardShippingCost(itemCost: Int, item: ShipmentItem): Int {
        var itemCost1 = itemCost
        itemCost1 = 500
        if (item.weight > 5.0) {
            itemCost1 += (100 * (item.weight - 5.0)).toInt()
        }
        return itemCost1
    }

    fun formatCurrency(amountInCents: Int): String {
        val amountInDollars = amountInCents / 100.0
        return "$%.2f".format(amountInDollars)
    }
}