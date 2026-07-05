package com.llsl.viper4android.migrate

import com.llsl.viper4android.ui.screens.main.BoolPref
import com.llsl.viper4android.ui.screens.main.EFFECT_PREFS
import com.llsl.viper4android.ui.screens.main.EffectPref
import com.llsl.viper4android.ui.screens.main.IntPref
import com.llsl.viper4android.ui.screens.main.NullableLongPref
import com.llsl.viper4android.ui.screens.main.StringPref
import org.json.JSONArray
import org.json.JSONObject

object V2Export {
    private enum class ArrayKind { INT, BOOL01, DOUBLE }

    private sealed class V2Slot {
        data class TopLevel(
            val key: String,
        ) : V2Slot()

        data class Group(
            val group: String,
            val field: String,
        ) : V2Slot()

        data class GroupArray(
            val group: String,
            val field: String,
            val kind: ArrayKind,
        ) : V2Slot()
    }

    private val V1_TO_V2: Map<String, V2Slot> =
        buildMap {
            // master enable
            put("masterEnabled", V2Slot.TopLevel("masterEnable"))

            // master limiter
            put("outputVolume", V2Slot.Group("masterLimiter", "outputVolume"))
            put("channelPan", V2Slot.Group("masterLimiter", "channelPan"))
            put("limiter", V2Slot.Group("masterLimiter", "threshold"))

            // playbackGainControl (v1 AGC)
            put("agcEnabled", V2Slot.Group("playbackGainControl", "enable"))
            put("agcStrength", V2Slot.Group("playbackGainControl", "strength"))
            put("agcMaxGain", V2Slot.Group("playbackGainControl", "maxGain"))
            put("agcOutputThreshold", V2Slot.Group("playbackGainControl", "outputThreshold"))

            // LUFS
            put("lufsEnabled", V2Slot.Group("lufs", "enable"))
            put("lufsTarget", V2Slot.Group("lufs", "target"))
            put("lufsMaxGain", V2Slot.Group("lufs", "maxGain"))
            put("lufsSpeed", V2Slot.Group("lufs", "speed"))

            // fetCompressor
            put("fetEnabled", V2Slot.Group("fetCompressor", "enable"))
            put("fetThreshold", V2Slot.Group("fetCompressor", "threshold"))
            put("fetRatio", V2Slot.Group("fetCompressor", "ratio"))
            put("fetAutoKnee", V2Slot.Group("fetCompressor", "kneeAuto"))
            put("fetKnee", V2Slot.Group("fetCompressor", "knee"))
            put("fetKneeMulti", V2Slot.Group("fetCompressor", "kneeMulti"))
            put("fetAutoGain", V2Slot.Group("fetCompressor", "gainAuto"))
            put("fetGain", V2Slot.Group("fetCompressor", "gain"))
            put("fetAutoAttack", V2Slot.Group("fetCompressor", "attackAuto"))
            put("fetAttack", V2Slot.Group("fetCompressor", "attack"))
            put("fetMaxAttack", V2Slot.Group("fetCompressor", "maxAttack"))
            put("fetAutoRelease", V2Slot.Group("fetCompressor", "releaseAuto"))
            put("fetRelease", V2Slot.Group("fetCompressor", "release"))
            put("fetMaxRelease", V2Slot.Group("fetCompressor", "maxRelease"))
            put("fetCrest", V2Slot.Group("fetCompressor", "crest"))
            put("fetAdapt", V2Slot.Group("fetCompressor", "adapt"))
            put("fetNoClip", V2Slot.Group("fetCompressor", "noClip"))

            // multibandCompressor (v1 ;-joined strings -> v2 native arrays)
            put("mbcEnabled", V2Slot.Group("multibandCompressor", "enable"))
            put("mbcBandEnables", V2Slot.GroupArray("multibandCompressor", "bandEnables", ArrayKind.BOOL01))
            put("mbcCrossovers", V2Slot.GroupArray("multibandCompressor", "crossovers", ArrayKind.INT))
            put("mbcThresholds", V2Slot.GroupArray("multibandCompressor", "thresholds", ArrayKind.INT))
            put("mbcRatios", V2Slot.GroupArray("multibandCompressor", "ratios", ArrayKind.INT))
            put("mbcGains", V2Slot.GroupArray("multibandCompressor", "gains", ArrayKind.INT))
            put("mbcKnees", V2Slot.GroupArray("multibandCompressor", "knees", ArrayKind.INT))
            put("mbcKneeMultis", V2Slot.GroupArray("multibandCompressor", "kneeMultis", ArrayKind.INT))
            put("mbcAttacks", V2Slot.GroupArray("multibandCompressor", "attacks", ArrayKind.INT))
            put("mbcMaxAttacks", V2Slot.GroupArray("multibandCompressor", "maxAttacks", ArrayKind.INT))
            put("mbcReleases", V2Slot.GroupArray("multibandCompressor", "releases", ArrayKind.INT))
            put("mbcMaxReleases", V2Slot.GroupArray("multibandCompressor", "maxReleases", ArrayKind.INT))
            put("mbcCrests", V2Slot.GroupArray("multibandCompressor", "crests", ArrayKind.INT))
            put("mbcAdapts", V2Slot.GroupArray("multibandCompressor", "adapts", ArrayKind.INT))
            put("mbcAutoKnees", V2Slot.GroupArray("multibandCompressor", "kneeAutos", ArrayKind.BOOL01))
            put("mbcAutoGains", V2Slot.GroupArray("multibandCompressor", "gainAutos", ArrayKind.BOOL01))
            put("mbcAutoAttacks", V2Slot.GroupArray("multibandCompressor", "attackAutos", ArrayKind.BOOL01))
            put("mbcAutoReleases", V2Slot.GroupArray("multibandCompressor", "releaseAutos", ArrayKind.BOOL01))
            put("mbcNoClips", V2Slot.GroupArray("multibandCompressor", "noClips", ArrayKind.BOOL01))

            // DDC
            put("ddcEnabled", V2Slot.Group("ddc", "enable"))
            put("ddcDevice", V2Slot.Group("ddc", "device"))

            // spectrumExtension (v1 VSE)
            put("vseEnabled", V2Slot.Group("spectrumExtension", "enable"))
            put("vseStrength", V2Slot.Group("spectrumExtension", "strength"))
            put("vseExciter", V2Slot.Group("spectrumExtension", "exciter"))

            // equalizer
            put("eqEnabled", V2Slot.Group("equalizer", "enable"))
            put("eqBandCount", V2Slot.Group("equalizer", "bandCount"))
            put("eqBands", V2Slot.GroupArray("equalizer", "bands", ArrayKind.DOUBLE))
            put("eqPresetId", V2Slot.Group("equalizer", "presetId"))

            // dynamicEq
            put("dynamicEqEnabled", V2Slot.Group("dynamicEq", "enable"))
            put("dynamicEqBandCount", V2Slot.Group("dynamicEq", "bandCount"))
            put("dynamicEqFreqs", V2Slot.GroupArray("dynamicEq", "freqs", ArrayKind.INT))
            put("dynamicEqQs", V2Slot.GroupArray("dynamicEq", "qs", ArrayKind.INT))
            put("dynamicEqGains", V2Slot.GroupArray("dynamicEq", "gains", ArrayKind.INT))
            put("dynamicEqThresholds", V2Slot.GroupArray("dynamicEq", "thresholds", ArrayKind.INT))
            put("dynamicEqAttacks", V2Slot.GroupArray("dynamicEq", "attacks", ArrayKind.INT))
            put("dynamicEqReleases", V2Slot.GroupArray("dynamicEq", "releases", ArrayKind.INT))
            put("dynamicEqFilterTypes", V2Slot.GroupArray("dynamicEq", "filterTypes", ArrayKind.INT))

            // convolver (v2 dropped kernelId, uses file-based dispatch)
            put("convolverEnabled", V2Slot.Group("convolver", "enable"))
            put("convolverKernel", V2Slot.Group("convolver", "kernelFile"))
            put("convolverCrossChannel", V2Slot.Group("convolver", "crossChannel"))

            // fieldSurround
            put("fieldSurroundEnabled", V2Slot.Group("fieldSurround", "enable"))
            put("fieldSurroundWidening", V2Slot.Group("fieldSurround", "widening"))
            put("fieldSurroundMidImage", V2Slot.Group("fieldSurround", "midImage"))
            put("fieldSurroundDepth", V2Slot.Group("fieldSurround", "depth"))

            // diffSurround
            put("diffSurroundEnabled", V2Slot.Group("diffSurround", "enable"))
            put("diffSurroundDelay", V2Slot.Group("diffSurround", "delay"))
            put("diffSurroundReverse", V2Slot.Group("diffSurround", "reverse"))
            put("diffSurroundWetDryMix", V2Slot.Group("diffSurround", "wetDryMix"))
            put("diffSurroundLpCutoff", V2Slot.Group("diffSurround", "lpCutoff"))

            // stereoImager (v1 stereoImg)
            put("stereoImgEnabled", V2Slot.Group("stereoImager", "enable"))
            put("stereoImgLowWidth", V2Slot.Group("stereoImager", "lowWidth"))
            put("stereoImgMidWidth", V2Slot.Group("stereoImager", "midWidth"))
            put("stereoImgHighWidth", V2Slot.Group("stereoImager", "highWidth"))
            put("stereoImgLowCrossover", V2Slot.Group("stereoImager", "lowCrossover"))
            put("stereoImgHighCrossover", V2Slot.Group("stereoImager", "highCrossover"))

            // headphoneSurround (v1 VHE)
            put("vheEnabled", V2Slot.Group("headphoneSurround", "enable"))
            put("vheQuality", V2Slot.Group("headphoneSurround", "quality"))

            // reverb
            put("reverbEnabled", V2Slot.Group("reverb", "enable"))
            put("reverbRoomSize", V2Slot.Group("reverb", "roomSize"))
            put("reverbWidth", V2Slot.Group("reverb", "width"))
            put("reverbDampening", V2Slot.Group("reverb", "damp"))
            put("reverbWet", V2Slot.Group("reverb", "wet"))
            put("reverbDry", V2Slot.Group("reverb", "dry"))

            // dynamicSystem
            put("dynamicSystemEnabled", V2Slot.Group("dynamicSystem", "enable"))
            put("dsPresetId", V2Slot.Group("dynamicSystem", "presetId"))
            put("dynamicSystemDevice", V2Slot.Group("dynamicSystem", "device"))
            put("dynamicSystemStrength", V2Slot.Group("dynamicSystem", "strength"))
            put("dsXLow", V2Slot.Group("dynamicSystem", "xLow"))
            put("dsXHigh", V2Slot.Group("dynamicSystem", "xHigh"))
            put("dsYLow", V2Slot.Group("dynamicSystem", "yLow"))
            put("dsYHigh", V2Slot.Group("dynamicSystem", "yHigh"))
            put("dsSideGainLow", V2Slot.Group("dynamicSystem", "sideGainLow"))
            put("dsSideGainHigh", V2Slot.Group("dynamicSystem", "sideGainHigh"))

            // tubeSimulator
            put("tubeSimulatorEnabled", V2Slot.Group("tubeSimulator", "enable"))

            // psychoacousticBass
            put("psychoBassEnabled", V2Slot.Group("psychoacousticBass", "enable"))
            put("psychoBassCutoff", V2Slot.Group("psychoacousticBass", "cutoff"))
            put("psychoBassIntensity", V2Slot.Group("psychoacousticBass", "intensity"))
            put("psychoBassHarmonicOrder", V2Slot.Group("psychoacousticBass", "harmonicOrder"))
            put("psychoBassOriginalLevel", V2Slot.Group("psychoacousticBass", "originalLevel"))

            // bass
            put("bassEnabled", V2Slot.Group("bass", "enable"))
            put("bassMode", V2Slot.Group("bass", "mode"))
            put("bassFrequency", V2Slot.Group("bass", "frequency"))
            put("bassGain", V2Slot.Group("bass", "gain"))
            put("bassAntiPop", V2Slot.Group("bass", "antiPop"))

            // bassMono
            put("bassMonoEnabled", V2Slot.Group("bassMono", "enable"))
            put("bassMonoMode", V2Slot.Group("bassMono", "mode"))
            put("bassMonoFrequency", V2Slot.Group("bassMono", "frequency"))
            put("bassMonoGain", V2Slot.Group("bassMono", "gain"))
            put("bassMonoAntiPop", V2Slot.Group("bassMono", "antiPop"))

            // clarity
            put("clarityEnabled", V2Slot.Group("clarity", "enable"))
            put("clarityMode", V2Slot.Group("clarity", "mode"))
            put("clarityGain", V2Slot.Group("clarity", "gain"))

            // cure
            put("cureEnabled", V2Slot.Group("cure", "enable"))
            put("cureStrength", V2Slot.Group("cure", "crossfeedPreset"))

            // analogX
            put("analogxEnabled", V2Slot.Group("analogX", "enable"))
            put("analogxMode", V2Slot.Group("analogX", "mode"))

            // speakerCorrection
            put("speakerOptEnabled", V2Slot.Group("speakerCorrection", "enable"))
        }

