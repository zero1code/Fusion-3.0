package br.com.fusiondms.core.database.model.entrega

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fusiondms.core.database.mapper.EntityMapper
import br.com.fusiondms.core.model.entrega.EntregaItem
import com.google.gson.Gson
import java.math.BigDecimal

@Entity(tableName = "tb_entrega_item")
data class EntregaItemEntity(
    var idRomaneio: Int = 0,
    var idEntrega: Int = 0,
    @PrimaryKey
    var idEntregaItem: Long = 0,
    var codigoMercadoria: String = "",
    var descricao: String = "",
    var quantidade: Double = 0.0,
    var preco: String = "",
    var unidade: String = "",
    var valorST: String = "",
    var subTotal: String = "",
    var peso: Double = 0.0,
    val motivoDevolucao: String = "",
    val quantidadeDevolucao: Double = 0.0,
    val entregue: Boolean = false
) : EntityMapper<EntregaItem, EntregaItemEntity> {

    override fun mapEntityToModel(entity: EntregaItemEntity): EntregaItem {
        val json = Gson().toJson(entity)
        return Gson().fromJson(json, EntregaItem::class.java)
    }

    override fun mapModelToEntity(model: EntregaItem): EntregaItemEntity {
        val json = Gson().toJson(model)
        return Gson().fromJson(json, EntregaItemEntity::class.java)
    }

    fun entityListToModel(entityList: List<EntregaItemEntity>): List<EntregaItem> {
        return entityList.map {
            mapEntityToModel(it)
        }
    }

    fun modelListToEntity(modelList: List<EntregaItem>): List<EntregaItemEntity> {
        return modelList.map {
            mapModelToEntity(it)
        }
    }

}
