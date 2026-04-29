package com.personalvault.app.data.expense

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RequestPage
import androidx.compose.ui.graphics.vector.ImageVector

enum class PaymentMethod(
    val id: String,
    val label: String,
    val icon: ImageVector
) {
    CASH("cash", "Cash", Icons.Default.Money),
    UPI("upi", "UPI (GPay / PhonePe / Paytm)", Icons.Default.QrCode2),
    CREDIT_CARD("credit_card", "Credit Card", Icons.Default.CreditCard),
    DEBIT_CARD("debit_card", "Debit Card", Icons.Default.CreditCard),
    NET_BANKING("netbanking", "Net Banking", Icons.Default.AccountBalance),
    WALLET("wallet", "Wallet (Paytm / Mobikwik)", Icons.Default.AccountBalanceWallet),
    CHEQUE("cheque", "Cheque", Icons.Default.RequestPage),
    OTHER("other", "Other", Icons.Default.Payments);

    companion object {
        fun fromId(id: String?): PaymentMethod =
            entries.firstOrNull { it.id == id } ?: OTHER
    }
}
