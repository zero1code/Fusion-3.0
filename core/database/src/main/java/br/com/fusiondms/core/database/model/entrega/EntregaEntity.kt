package br.com.fusiondms.core.database.model.entrega

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import br.com.fusiondms.core.database.mapper.EntityMapper
import br.com.fusiondms.core.model.entrega.Entrega
import com.google.gson.Gson
import java.math.BigDecimal

@Entity(tableName = "tb_entrega")
data class EntregaEntity(
    val id: Int = 0,
    @PrimaryKey()
    val idEntrega: Int = 0,
    val idRomaneio: Int = 0,
    val idCliente: Int = 0,
    val ordermEntrega: Int = 0,
    val dadosCliente: String = "",
    val localCliente: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val numeroNotaFiscal: Int = 0,
    val valor: String = "",
    val statusEntrega: String = "0",
    val formaPagamento: String = ""
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
