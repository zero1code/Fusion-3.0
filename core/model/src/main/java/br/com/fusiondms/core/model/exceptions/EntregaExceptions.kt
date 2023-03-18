package br.com.fusiondms.core.model.exceptions

class ErrorAtualizarStatusEntrega(message: String?, val code: Int = 1100) : Exception(
    "$message <br>Code: <b>$code</b>"
)