package com.entryventures.crons

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

private const val NUMBER_OF_TASK_WORKERS = 2

object TaskManagers {
    suspend fun <I, O> processCounterChannelTasks(
        inputs: List<I>,
        inputProcessor: suspend (I) -> O?,
        outputReceiver: suspend (List<O>) -> Unit
    ) = coroutineScope {
        val inputChannel = Channel<I>()
        val outputChannel = Channel<O>()

        /**
         * Send input to channel for processing
         */

        /**
         * Send input to channel for processing
         */
        launch {
            /**
             * Wait until worker coroutine receives the input for processing
             */
            /**
             * Wait until worker coroutine receives the input for processing
             */
            inputs.forEach { inputChannel.send(it) }
            inputChannel.close()
        }

        launch {
            (1..NUMBER_OF_TASK_WORKERS).map {
                /**
                 * A worker coroutine - Takes input from the input channel, and initiates processing
                 */
                /**
                 * A worker coroutine - Takes input from the input channel, and initiates processing
                 */
                launch {
                    for (input in inputChannel) {
                        inputProcessor(input)?.also { outputChannel.send(it) }
                    }
                }
            }.joinAll()
            outputChannel.close()
        }

        // Receive output
        outputReceiver(outputChannel.toList())
    }
}