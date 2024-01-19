package com.entryventures.crons

import com.entryventures.apis.mpesa.MpesaTransactions
import com.entryventures.models.LoanStatus
import com.entryventures.models.jpa.Loan
import com.entryventures.models.jpa.LoanDisbursementSchedule
import com.entryventures.repository.LoanDisbursementScheduleRepository
import com.entryventures.repository.LoanRepository
import kotlinx.coroutines.*
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

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

            TaskManagers.processCounterChannelTasks(
                inputs = loanDisbursementSchedules,
                inputProcessor = { processLoanDisbursementTransaction(it) },
                outputReceiver = { disbursedLoans ->
                    disbursedLoans.forEach { disbursedLoan ->
                        // Database sync
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
            )
        }
    }

    /**
     * A worker coroutine - Initiates loan disbursement
     * @param loanDisbursementSchedule : LoanDisbursementSchedule - Scheduled disbursement awaiting processing
     */
    suspend fun processLoanDisbursementTransaction(
        loanDisbursementSchedule: LoanDisbursementSchedule
    ): Loan? {

        var loan: Loan? = null

        // Process Business to Client Transaction
        MpesaTransactions.processB2CTransaction(
                transactionId = loanDisbursementSchedule.loan.id,
                customer = loanDisbursementSchedule.loan.client,
                amount = loanDisbursementSchedule.loan.amount,
                responseCallback = { b2cRes ->
                    // Successful b2c initiation
                    println("${Date()} MPESA_B2C_SUCCESS: LOANID: ${loanDisbursementSchedule.loan.id}  :RESPONSE $b2cRes")
                    loan = loanDisbursementSchedule.loan
                }
        )
        return loan
    }
}