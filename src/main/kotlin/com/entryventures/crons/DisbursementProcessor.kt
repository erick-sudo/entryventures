package com.entryventures.crons

import com.entryventures.apis.Apis
import com.entryventures.apis.mpesa.B2cRequestPayload
import com.entryventures.models.LoanStatus
import com.entryventures.models.jpa.Loan
import com.entryventures.models.jpa.LoanDisbursementSchedule
import com.entryventures.repository.LoanDisbursementScheduleRepository
import com.entryventures.repository.LoanRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import kotlin.math.roundToInt

private const val NUMBER_OF_DISBURSEMENT_WORKERS = 2

@Component
class DisbursementProcessor(
    private val loanDisbursementScheduleRepository: LoanDisbursementScheduleRepository,
    private val loanRepository: LoanRepository
) {

    /**
     * Process loan disbursements within a 10 minutes interval
     * When a loan is approved, it gets disbursed within a ten-minute timeline
     */
    //@Scheduled(fixedRate = 10000)
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
                disbursedLoan.loanDisbursementSchedule?.apply {
                    // Switch schedule to handled
                    processed = true
                    // Persist to database
                    loanDisbursementScheduleRepository.save(this)
                }

                disbursedLoan.apply {
                    // Switch loan status to DISBURSED
                    disbursedLoan.status = LoanStatus.Disbursed
                    // Persist to database
                    loanRepository.save(disbursedLoan)
                }
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
        // Send each schedule in the channel for processing
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

        // Process Business to Client Transaction
        Apis.MPESA_CLIENT.processB2CTransaction(
                b2cRequestPayload = B2cRequestPayload(
                        originatorConversationID = loanDisbursementSchedule.loan.id,
                        initiatorName = "testapi",
                        commandID = "BusinessPayment",
                        amount = "${loanDisbursementSchedule.loan.amount.roundToInt()}",
                        partyA = "600996",
                        partyB = "254${loanDisbursementSchedule.loan.client.phone}",
                        remarks = "Loan Disbursement",
                        queueTimeOutURL = "",
                        resultURL = "http://localhost:8080/entry-ventures/mpesa/callback/b2c",
                        occasion = "Loan Disbursement"
                ),
                responseCallback = { b2cRes ->
                    // Successful b2c initiation
                    println("${Date()} MPESA_B2C_SUCCESS: LOANID: ${loanDisbursementSchedule.loan.id}  :RESPONSE $b2cRes")
                    try {
                        // Push to accumulator channel for finalization
                        successDisbursementChannel.send(loanDisbursementSchedule.loan)
                    } catch (e: ClosedSendChannelException) {
                        // Handle channel closed before success acknowledgement
                        println("${Date()} MPESA_B2C_SUCCESS_CHANNEL_MISS: LOANID: ${loanDisbursementSchedule.loan.id}  :RESPONSE $b2cRes")
                    }
                }
        )
    }
}