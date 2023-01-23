package br.com.fusiondms.moddatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fusiondms.moddatabase.mapper.EntityMapper
import br.com.fusiondms.modmodel.Entrega
import br.com.fusiondms.modmodel.Romaneio
import com.google.gson.Gson

@Entity(tableName = "tb_romaneios")
data class RomaneioEntity(
    var id: Int = 0,
    @PrimaryKey()
    var idRomaneio: Int = 0,
    var destino: String = "",
    var qtdEntregas: Int = 0,
    var status: String = "",
    var kmTotal: Int = 0,
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