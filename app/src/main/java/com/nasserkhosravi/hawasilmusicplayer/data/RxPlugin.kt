package com.nasserkhosravi.hawasilmusicplayer.data

import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * A controllable interval observable can be pause and resume
 */
open class SuspendableObservable(period: Long, timeUnit: TimeUnit) {

    private val canResume = AtomicBoolean(false)
    private val tick = AtomicLong(0L)

    open var observable: Observable<Number> = Observable.interval(period, timeUnit)
        .takeWhile { canResume.get() }
        .repeat()
        .map { tick.incrementAndGet() }

    fun pause() {
        canResume.set(false)
    }

    fun resume() {
        canResume.set(true)
    }

    fun isPlay(): Boolean {
        return canResume.get()
    }
}