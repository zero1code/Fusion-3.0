package br.com.fusiondms.moddatabase.model

import androidx.room.PrimaryKey
import br.com.fusiondms.moddatabase.mapper.EntityMapper
import br.com.fusiondms.modmodel.ReciboRegistroPonto
import com.google.gson.Gson


data class ReciboRegistroPontoEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var matricula: Int = 0,
    var dataRegistro: Long = 0L,
    var registroEfetuado: Boolean = false,
    var nome: String = ""
) : EntityMapper<ReciboRegistroPonto, ReciboRegistroPontoEntity> {
    override fun mapEntityToModel(entity: ReciboRegistroPontoEntity): ReciboRegistroPonto {
        val json = Gson().toJson(entity)
        return Gson().fromJson(json, ReciboRegistroPonto::class.java)
    }

    override fun mapModelToEntity(model: ReciboRegistroPonto): ReciboRegistroPontoEntity {
        val json = Gson().toJson(model)
        return Gson().fromJson(json, ReciboRegistroPontoEntity::class.java)
    }

    fun entityListToModel(entityList: List<ReciboRegistroPontoEntity>): List<ReciboRegistroPonto> {
        return entityList.map {
            mapEntityToModel(it)
        }
    }

    fun modelListToEntity(registroPontoList: List<ReciboRegistroPonto>): List<ReciboRegistroPontoEntity> {
        return registroPontoList.map {
            mapModelToEntity(it)
        }
    }

}