    private val CANONICAL_GROUP_ORDER =
        linkedMapOf(
            "masterLimiter" to listOf("threshold", "outputVolume", "channelPan"),
            "playbackGainControl" to listOf("enable", "strength", "maxGain", "outputThreshold"),
            "lufs" to listOf("enable", "target", "maxGain", "speed"),
            "fetCompressor" to
                listOf(
                    "enable",
                    "threshold",
                    "ratio",
                    "kneeAuto",
                    "knee",
                    "kneeMulti",
                    "gainAuto",
                    "gain",
                    "attackAuto",
                    "attack",
                    "maxAttack",
                    "releaseAuto",
                    "release",
                    "maxRelease",
                    "crest",
                    "adapt",
                    "noClip",
                ),
            "multibandCompressor" to
                listOf(
                    "enable",
                    "bandEnables",
                    "crossovers",
                    "thresholds",
                    "ratios",
                    "gains",
                    "knees",
                    "kneeMultis",
                    "attacks",
                    "maxAttacks",
                    "releases",
                    "maxReleases",
                    "crests",
                    "adapts",
                    "kneeAutos",
                    "gainAutos",
                    "attackAutos",
                    "releaseAutos",
                    "noClips",
                ),
            "ddc" to listOf("enable", "device"),
            "spectrumExtension" to listOf("enable", "strength", "exciter"),
            "equalizer" to listOf("enable", "bandCount", "bands", "presetId"),
            "dynamicEq" to
                listOf(
                    "enable",
                    "bandCount",
                    "freqs",
                    "qs",
                    "gains",
                    "thresholds",
                    "attacks",
                    "releases",
                    "filterTypes",
                ),
            "convolver" to listOf("enable", "kernelFile", "crossChannel"),
            "fieldSurround" to listOf("enable", "widening", "midImage", "depth"),
            "diffSurround" to listOf("enable", "delay", "reverse", "wetDryMix", "lpCutoff"),
            "stereoImager" to
                listOf(
                    "enable",
                    "lowWidth",
                    "midWidth",
                    "highWidth",
                    "lowCrossover",
                    "highCrossover",
                ),
            "headphoneSurround" to listOf("enable", "quality"),
            "reverb" to listOf("enable", "roomSize", "width", "damp", "wet", "dry"),
            "dynamicSystem" to
                listOf(
                    "enable",
                    "presetId",
                    "device",
                    "strength",
                    "xLow",
                    "xHigh",
                    "yLow",
                    "yHigh",
                    "sideGainLow",
                    "sideGainHigh",
                ),
            "psychoacousticBass" to
                listOf(
                    "enable",
                    "cutoff",
                    "intensity",
                    "harmonicOrder",
                    "originalLevel",
                ),
            "bass" to listOf("enable", "mode", "frequency", "gain", "antiPop"),
            "bassMono" to listOf("enable", "mode", "frequency", "gain", "antiPop"),
            "clarity" to listOf("enable", "mode", "gain"),
            "cure" to listOf("enable", "crossfeedPreset"),
            "tubeSimulator" to listOf("enable"),
            "analogX" to listOf("enable", "mode"),
            "speakerCorrection" to listOf("enable"),
        )

