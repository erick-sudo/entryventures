package com.entryventures.services

import com.entryventures.exceptions.EntryVenturesException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.*

object Crud {
    inline fun <reified T> find(entityFinder: () -> Optional<T>): T {

        val optionalEntity =  entityFinder()

        if (optionalEntity.isPresent) {
            return optionalEntity.get()
        }

        throw EntryVenturesException(HttpStatus.NOT_FOUND) {
            "${T::class.java.simpleName} not found."
        }
    }

    fun <T> paginate(
        pageNumber: Int,
        pageSize: Int,
        count: () -> Long,
        execute: (Pageable) -> List<T>
    ): List<T> {
        var records = mutableListOf<T>()

        if(pageNumber -1 >= 0 && count() >= (pageNumber - 1) * pageSize) {
            val pageable: Pageable = PageRequest.of(pageNumber-1, pageSize)
            records = execute(pageable).toMutableList()
        }

        return records
    }
}