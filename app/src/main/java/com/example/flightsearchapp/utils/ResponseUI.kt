package com.example.flightsearchapp.utils

/**
 * Sealed interface for App UI state
 */
sealed interface ResponseUI {
    object Success: ResponseUI
    data class Error(val message: String?): ResponseUI
    object Loading: ResponseUI
}