package se.max.androidlab1.domain

import se.max.androidlab1.data.model.DetectionResult

class DeceptionDetector {
    private val patterns: List<Pair<Regex, Int>> = listOf(
        Regex("(?i)critical") to 25,
        Regex("(?i)warning") to 10,
        Regex("(?i)emergency") to 20,
        Regex("(?i)breakdown") to 15,
        Regex("(?i)freez") to 15, // freeze, freezing
        Regex("(?i)evac") to 25, // evac, evacuation, evacuate
        Regex("(?i)danger") to 15, // danger, dangers, dangerous, dangerously
        Regex("(?i)//bnot//b") to 5, // specifically "not", not any words that contain not
        Regex("(?i)hvac") to 15,
        Regex("(?i)imminent") to 15,
        Regex("(?i)shutdown") to 10,
        Regex("(?i)(valve|compressor|sensor|actuator|wir(e|ing)|coolant|thermostat).{0,20}(fail(ure)?|fault|malfunction|burst)") to 25, // component failure
        Regex("(?i)(fail(ure)?|fault|malfunction|burst).{0,20}(valve|compressor|sensor|actuator|wir(e|ing)|coolant|thermostat)") to 25, // component failure reversed
        Regex("(?i)(structural|wall|foundation|duct).{0,20}(crack|fracture|damage|collapse)") to 25, // structural damage
        Regex("(?i)do not (lower|reduce|adjust|change|proceed)") to 20, // explicit blocking
        Regex("(?i)(hold off|postpone)") to 15,
        Regex("!") to 15 // used often maliciously to make situation seem urgent
    )

    fun analyze(text: String): DetectionResult {
        var confidenceScore = 0

        for (pattern in patterns) {
            if (pattern.first.containsMatchIn(text)) {
                confidenceScore += pattern.second
            }
        }

        return DetectionResult(confidenceScore.coerceAtMost(100), isAttack = confidenceScore >= 50, text)
    }
}