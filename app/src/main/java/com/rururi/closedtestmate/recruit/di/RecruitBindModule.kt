package com.rururi.closedtestmate.recruit.di

import com.rururi.closedtestmate.recruit.data.RecruitRepositoryImpl
import com.rururi.closedtestmate.recruit.domain.RecruitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecruitBindModule {
    @Binds
    @Singleton
    abstract fun bindRecruitRepository(
        impl: RecruitRepositoryImpl
    ): RecruitRepository
}

//provideはcoreのほうにある