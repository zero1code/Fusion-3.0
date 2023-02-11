package br.com.fusiondms.core.databasejornadatrabalho.model.jornadatrabalho

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fusiondms.core.databasejornadatrabalho.mapper.EntityMapper
import br.com.fusiondms.core.model.jornadatrabalho.RegistroPonto
import com.google.gson.Gson

@Entity(tableName = "tb_registros_ponto")
data class RegistroPontoEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var matricula: Int = 0,
    var dataRegistro: Long = 0L,
    var registroEfetuado: Boolean = false
) : EntityMapper<RegistroPonto, RegistroPontoEntity> {
    override fun mapEntityToModel(entity: RegistroPontoEntity): RegistroPonto {
        val json = Gson().toJson(entity)
        return Gson().fromJson(json, RegistroPonto::class.java)
    }

    override fun mapModelToEntity(model: RegistroPonto): RegistroPontoEntity {
        val json = Gson().toJson(model)
        return Gson().fromJson(json, RegistroPontoEntity::class.java)
    }

    fun entityListToModel(entityList: List<RegistroPontoEntity>): List<RegistroPonto> {
        return entityList.map {
            mapEntityToModel(it)
        }
    }

    fun modelListToEntity(registroPontoList: List<RegistroPonto>): List<RegistroPontoEntity> {
        return registroPontoList.map {
            mapModelToEntity(it)
        }
    }

}
