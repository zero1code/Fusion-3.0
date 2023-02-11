package br.com.fusiondms.core.databasejornadatrabalho.model.jornadatrabalho

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.fusiondms.core.databasejornadatrabalho.mapper.EntityMapper
import br.com.fusiondms.core.model.jornadatrabalho.Colaborador
import com.google.gson.Gson

@Entity(tableName = "tb_colaboradores")
data class ColaboradorEntity(
    @PrimaryKey()
    var matricula: Int = 0,
    var nome: String = "",
    var funcao: String = "",
) : EntityMapper<Colaborador, ColaboradorEntity> {
    override fun mapEntityToModel(entity: ColaboradorEntity): Colaborador {
        val json = Gson().toJson(entity)
        return Gson().fromJson(json, Colaborador::class.java)
    }

    override fun mapModelToEntity(model: Colaborador): ColaboradorEntity {
        val json = Gson().toJson(model)
        return Gson().fromJson(json, ColaboradorEntity::class.java)
    }

    fun entityListToModel(entityList: List<ColaboradorEntity>): List<Colaborador> {
        return entityList.map {
            mapEntityToModel(it)
        }
    }

    fun modelListToEntity(entregaList: List<Colaborador>): List<ColaboradorEntity> {
        return entregaList.map {
            mapModelToEntity(it)
        }
    }
}
