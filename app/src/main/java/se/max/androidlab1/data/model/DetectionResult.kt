package se.max.androidlab1.data.model

data class DetectionResult(val confidenceScore: Int, val isAttack: Boolean, val maliciousComment: String)
