package com.rururi.closedtestmate.recruit.domain

import kotlinx.coroutines.flow.Flow

//リポジトリのインタフェース
interface RecruitRepository {
    fun getList(limit:Int = 50): Flow<List<Recruit>>    //一覧取得
    suspend fun getById(id: String): Recruit?    //詳細取得
    suspend fun add(recruit: Recruit): String    //登録
    suspend fun update(id: String, recruit: Recruit)    //更新
    suspend fun delete(id: String)    //削除
}