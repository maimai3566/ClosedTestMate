package com.rururi.closedtestmate.recruit.domain

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.rururi.closedtestmate.R

sealed class RecruitStatus(
    @StringRes val labelResId: Int,
    @DrawableRes val iconResId: Int,
    val fireStoreLabel: String = ""
) {
    object Open: RecruitStatus(R.string.status_open, R.drawable.open,"募集中")
    object Closed: RecruitStatus(R.string.status_closed, R.drawable.closed,"募集終了")
    object Released: RecruitStatus(R.string.status_released, R.drawable.released,"リリース済")

    companion object {
        // Firestoreのステータス文字列からステータスオブジェクトに変換する
        fun fromFirestoreString(status: String): RecruitStatus = when (status) {
            Open.fireStoreLabel -> Open
            Closed.fireStoreLabel -> Closed
            Released.fireStoreLabel -> Released
            else -> Open
        }
    }
}