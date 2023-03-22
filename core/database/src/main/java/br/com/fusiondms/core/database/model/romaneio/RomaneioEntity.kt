package br.com.fusiondms.core.database.model.romaneio

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fusiondms.core.database.mapper.EntityMapper
import br.com.fusiondms.core.model.romaneio.Romaneio
import com.google.gson.Gson

@Entity(tableName = "tb_romaneio")
data class RomaneioEntity(
    val id: Int = 0,
    @PrimaryKey()
    val idRomaneio: Int = 0,
    val destino: String = "",
    val qtdEntregas: Int = 0,
    val status: String = "",
    val kmTotal: Int = 0,
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