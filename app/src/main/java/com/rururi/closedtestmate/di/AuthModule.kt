package com.rururi.closedtestmate.di

import com.rururi.closedtestmate.data.AuthRepository
import com.rururi.closedtestmate.data.FirebaseAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module //Hiltにこのクラスは依存性注入の設定を描いたモジュールですと伝える
@InstallIn(SingletonComponent::class)   //このモジュールが提供する依存性をアプリ全体で使えるようにする
abstract class AuthModule {
    @Binds  //インターフェースへの依存を解決するときはこれを使ってねとHiltに伝える
    @Singleton  //どのViewModelからも同じFirebaseAuthRepositoryインスタンスが共有される
    //AuthRepositoryが呼ばれたら、FirebaseAuthRepositoryを返す
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepository    //実装
    ): AuthRepository               //戻り値はインタフェース
}