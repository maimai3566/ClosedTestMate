package com.rururi.closedtestmate.recruit.ui.recruitnew

sealed interface RecruitNewEvent {
    data object SaveSucceeded : RecruitNewEvent
    data class SaveFailed(val msg: String) : RecruitNewEvent
}