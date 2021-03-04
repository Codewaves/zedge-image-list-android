package com.example.zedgeimagelist.presentation

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun View.onThrottledClick(scope: CoroutineScope, delay: Long = 500L, block: (view: View) -> Unit) {
    scope.launch {
        while (true) {
            suspendCoroutine<Unit> { continuation ->
                setOnClickListener {
                    continuation.resume(Unit)
                    setOnClickListener(null)
                }
            }
            block(this@onThrottledClick)
            delay(delay)
        }
    }
}

class ClickThrottle(
    private val scope: CoroutineScope,
    private val delay: Long = 500L,
    private val block: () -> Unit
) {
    private val channel = Channel<Unit?>()

    init {
        scope.launch {
            var nextTime = 0L
            channel.consumeEach {
                val curTime = System.currentTimeMillis()
                if (curTime >= nextTime) {
                    nextTime = curTime + delay
                    block()
                }
            }
        }
    }

    fun click() {
        scope.launch {
            channel.send(null)
        }
    }
}