package com.entryventures.exceptions

import org.springframework.http.HttpStatus

class EntryVenturesException(
    var serverStatus: HttpStatus,
    private val errorDescription: () -> Any? = { null },
    private val errorMessage: () -> String = { "Entry Ventures Exception Occurred" }
) : Exception() {

    override val message: String
        get() = errorMessage()

    val description
        get() = errorDescription()
}