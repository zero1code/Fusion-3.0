package br.com.fusiondms.core.network.model

import br.com.fusiondms.core.database.model.entrega.EntregaEntity
import br.com.fusiondms.core.database.model.entrega.EntregaItemEntity
import br.com.fusiondms.core.database.model.romaneio.RomaneioEntity
import com.google.gson.annotations.SerializedName

data class SincronizacaoDto(
    @SerializedName("romaneios")
    val listaRomaneio: List<RomaneioEntity>,
    @SerializedName("entregas")
    val listaEntrega: List<EntregaEntity>,
    @SerializedName("entregas_itens")
    val listaEntregaItem: List<EntregaItemEntity>
)