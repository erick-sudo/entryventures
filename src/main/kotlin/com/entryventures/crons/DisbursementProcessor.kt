package com.entryventures.crons

import com.entryventures.models.jpa.LoanDisbursementSchedule
import com.entryventures.repository.LoanDisbursementScheduleRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class DisbursementProcessor(
    private val loanDisbursementScheduleRepository: LoanDisbursementScheduleRepository
) {

    /**
     * Process loan disbursements within a 10 minutes interval
     */
    @Scheduled(fixedRate = 600000)
    fun fetchDisbursements() {
        // Obtain the first 1000 unprocessed loans awaiting disbursement
        val loans = loanDisbursementScheduleRepository.findUnprocessedLoanDisbursementSchedules(PageRequest.of(0, 1000))

        runBlocking {
            val disbursementChannel = Channel<LoanDisbursementSchedule>()

            launch {
                loans.forEach { loanDisbursementSchedule ->
                    disbursementChannel.send(loanDisbursementSchedule)
                }
            }

            launch {
                processDisbursements(disbursementChannel)
            }
        }
    }

    suspend fun processDisbursements(
        disbursementChannel: ReceiveChannel<LoanDisbursementSchedule>
    ) {

        for(loanDisbursement in disbursementChannel) {
            println("Processing ${loanDisbursement.loan.id}")
        }
    }
}