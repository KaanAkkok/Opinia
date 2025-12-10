package com.example.opinia.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opinia.data.repository.AuthRepository // AuthRepository eklendi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository // FirebaseAuth yerine Repo enjekte edildi
) : ViewModel() {

    // Çıkış yapma işlemi
    fun signOut(onSignOutComplete: () -> Unit) {
        // Repository üzerinden logout çağrısı
        val result = authRepository.logout()

        if (result.isSuccess) {
            onSignOutComplete()
        } else {
            // Hata olursa loglanabilir veya kullanıcıya gösterilebilir
            // Şimdilik akışı bozmamak için yine de yönlendirme yapabiliriz veya hata mesajı gösterebiliriz.
            onSignOutComplete()
        }
    }
}