    private val SLOT_TO_V1_KEY: Map<Pair<String, String>, String> =
        buildMap {
            for ((v1Key, slot) in V1_TO_V2) {
                val key =
                    when (slot) {
                        is V2Slot.Group -> slot.group to slot.field
                        is V2Slot.GroupArray -> slot.group to slot.field
                        is V2Slot.TopLevel -> continue
                    }
                put(key, v1Key)
            }
        }

    private val PREF_BY_JSON_KEY: Map<String, EffectPref<*>> =
        EFFECT_PREFS.associateBy { it.jsonKey }

    fun serializeEffectPrefsV2(
        presetJson: JSONObject,
        isSpk: Boolean,
        name: String? = null,
        createdAt: Long? = null,
    ): JSONObject {
        val out = JSONObject()
        out.put("schemaVersion", 2)
        if (name != null) out.put("name", name)
        if (createdAt != null) out.put("createdAt", createdAt)

        run {
            val v1Key = if (isSpk) "spkMasterEnabled" else "masterEnabled"
            val pref = PREF_BY_JSON_KEY["masterEnabled"]
            if (pref != null && presetJson.has(v1Key)) {
                out.put("masterEnable", presetJson.optBoolean(v1Key, false))
            }
        }

        for ((groupName, fields) in CANONICAL_GROUP_ORDER) {
            val groupObj = JSONObject()
            for (field in fields) {
                val v1Key = SLOT_TO_V1_KEY[groupName to field] ?: continue
                val pref = PREF_BY_JSON_KEY[v1Key] ?: continue
                val slot = V1_TO_V2[v1Key] ?: continue
                val srcKey = if (isSpk) pref.spkJsonKey else pref.jsonKey
                if (!presetJson.has(srcKey)) continue
                applySlotInto(groupObj, slot, pref, presetJson, srcKey)
            }
            if (groupObj.length() > 0) out.put(groupName, groupObj)
        }
        return out
    }

