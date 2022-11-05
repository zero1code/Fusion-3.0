package br.com.fusiondms.moddatabase.repository.romaneios

import br.com.fusiondms.modmodel.Romaneio

interface RomaneiosRepository {
    suspend fun getListaRomaneio() : List<Romaneio>
}