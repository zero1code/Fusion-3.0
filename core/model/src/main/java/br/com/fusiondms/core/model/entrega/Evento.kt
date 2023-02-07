package br.com.fusiondms.modmodel.entrega

import androidx.annotation.DrawableRes

data class Evento(val name: String, @DrawableRes val drawable: Int, val idEvento: Int, var isCliked: Boolean = false)
