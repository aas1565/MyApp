package com.example.hw_3.di

import com.example.hw_3.data.repository.MatchesRepositoryImpl
import com.example.hw_3.domain.repository.MatchesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMatchesRepository(
        matchesRepositoryImpl: MatchesRepositoryImpl
    ): MatchesRepository
}
