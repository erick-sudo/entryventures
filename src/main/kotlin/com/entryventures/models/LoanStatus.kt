package com.entryventures.models

enum class LoanStatus(
    private var status: String
) {
    Pending("Pending approval"),
    Approved("Approved"),
    Disbursed("Disbursed"),
    Closed("Completed and closed");

    override fun toString(): String = status
}