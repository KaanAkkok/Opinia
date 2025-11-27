package com.example.opinia.di

import com.example.opinia.data.repository.InstructorRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// VIEWMODELLER İÇİN GEREKLİ OLAN DEPENDENCY INJECTİON'I BURADAN YAP
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideInstructorRepository(firestore: FirebaseFirestore): InstructorRepository {
        return InstructorRepository(firestore)
    }
}
