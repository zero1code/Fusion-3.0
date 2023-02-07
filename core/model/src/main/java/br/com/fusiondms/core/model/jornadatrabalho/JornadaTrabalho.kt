package br.com.fusiondms.modmodel.jornadatrabalho

data class JornadaTrabalho(
    val colaborador: Colaborador,
    val registroPonto: List<RegistroPonto>
)
