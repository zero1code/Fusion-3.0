package br.com.fusiondms.core.database.model.recebimento

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fusiondms.core.database.mapper.EntityMapper
import br.com.fusiondms.core.database.model.entrega.EntregaItemEntity
import br.com.fusiondms.core.model.entrega.EntregaItem
import br.com.fusiondms.core.model.recebimento.Recebimento
import com.google.gson.Gson

@Entity(tableName = "tb_recebimento")
data class RecebimentoEntity(
    @PrimaryKey
    val id: Int = 0,
    val idRomaneio: Int = 0,
    val idEntrega: Int = 0,
    val valor: String = "0",
    val tipo: String = "",
    val banco: String = "",
    val agencia: String = "",
    val conta: String = "",
    val numCh: String = "",
    val dataCh: String = "",
    val codigoTransacao: String = "",
    val formaPagamento: String = "",
    val parcelas: Int = 0,
    val bandeira: String = "",
    val nsu: String = "",
    val txid: String = "",
    val dataRecebimento: Long = 0
) : EntityMapper<Recebimento, RecebimentoEntity> {
    override fun mapEntityToModel(entity: RecebimentoEntity): Recebimento {
        val json = Gson().toJson(entity)
        return Gson().fromJson(json, Recebimento::class.java)
    }

    override fun mapModelToEntity(model: Recebimento): RecebimentoEntity {
        val json = Gson().toJson(model)
        return Gson().fromJson(json, RecebimentoEntity::class.java)
    }

    fun entityListToModel(entityList: List<RecebimentoEntity>): List<Recebimento> {
        return entityList.map {
            mapEntityToModel(it)
        }
    }

    fun modelListToEntity(modelList: List<Recebimento>): List<RecebimentoEntity> {
        return modelList.map {
            mapModelToEntity(it)
        }
    }
}
