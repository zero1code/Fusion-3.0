package br.com.fusiondms.modmodel

data class JornadaTrabalho(
    val colaborador: Colaborador,
    val registroPonto: List<RegistroPonto>
)
