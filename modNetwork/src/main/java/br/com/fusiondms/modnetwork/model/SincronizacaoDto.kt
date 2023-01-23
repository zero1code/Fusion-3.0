package br.com.fusiondms.modnetwork.model

import br.com.fusiondms.moddatabase.model.jornadatrabalho.ColaboradorEntity
import br.com.fusiondms.moddatabase.model.entrega.EntregaEntity
import br.com.fusiondms.moddatabase.model.jornadatrabalho.RegistroPontoEntity
import br.com.fusiondms.moddatabase.model.romaneio.RomaneioEntity
import com.google.gson.annotations.SerializedName

data class SincronizacaoDto(
    @SerializedName("romaneios")
    val listaRomaneio: List<RomaneioEntity>,
    @SerializedName("entregas")
    val listaEntrega: List<EntregaEntity>,
    @SerializedName("colaboradores")
    val listaColaborador: List<ColaboradorEntity>,
    @SerializedName("registros_ponto")
    val listaRegistroPonto: List<RegistroPontoEntity>
)