package br.com.fusiondms.core.model.jornadatrabalho

data class JornadaTrabalho(
    val colaborador: Colaborador,
    val registroPonto: List<RegistroPonto>
)
