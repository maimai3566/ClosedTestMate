package com.rururi.closedtestmate.recruit.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rururi.closedtestmate.recruit.domain.Recruit
import com.rururi.closedtestmate.recruit.domain.RecruitRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**データの実態
 * Firestoreから受け取ったデータはDomain型にして渡す
 * Firestoreに保存するデータはDto型にして渡す
 **/
@Singleton
class RecruitRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) : RecruitRepository {
    private val col get() = db.collection("recruits")

    override fun getList(limit: Int) = callbackFlow {
        val reg = col.orderBy("postedAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = value?.documents?.mapNotNull { d ->
                    val raw = d.get("postedAt")
                    if (raw != null && raw !is Timestamp) return@mapNotNull null
                    d.toObject(RecruitDto::class.java)?.toDomain(d.id)
                }.orEmpty()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    override suspend fun getById(id: String):Recruit? =
        col.document(id).get().await()
            .toObject(RecruitDto::class.java)
            ?.toDomain(id)

    override suspend fun add(recruit: Recruit): String {
        val dto = recruit.toDto()
        Log.d("ruruv", "add: $dto")
        return col.add(dto).await().id
    }

    override suspend fun update(id: String, recruit: Recruit) {
        val dto = recruit.toDto()
        col.document(id).set(dto).await()
    }

    override suspend fun delete(id: String) {
        col.document(id).delete().await()
    }
}