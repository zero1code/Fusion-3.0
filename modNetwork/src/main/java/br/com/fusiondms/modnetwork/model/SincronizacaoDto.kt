package br.com.fusiondms.modnetwork.model

import br.com.fusiondms.moddatabase.model.EntregaEntity
import br.com.fusiondms.moddatabase.model.RomaneioEntity
import com.google.gson.annotations.SerializedName

data class SincronizacaoDto(
    @SerializedName("romaneios")
    val listaRomaneio: List<RomaneioEntity>,
    @SerializedName("entregas")
    val listaEntrega: List<EntregaEntity>
)