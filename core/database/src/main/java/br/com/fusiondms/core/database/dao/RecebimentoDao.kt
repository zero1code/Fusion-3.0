package br.com.fusiondms.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.fusiondms.core.database.model.recebimento.RecebimentoEntity
import br.com.fusiondms.core.database.model.recebimento.TipoPagamentoEntity

@Dao
interface RecebimentoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirAllRecebimentos(listaRecebimento: List<RecebimentoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirRecebimento(recebimento: RecebimentoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirAllFormasPagamento(listaTipoPagamento: List<TipoPagamentoEntity>)

    @Query("SELECT * FROM tb_recebimento WHERE idRomaneio = :idRomaneio AND idEntrega = :idEntrega")
    suspend fun getAllRecebimento(idRomaneio: Int, idEntrega: Int) : List<RecebimentoEntity>

//    suspend fun getAllFormasPagamento()

    @Query("DELETE FROM tb_recebimento WHERE idRomaneio = :idRomaneio AND idEntrega = :idEntrega")
    suspend fun deleteRecebimento(idRomaneio: Int, idEntrega: Int) : Int

}