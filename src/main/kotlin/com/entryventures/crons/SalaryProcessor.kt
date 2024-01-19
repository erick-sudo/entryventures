package com.entryventures.crons

import com.entryventures.apis.mpesa.MpesaTransactions
import com.entryventures.models.UserPosition
import com.entryventures.models.jpa.User
import com.entryventures.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SalaryProcessor(
    private val userRepository: UserRepository
) {

    @Scheduled(cron = "0 0 8 LW * *") // Every last weekday of the month
    fun loanOfficersPayout() {

        // Fetch Loan Officers
        val loanOfficers = userRepository.findAllByPosition(UserPosition.LoanOfficer)

        runBlocking {
            TaskManagers.processCounterChannelTasks<User, String>(
                inputs = loanOfficers,
                inputProcessor = { user ->
                    MpesaTransactions.processB2CTransaction(
                        transactionId = user.id,
                        customer = user,
                        amount = 45000f,
                        responseCallback = {}
                    )
                    user.id
                },
                outputReceiver = {}
            )
        }
    }

    @Scheduled(cron = "0 0 8 L * *") // Every last day of the month
    fun shareHoldersPayout() {

        // Fetch Loan Officers
        val shareHolders = userRepository.findAllByPosition(UserPosition.ShareHolder)

        runBlocking {
            TaskManagers.processCounterChannelTasks<User, String>(
                inputs = shareHolders,
                inputProcessor = { user ->
                    MpesaTransactions.processB2CTransaction(
                        transactionId = user.id,
                        customer = user,
                        amount = 90000f,
                        responseCallback = {}
                    )
                    user.id
                },
                outputReceiver = {}
            )
        }
    }
}