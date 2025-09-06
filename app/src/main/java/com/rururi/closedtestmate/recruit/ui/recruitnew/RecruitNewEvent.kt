package com.rururi.closedtestmate.recruit.ui.recruitnew

sealed interface RecruitNewEvent {
    object SaveSucceeded : RecruitNewEvent
    object SaveFailed: RecruitNewEvent
}