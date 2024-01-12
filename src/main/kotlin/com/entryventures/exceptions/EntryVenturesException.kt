package com.entryventures.exceptions

import org.springframework.http.HttpStatus

class EntryVenturesException(
    var serverStatus: HttpStatus,
    val errorDescription: () -> String = { "" }
) : Exception() {

    override val message: String
        get() = errorDescription()
}