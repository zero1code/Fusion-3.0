package br.com.fusiondms.moddatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_entregas")
data class EntregaEntity(
    @PrimaryKey(autoGenerate = true)
    var idEntrega: Int = 0,
    var ordermEntrega: Int = 0,
    var dadosCliente: String = "",
    var localCliente: String = "",
    var numeroNotaFiscal: Int = 0,
    var valor: String = "",
    var statusEntrega: String = ""
) {

}
