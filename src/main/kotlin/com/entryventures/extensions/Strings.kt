package com.entryventures.extensions

import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.models.LoanStatus
import com.entryventures.models.PaymentMethod
import org.springframework.http.HttpStatus
import java.util.*

const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
const val DATE_REGEX = "[0-9]{2}/[0-9]{2}/[0-9]{4}"

fun String.charactersUpto(stop: Char, start: Int = 0): String {
    val firstOccurrence = this.indexOf(stop)
    return this.substring(start..<firstOccurrence)
}

fun String.toLoanStatus(): LoanStatus {
    return when(this) {
        "Pending" -> LoanStatus.Pending
        "Approved" -> LoanStatus.Approved
        "Disbursed" -> LoanStatus.Disbursed
        "Closed" -> LoanStatus.Closed
        else -> throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY) {
            "Invalid loan status"
        }
    }
}

fun String.isValidEmail(): Boolean = this.matches(EMAIL_REGEX.toRegex())

fun String.toDate(): Date {
    if(this.matches(DATE_REGEX.toRegex())) {
        val (day, month, year) = this.split("/").map { it.toInt() }
        return Calendar.getInstance().apply {
            set(year, month - 1, day)
        }.time
    }
    throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY) {
        "Invalid date"
    }
}

fun String.toPaymentMethod(): PaymentMethod {
    return when(this) {
        "Cash" -> PaymentMethod.Cash
        "Cheque" -> PaymentMethod.Cheque
        "CreditCard" -> PaymentMethod.CreditCard
        "DebitCard" -> PaymentMethod.DebitCard
        "OnlineTransfer" -> PaymentMethod.OnlineTransfer
        else -> throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY) {
            "Unrecognized payment method"
        }
    }
}