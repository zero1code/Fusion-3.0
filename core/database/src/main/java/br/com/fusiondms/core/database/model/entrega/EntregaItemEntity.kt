package br.com.fusiondms.core.database.model.entrega

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fusiondms.core.database.mapper.EntityMapper
import br.com.fusiondms.core.model.entrega.EntregaItem
import com.google.gson.Gson
import java.math.BigDecimal

@Entity(tableName = "tb_entrega_item")
data class EntregaItemEntity(
    val idRomaneio: Int = 0,
    val idEntrega: Int = 0,
    @PrimaryKey
    val idEntregaItem: Long = 0,
    val codigoMercadoria: String = "",
    val descricao: String = "",
    val quantidade: Double = 0.0,
    val preco: String = "",
    val unidade: String = "",
    val valorST: String = "",
    val subTotal: String = "",
    val peso: Double = 0.0,
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
