package com.entryventures.crons

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SalaryProcessor {

    @Scheduled(cron = "0 0 8 LW * *") // Every last weekday of the month
    fun loanOfficersPayout() {

        // Fetch Loan Officers

    }

    @Scheduled(cron = "0 0 8 L * *") // Every last day of the month
    fun shareHoldersPayout() {

    }
}