    private fun applySlotInto(
        groupObj: JSONObject,
        slot: V2Slot,
        pref: EffectPref<*>,
        src: JSONObject,
        v1Key: String,
    ) {
        when (slot) {
            is V2Slot.Group -> {
                putScalar(groupObj, slot.field, pref, src, v1Key)
            }

            is V2Slot.GroupArray -> {
                val raw = src.optString(v1Key, (pref as? StringPref)?.defaultValue ?: "")
                groupObj.put(slot.field, parseV1Array(raw, slot.kind))
            }

            is V2Slot.TopLevel -> { }
        }
    }

    private fun parseV1Array(
        raw: String,
        kind: ArrayKind,
    ): JSONArray {
        val out = JSONArray()
        if (raw.isEmpty()) return out
        for (token in raw.split(';')) {
            if (token.isEmpty()) continue
            when (kind) {
                ArrayKind.INT -> out.put(token.toIntOrNull() ?: 0)
                ArrayKind.BOOL01 -> out.put(token.toIntOrNull() == 1 || token == "true")
                ArrayKind.DOUBLE -> out.put(token.toDoubleOrNull() ?: 0.0)
            }
        }
        return out
    }

    private fun putScalar(
        target: JSONObject,
        field: String,
        pref: EffectPref<*>,
        src: JSONObject,
        v1Key: String,
    ) {
        when (pref) {
            is BoolPref -> {
                target.put(field, src.optBoolean(v1Key, pref.defaultValue))
            }

            is IntPref -> {
                target.put(field, src.optInt(v1Key, pref.defaultValue))
            }

            is StringPref -> {
                target.put(field, src.optString(v1Key, pref.defaultValue))
            }

            is NullableLongPref -> {
                val v = src.optInt(v1Key, -1)
                if (v < 0) target.put(field, JSONObject.NULL) else target.put(field, v.toLong())
            }
        }
    }
}
