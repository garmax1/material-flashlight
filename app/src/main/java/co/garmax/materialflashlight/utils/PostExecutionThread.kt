package co.garmax.materialflashlight.utils

import io.reactivex.Scheduler

interface PostExecutionThread {
    val scheduler: Scheduler
}