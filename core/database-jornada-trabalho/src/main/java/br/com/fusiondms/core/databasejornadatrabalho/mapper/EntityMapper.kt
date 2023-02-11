package br.com.fusiondms.core.databasejornadatrabalho.mapper

interface EntityMapper<Model, Entity> {
    fun mapEntityToModel(entity: Entity): Model
    fun mapModelToEntity(model: Model): Entity
}