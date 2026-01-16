package com.refactoring.demo2.invoiceGenerator

class InvoiceTextGenerator(val order: Order, val products: Map<String, Product>) {

    fun generate(): String {
        val invoiceData = InvoiceData(
            customerName = order.customerName,
            loyaltyPoints = calculateLoyaltyPoints(),
            totalCost = calculateTotalcost()
        )
        var result = "Shipping Invoice for ${invoiceData.customerName}\n"
        for (item in order.shipmentItems) {
            val invoiceLine = InvoiceLine(
                itemCost = calculateItemCost(item),
                itemQuantity = item.quantity,
                itemWeight = item.weight,
                productName = getProduct(item).name
            )
            result += string(invoiceLine)
        }

        result += "Total shipping cost is ${formatCurrency(calculateTotalcost())}\n"
        result += "You earned ${invoiceData.loyaltyPoints} loyalty points\n"

        return result
    }

    private fun string(invoiceLine: InvoiceLine): String = getInvoiceForLineItem(
        itemCost = invoiceLine.itemCost,
        itemQuantity = invoiceLine.itemQuantity,
        itemWeight = invoiceLine.itemWeight,
        productName = invoiceLine.productName
    )

    fun generateHTML(): String {

        var result = "<Header>Shipping Invoice for ${order.customerName}\n"
        for (item in order.shipmentItems) {
            result += getInvoiceForLineItem(
                itemCost = calculateItemCost(item),
                itemQuantity = item.quantity,
                itemWeight = item.weight,
                productName = getProduct(item).name
            )
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

    private fun getInvoiceForLineItem(
        itemCost: Int,
        itemQuantity: Int,
        itemWeight: Double,
        productName: String
    ): String {
        return "  $productName: ${formatCurrency(itemCost)} " +
                "($itemQuantity items, ${itemWeight}kg)\n"
    }

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
                itemCost = 500
                if (item.weight > 5.0) {
                    itemCost += (100 * (item.weight - 5.0)).toInt()
                }
            }

            "express" -> {
                itemCost = 1200
                if (item.weight > 3.0) {
                    itemCost += (250 * (item.weight - 3.0)).toInt()
                }

                itemCost += 150 * item.quantity
            }


            else -> {
                throw kotlin.IllegalArgumentException("unknown shipping method: ${product.shippingMethod}")
            }
        }
        return itemCost
    }

    fun formatCurrency(amountInCents: Int): String {
        val amountInDollars = amountInCents / 100.0
        return "$%.2f".format(amountInDollars)
    }
}