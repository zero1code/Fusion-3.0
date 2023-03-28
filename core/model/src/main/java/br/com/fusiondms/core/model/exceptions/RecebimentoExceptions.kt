package br.com.fusiondms.core.model.exceptions

class ErrorInserirRecebimento(message: String?, val code: Int = 1150) : Exception(
    "$message <br>Code: <b>$code</b>"
)