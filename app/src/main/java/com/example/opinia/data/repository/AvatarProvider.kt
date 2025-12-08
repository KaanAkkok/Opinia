package com.example.opinia.data.repository

import com.example.opinia.R
import com.example.opinia.data.model.Avatar
import javax.inject.Inject

class AvatarProvider @Inject constructor() {

    val avatars = listOf(
        Avatar("mor", R.drawable.mor),
        Avatar("turuncu", R.drawable.turuncu),
        Avatar("turkuaz", R.drawable.turkuaz)
    )

    fun getAvatarResId(key: String): Int {
        return avatars.find { it.key == key }?.resId ?: R.drawable.turuncu // eğer null ise default turuncu dönsün
    }
}