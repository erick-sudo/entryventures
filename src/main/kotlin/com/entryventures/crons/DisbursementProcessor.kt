package com.entryventures.crons

import com.entryventures.apis.Apis
import com.entryventures.models.jpa.Loan
import com.entryventures.models.jpa.LoanDisbursementSchedule
import com.entryventures.repository.LoanDisbursementScheduleRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

const val NUMBER_OF_DISBURSEMENT_WORKERS = 2

@Component
class DisbursementProcessor(
    private val loanDisbursementScheduleRepository: LoanDisbursementScheduleRepository
) {

    /**
     * Process loan disbursements within a 10 minutes interval
     * When a loan is approved, it gets disbursed within a ten-minute timeline
     */
    @Scheduled(fixedRate = 600000)
    fun initiateDisbursements() {
        // Obtain the first 1000 unprocessed loans awaiting disbursement
        val loanDisbursementSchedules = loanDisbursementScheduleRepository.findUnprocessedLoanDisbursementSchedules(PageRequest.of(0, 1000))

        runBlocking {
            val disbursementChannel = Channel<LoanDisbursementSchedule>()
            val successDisbursementChannel = Channel<Loan>()

            // Send the pending disbursements to the processing channel
            launch {
                loanDisbursementSchedules.forEach { disbursementChannel.send(it) }
                disbursementChannel.close()
            }

            // Launching multiple workers to process disbursements in parallel
            launch {
                (1..NUMBER_OF_DISBURSEMENT_WORKERS).map {
                    launch {
                        processDisbursements(
                            disbursementChannel,
                            successDisbursementChannel
                        )
                    }
                }.joinAll() // Wait for all disbursements to complete
                successDisbursementChannel.close() // Close the success accumulator channel
            }

            // Database sync
            successDisbursementChannel.toList().forEach { disbursedLoan ->
                println("${disbursedLoan.id} \t ${disbursedLoan.amount}")
            }
        }
    }

    /**
     * A worker coroutine - Takes loans from the processing channel, and initiates processing
     * @param disbursementChannel : ReceiveChannel - Stages scheduled disbursements for processing
     * @param successDisbursementChannel : SendChannel - Accumulates successfully disbursed loans
     */
    suspend fun processDisbursements(
        disbursementChannel: ReceiveChannel<LoanDisbursementSchedule>,
        successDisbursementChannel: SendChannel<Loan>
    ) {
        for(schedule in disbursementChannel) {
            processTransaction(schedule, successDisbursementChannel)
        }
    }

    /**
     * A worker coroutine - Takes loans from the processing channel, and initiates processing
     * @param loanDisbursementSchedule : LoanDisbursementSchedule - Scheduled disbursement awaiting processing
     * @param successDisbursementChannel : SendChannel - Accumulates successfully disbursed loans
     */
    suspend fun processTransaction(
        loanDisbursementSchedule: LoanDisbursementSchedule,
        successDisbursementChannel: SendChannel<Loan>
    ) {

        val accessTokenResponse = Apis.MPESA_CLIENT.accessToken()
        println(accessTokenResponse)

        try {
            successDisbursementChannel.send(loanDisbursementSchedule.loan)
        } catch (e: ClosedSendChannelException) {
            // Handle channel closed before success acknowledgement
        }
    }
}