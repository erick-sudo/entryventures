package com.entryventures.models

enum class LoanStatus(
    private var status: String
) {
    Pending("Pending"),
    Approved("Approved"),
    Disbursed("Disbursed"),
    Closed("Closed");

    override fun toString(): String = status
}