package br.com.fusiondms.core.model.exceptions

class ErrorApiRecusarRomaneio(message: String?, val code: Int = 1050) : Exception(
    "$message <br>Code: <b>$code</b>"
)

class ErrorDeletarRomaneio(message: String?, val code: Int = 1051) : Exception(
    "$message <br>Code: <b>$code</b>"
)

class ErrorGetListaRomaneio(message: String?, val code: Int = 1050) : Exception(
    "$message <br>Code: <b>$code</b>"
)