package co.aspirasoft.catalyst.utils.db

interface Database<in Q> {

    @Throws(Exception::class)
    suspend fun <S> get(query: Q): S?

}