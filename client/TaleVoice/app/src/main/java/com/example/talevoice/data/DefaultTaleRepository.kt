package com.example.talevoice.data

import com.example.talevoice.data.source.local.LocalTaleListItem
import com.example.talevoice.data.source.local.TaleDao
import com.example.talevoice.data.source.server.NetworkTaleCreationRequest
import com.example.talevoice.data.source.server.TaleApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DefaultTaleRepository (
    private val localDataSource: TaleDao,
    private val networkApiService: TaleApiService,
    private val dispatcher: CoroutineDispatcher,
) : TaleRepository{

    fun TaleListItem.toLocalTaleListItem(): LocalTaleListItem {
        return LocalTaleListItem(
            taleId = this.taleId,
            title = this.title,
            version = this.version
        )
    }
    fun List<TaleListItem>.toLocalTaleListItems(): List<LocalTaleListItem> {
        return this.map { it.toLocalTaleListItem() }
    }

    override fun getTaleList(): Flow<List<TaleListItem>> = flow {
        try {
            val taleList = localDataSource.getTaleList()
            emit(taleList)
            val response = networkApiService.getTaleList()
            if (response.isSuccessful){
                val taleListFromNetwork = response.body()?.data
                if (taleListFromNetwork != null){
                    emit(taleListFromNetwork)

                    localDataSource.upsertTaleList(taleListFromNetwork.toLocalTaleListItems())
                }
            } else {
                throw Exception("Network request failed: ${response.errorBody()?.string()}")
            }

        } catch (_: Exception) {
            emit(emptyList<TaleListItem>())
        }
    }.flowOn(dispatcher)

    override suspend fun getTaleItem(taleId: String) : TaleItem{
        return withContext(dispatcher){
            val response = networkApiService.getTaleDetail(taleId) // NetworkTaleContent 요청
            if (response.isSuccessful) {
                val networkTaleContent = response.body()
                networkTaleContent?.data ?: throw IllegalStateException("Response body is null")
            } else {
                throw Exception("Failed to fetch tale details: ${response.errorBody()?.string()}")
            }
        }
    }
    override suspend fun createTale(name: String, gender: String): TaleCreation {
        val request = NetworkTaleCreationRequest(name = name, gender = gender)
        val response = networkApiService.createTale(request)
        try {
            if (response.isSuccessful) {
                val networkTaleCreation = response.body()
                return networkTaleCreation?.data ?: throw IllegalStateException("Response body is null")
            } else {
                throw Exception("Error creating tale: ${response.errorBody()?.string()}")
            }
        } finally {
            response.errorBody()?.close()
        }
    }
}