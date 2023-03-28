package br.com.fusiondms.core.database.model.recebimento

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fusiondms.core.database.mapper.EntityMapper
import br.com.fusiondms.core.model.recebimento.TipoPagamento
import com.google.gson.Gson

@Entity(tableName = "tb_tipo_pagamento")
data class TipoPagamentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descricao: String = "",
    val tipoFusion: String = "",
    val tipoErp: String = "",
    val solicitaPagamento: Boolean = false,
    val obrigatorioErp: Boolean = false,
    val permitirMudarTipoPagamento: Boolean = false,
    val formasPagamento: String = "",
    val bandeiraCartao: String = "",
    val qtdParcelaCartao: Int = 0,
    val pixKey: String = "",
    val pixNome: String = "",
    val pixDescricao: String = "",
    val pixCidade: String = ""
) : EntityMapper<TipoPagamento, TipoPagamentoEntity> {
    override fun mapEntityToModel(entity: TipoPagamentoEntity): TipoPagamento {
        val json = Gson().toJson(entity)
        return Gson().fromJson(json, TipoPagamento::class.java)
    }

    override fun mapModelToEntity(model: TipoPagamento): TipoPagamentoEntity {
        val json = Gson().toJson(model)
        return Gson().fromJson(json, TipoPagamentoEntity::class.java)
    }

    fun entityListToModel(entityList: List<TipoPagamentoEntity>): List<TipoPagamento> {
        return entityList.map {
            mapEntityToModel(it)
        }
    }

    fun modelListToEntity(modelList: List<TipoPagamento>): List<TipoPagamentoEntity> {
        return modelList.map {
            mapModelToEntity(it)
        }
    }

}
