package br.com.fusiondms.core.model.exceptions

class ErrorApiSincronizacao(message: String?, val code: Int = 1000) : Exception(
    "$message <br>Código: <b>$code</b>"
)