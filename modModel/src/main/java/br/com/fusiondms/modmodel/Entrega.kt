package br.com.fusiondms.modmodel

import br.com.fusiondms.moddatabase.model.EntregaEntity

import com.google.gson.Gson

data class Entrega(
    var idEntrega: Int = 0,
    var ordermEntrega: Int = 0,
    var dadosCliente: String = "",
    var localCliente: String = "",
    var numeroNotaFiscal: Int = 0,
    var valor: String = "",
    var statusEntrega: String = ""
) {
    fun mapToModel(T : Any) : Entrega {
        val json = Gson().toJson(T)
        return Gson().fromJson(json, Entrega::class.java)
    }

    fun mapToEntity() : EntregaEntity {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, EntregaEntity::class.java)
    }

//    fun mapToDTO() : EntregaDTO {
//        val json = Gson().toJson(this)
//        return Gson().fromJson(json, EntregaDTO::class.java)
//    }
}
