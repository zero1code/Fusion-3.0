package br.com.fusiondms.moddatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fusiondms.moddatabase.mapper.EntityMapper
import br.com.fusiondms.modmodel.Entrega
import br.com.fusiondms.modmodel.Romaneio
import com.google.gson.Gson

@Entity(tableName = "tb_romaneios")
data class RomaneioEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var status: String = "",
    var track_id: Int = 0,
    var lastPointSent: Int = 0,
    var rastreador_id: Int = 0,
    var empresa_id: Int = 0,
    var transporte_id: Int = 0,
    var nomeTransporte: String = "",
    var placa: String = "",
    val kmTransporte: Double = 0.0,
    var motorista_id: Int = 0,
    var motoristaCodigoErp: String = "",
    var motorista: String = "",
    var dataSaida: String = "",
    var kmTotal: Double = 0.0,
    var tempoTotal: Int = 0,
    var diariasSimples: Int = 0,
    var diariasCompletas: Int = 0,
    var valorAdiantado: Double = 0.0,
    var dataTermino: String = "",
    var idFilial: Int = 0,
    var nomeFilial: String = "",
    var cidadeFilial: String = "",
    var ufFilial: String = "",
    var latitudeFilial: Double = 0.0,
    var longitudeFilial: Double = 0.0,
    var telefoneFinanceiro: String = "",
    var ajudante1: String = "",
    var ajudante2: String = "",
    var ajudante1_id: Int = 0,
    var ajudante2_id: Int = 0,
    var ajudante3_id: Int = 0,
    var ajudante4_id: Int = 0,
    val destino: String = "",
    val cargaERP: String = "",
    val numEntregas: Int = 0,
    val numPedidos: Int = 0,
    val valorTotal: Double = 0.0,
    val pesoTotal: Double = 0.0,
    val senhaMotorista: String = "",
    val telefoneMotorista: String = "",
    val tipo_contrato: String = "",
    val dataHoraUltimaEntrega: String = "",
    val razaoSocialFilial: String = "",
    val cnpjFilial: String = "",
    val enderecoFilial: String = "",
    val cepFilial: String? = "",
    var corIdentificador: Int = 0
) : EntityMapper<Romaneio, RomaneioEntity> {

    override fun mapEntityToModel(entity: RomaneioEntity): Romaneio {
        val json = Gson().toJson(entity)
        return Gson().fromJson(json, Romaneio::class.java)
    }

    override fun mapModelToEntity(model: Romaneio): RomaneioEntity {
        val json = Gson().toJson(model)
        return Gson().fromJson(json, RomaneioEntity::class.java)
    }

    fun entityListToModel(entityList: List<RomaneioEntity>): List<Romaneio> {
        return entityList.map {
            mapEntityToModel(it)
        }
    }

    fun modelListToEntity(entregaList: List<Romaneio>): List<RomaneioEntity> {
        return entregaList.map {
            mapModelToEntity(it)
        }
    }
}