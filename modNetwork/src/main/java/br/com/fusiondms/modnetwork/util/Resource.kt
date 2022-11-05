package br.com.fusiondms.modnetwork.util

sealed class Resource<T>(val message: String, val responseCode: Int) {
    class Success<T>(message: String, responseCode: Int) : Resource<T>(message, responseCode)
    class Error<T>(message: String, responseCode: Int) : Resource<T>(message, responseCode)
}
