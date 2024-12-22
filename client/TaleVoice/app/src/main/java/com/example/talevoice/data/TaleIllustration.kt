package com.example.talevoice.data

import kotlinx.serialization.Serializable

@Serializable
data class TaleIllustration(
    val page: Int,
    val image: String // null인 경우 생성 중
)
