package br.com.fusiondms.moddatabase.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fusiondms.moddatabase.mapper.EntityMapper
import br.com.fusiondms.modmodel.Entrega
import com.google.gson.Gson

@Entity(tableName = "tb_entregas")
data class EntregaEntity(
    var id: Int = 0,
    @PrimaryKey()
    var idEntrega: Int = 0,
    var idRomaneio: Int = 0,
    var idCliente: Int = 0,
    var ordermEntrega: Int = 0,
    var dadosCliente: String = "",
    var localCliente: String = "",
    var numeroNotaFiscal: Int = 0,
    var valor: String = "",
    var statusEntrega: String = "0"
) : EntityMapper<Entrega, EntregaEntity> {

    override fun mapEntityToModel(entity: EntregaEntity): Entrega {
        val json = Gson().toJson(entity)
        return Gson().fromJson(json, Entrega::class.java)
    }

    override fun mapModelToEntity(model: Entrega): EntregaEntity {
        val json = Gson().toJson(model)
        return Gson().fromJson(json, EntregaEntity::class.java)
    }

    fun entityListToModel(entityList: List<EntregaEntity>): List<Entrega> {
        return entityList.map {
            mapEntityToModel(it)
        }
    }

    fun modelListToEntity(entregaList: List<Entrega>): List<EntregaEntity> {
        return entregaList.map {
            mapModelToEntity(it)
        }
    }
}
