package com.openclassrooms.realestatemanager.models

sealed class State<out T> {
    class Loading<out R>: State<R>()
    data class Success<out R>(val value: R): State<R>()
    data class Failure(val throwable: Throwable?): State<Nothing>()
}