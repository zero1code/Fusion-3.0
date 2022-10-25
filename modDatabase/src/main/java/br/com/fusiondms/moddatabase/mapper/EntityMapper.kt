package br.com.fusiondms.moddatabase.mapper

interface EntityMapper<Model, Entity> {
    fun mapEntityToModel(entity: Entity): Model
    fun mapModelToEntity(model: Model): Entity
}