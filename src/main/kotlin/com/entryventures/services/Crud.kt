package com.entryventures.services

import com.entryventures.exceptions.EntryVenturesException
import org.springframework.http.HttpStatus
import java.util.*

object Crud {
    inline fun <reified T> find(entityFinder: () -> Optional<T>): T {

        val optionalEntity: Optional<T> =  entityFinder()

        if (optionalEntity.isPresent) {
            return optionalEntity.get()
        }

        throw EntryVenturesException(HttpStatus.NOT_FOUND) {
            "${T::class.java.simpleName} not found."
        }
    }
}