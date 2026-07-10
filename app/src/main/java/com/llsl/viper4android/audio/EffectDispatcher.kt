package com.llsl.viper4android.audio

import com.llsl.viper4android.R
import com.llsl.viper4android.data.repository.ViperRepository
import com.llsl.viper4android.ui.screens.main.DynamicSystemState
import com.llsl.viper4android.ui.screens.main.MainUiState
import com.llsl.viper4android.ui.screens.main.loadEffectPrefs
import com.llsl.viper4android.utils.FileLogger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.ln
import kotlin.math.roundToInt

object EffectDispatcher {
    fun fetCompressorThresholdToRaw(dB: Int): Int = (dB / -60.0 * 100.0).roundToInt()

    fun fetCompressorKneeToRaw(dB: Int): Int = (dB / 60.0 * 100.0).roundToInt()

    fun fetCompressorGainToRaw(dB: Int): Int = (dB / 60.0 * 100.0).roundToInt()

    fun fetCompressorAttackMsToRaw(ms: Int): Int {
        val timeSec = ms / 1000.0
        val value = (ln(timeSec) + 9.21034) / 7.600903
        return (value * 100.0).roundToInt().coerceIn(0, 200)
    }

    fun fetCompressorReleaseMsToRaw(ms: Int): Int {
        val timeSec = ms / 1000.0
        val value = (ln(timeSec) + 5.298317) / 5.991465
        return (value * 100.0).roundToInt().coerceIn(0, 200)
    }

    fun spectrumExtensionExciterToRaw(value: Int): Int = (value * 5.6).toInt()

    fun fieldSurroundMidImageToRaw(value: Int): Int = value * 10 + 100

    fun fieldSurroundDepthToRaw(value: Int): Int = value * 75 + 200

    fun dynamicSystemStrengthToRaw(value: Int): Int = value * 20 + 100

    fun bassFrequencyToRaw(value: Int): Int = value + 15

    fun fieldSurroundWideningToRaw(value: Int): Int = value * 100

    fun diffSurroundDelayToRaw(ms: Int): Int = ms * 100

    data class BuiltinEqPreset(
        val key: String,
        val nameRes: Int,
        val bands10: String,
        val bands15: String,
        val bands25: String,
        val bands31: String,
    )

    val BUILTIN_EQ_PRESETS: List<BuiltinEqPreset> =
        listOf(
            BuiltinEqPreset(
                key = "eq_preset_acoustic",
                nameRes = R.string.eq_preset_acoustic,
                bands10 = "4.5;4.5;3.5;1.2;1.0;0.5;1.4;1.75;3.5;2.5;",
                bands15 = "4.5;4.5;4.5;4.0;2.5;1.0;1.0;1.0;0.5;1.0;1.5;2.0;3.0;3.0;2.5;",
                bands25 = "4.5;4.5;4.5;4.5;4.0;4.0;3.5;2.5;1.0;1.0;1.0;1.0;0.5;0.5;1.0;1.0;1.5;1.5;2.0;2.5;3.5;3.0;3.0;2.5;2.5;",
                bands31 = "4.5;4.5;4.5;4.5;4.5;4.5;4.0;4.0;3.5;2.5;2.0;1.0;1.0;1.0;1.0;1.0;0.5;0.5;1.0;1.0;1.5;1.5;1.5;2.0;2.5;3.0;3.5;3.0;3.0;2.5;2.5;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_bass_booster",
                nameRes = R.string.eq_preset_bass_booster,
                bands10 = "6.0;4.0;2.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
                bands15 = "6.0;5.5;4.0;2.5;1.5;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
                bands25 = "6.0;6.0;5.5;4.5;3.5;2.5;2.0;1.5;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
                bands31 = "6.0;6.0;6.0;5.5;4.5;4.0;3.5;2.5;2.0;1.5;0.5;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_bass_reducer",
                nameRes = R.string.eq_preset_bass_reducer,
                bands10 = "-6.0;-4.0;-2.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
                bands15 = "-6.0;-5.5;-4.0;-2.5;-1.5;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
                bands25 = "-6.0;-6.0;-5.5;-4.5;-3.5;-2.5;-2.0;-1.5;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
                bands31 = "-6.0;-6.0;-6.0;-5.5;-4.5;-4.0;-3.5;-2.5;-2.0;-1.5;-0.5;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_classical",
                nameRes = R.string.eq_preset_classical,
                bands10 = "0.0;0.0;0.0;0.0;0.0;0.0;-3.0;-3.0;-3.0;-5.0;",
                bands15 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;-2.0;-3.0;-3.0;-3.0;-3.5;-5.0;",
                bands25 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;-1.0;-2.0;-3.0;-3.0;-3.0;-3.0;-3.0;-3.5;-4.5;-5.0;-5.0;",
                bands31 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;-1.0;-2.0;-3.0;-3.0;-3.0;-3.0;-3.0;-3.0;-3.0;-3.5;-4.5;-5.0;-5.0;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_deep",
                nameRes = R.string.eq_preset_deep,
                bands10 = "3.0;2.0;1.0;0.5;0.5;0.0;-1.0;-2.0;-3.0;-3.5;",
                bands15 = "3.0;2.5;2.0;1.5;1.0;0.5;0.5;0.5;0.0;-0.5;-1.5;-2.0;-2.5;-3.0;-3.5;",
                bands25 = "3.0;3.0;2.5;2.5;1.5;1.5;1.0;1.0;0.5;0.5;0.5;0.5;0.0;0.0;-0.5;-0.5;-1.5;-1.5;-2.0;-2.5;-3.0;-3.0;-3.5;-3.5;-3.5;",
                bands31 = "3.0;3.0;3.0;2.5;2.5;2.0;1.5;1.5;1.0;1.0;0.5;0.5;0.5;0.5;0.5;0.5;0.0;0.0;-0.5;-0.5;-1.0;-1.5;-1.5;-2.0;-2.5;-2.5;-3.0;-3.0;-3.5;-3.5;-3.5;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_flat",
                nameRes = R.string.eq_preset_flat,
                bands10 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
                bands15 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
                bands25 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
                bands31 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_rnb",
                nameRes = R.string.eq_preset_rnb,
                bands10 = "3.0;6.0;4.0;1.0;-1.0;-0.5;1.0;1.5;2.5;3.0;",
                bands15 = "3.0;4.0;6.0;4.5;3.0;1.0;-0.5;-1.0;-0.5;0.5;1.0;1.5;2.0;2.5;3.0;",
                bands25 = "3.0;3.0;4.0;5.0;5.5;4.5;4.0;3.0;1.0;0.5;-0.5;-1.0;-0.5;-0.5;0.0;0.5;1.0;1.5;1.5;2.0;2.5;2.5;3.0;3.0;3.0;",
                bands31 = "3.0;3.0;3.0;4.0;5.0;6.0;5.5;4.5;4.0;3.0;2.0;1.0;0.5;-0.5;-1.0;-1.0;-0.5;-0.5;0.0;0.5;1.0;1.0;1.5;1.5;2.0;2.0;2.5;2.5;3.0;3.0;3.0;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_rock",
                nameRes = R.string.eq_preset_rock,
                bands10 = "4.0;3.0;1.0;0.0;-0.5;0.0;1.5;2.5;3.5;4.0;",
                bands15 = "4.0;3.5;3.0;1.5;0.5;0.0;-0.5;-0.5;0.0;1.0;2.0;2.5;3.0;3.5;4.0;",
                bands25 = "4.0;4.0;3.5;3.5;2.5;1.5;1.0;0.5;0.0;0.0;-0.5;-0.5;0.0;0.0;0.5;1.0;2.0;2.0;2.5;3.0;3.5;3.5;4.0;4.0;4.0;",
                bands31 = "4.0;4.0;4.0;3.5;3.5;3.0;2.5;1.5;1.0;0.5;0.5;0.0;0.0;-0.5;-0.5;-0.5;0.0;0.0;0.5;1.0;1.5;2.0;2.0;2.5;3.0;3.0;3.5;3.5;4.0;4.0;4.0;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_small_speakers",
                nameRes = R.string.eq_preset_small_speakers,
                bands10 = "3.0;2.0;1.5;1.0;0.5;-0.5;-1.5;-2.0;-3.0;-3.5;",
                bands15 = "3.0;2.5;2.0;1.5;1.5;1.0;0.5;0.0;-0.5;-1.0;-1.5;-2.0;-2.5;-3.0;-3.5;",
                bands25 = "3.0;3.0;2.5;2.5;2.0;1.5;1.5;1.5;1.0;1.0;0.5;0.5;0.0;-0.5;-1.0;-1.0;-1.5;-2.0;-2.0;-2.5;-3.0;-3.0;-3.5;-3.5;-3.5;",
                bands31 = "3.0;3.0;3.0;2.5;2.5;2.0;2.0;1.5;1.5;1.5;1.0;1.0;1.0;0.5;0.5;0.0;0.0;-0.5;-1.0;-1.0;-1.5;-1.5;-2.0;-2.0;-2.5;-2.5;-3.0;-3.0;-3.5;-3.5;-3.5;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_treble_booster",
                nameRes = R.string.eq_preset_treble_booster,
                bands10 = "0.0;0.0;0.0;0.0;0.0;1.0;2.0;3.0;4.0;5.0;",
                bands15 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.5;1.0;1.5;2.5;3.0;3.5;4.5;5.0;",
                bands25 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.5;1.0;1.5;1.5;2.5;2.5;3.0;3.5;4.0;4.5;4.5;5.0;5.0;",
                bands31 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.5;0.5;1.0;1.5;1.5;2.0;2.5;2.5;3.0;3.5;3.5;4.0;4.5;4.5;5.0;5.0;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_treble_reducer",
                nameRes = R.string.eq_preset_treble_reducer,
                bands10 = "0.0;0.0;0.0;0.0;0.0;-1.0;-2.0;-3.0;-4.0;-5.0;",
                bands15 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;-0.5;-1.0;-1.5;-2.5;-3.0;-3.5;-4.5;-5.0;",
                bands25 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;-0.5;-1.0;-1.5;-1.5;-2.5;-2.5;-3.0;-3.5;-4.0;-4.5;-4.5;-5.0;-5.0;",
                bands31 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;-0.5;-0.5;-1.0;-1.5;-1.5;-2.0;-2.5;-2.5;-3.0;-3.5;-3.5;-4.0;-4.5;-4.5;-5.0;-5.0;",
            ),
            BuiltinEqPreset(
                key = "eq_preset_vocal_booster",
                nameRes = R.string.eq_preset_vocal_booster,
                bands10 = "-1.0;-0.5;0.0;1.5;3.0;3.0;2.0;1.0;0.0;-1.0;",
                bands15 = "-1.0;-1.0;-0.5;0.0;0.5;1.5;2.5;3.0;3.0;2.5;1.5;1.0;0.5;-0.5;-1.0;",
                bands25 = "-1.0;-1.0;-1.0;-0.5;-0.5;0.0;0.0;0.5;1.5;2.0;2.5;3.0;3.0;3.0;2.5;2.5;1.5;1.5;1.0;0.5;0.0;-0.5;-0.5;-1.0;-1.0;",
                bands31 = "-1.0;-1.0;-1.0;-1.0;-0.5;-0.5;-0.5;0.0;0.0;0.5;1.0;1.5;2.0;2.5;3.0;3.0;3.0;3.0;2.5;2.5;2.0;1.5;1.5;1.0;0.5;0.5;0.0;-0.5;-0.5;-1.0;-1.0;",
            ),
        )

    val EQ_PRESET_NAME_RES: Map<String, Int> =
        BUILTIN_EQ_PRESETS.associate { it.key to it.nameRes }

    data class BuiltinDsPreset(
        val key: String,
        val nameRes: Int,
        val xLow: Int,
        val xHigh: Int,
        val yLow: Int,
        val yHigh: Int,
        val sideGainLow: Int,
        val sideGainHigh: Int,
    )

    val BUILTIN_DS_PRESETS: List<BuiltinDsPreset> =
        listOf(
            BuiltinDsPreset(
                key = "ds_device_extreme_headphone_v2",
                nameRes = R.string.ds_device_extreme_headphone_v2,
                xLow = 140,
                xHigh = 6200,
                yLow = 40,
                yHigh = 60,
                sideGainLow = 10,
                sideGainHigh = 80,
            ),
            BuiltinDsPreset(
                key = "ds_device_high_end_headphone_v2",
                nameRes = R.string.ds_device_high_end_headphone_v2,
                xLow = 180,
                xHigh = 5800,
                yLow = 55,
                yHigh = 80,
                sideGainLow = 10,
                sideGainHigh = 70,
            ),
            BuiltinDsPreset(
                key = "ds_device_common_headphone_v2",
                nameRes = R.string.ds_device_common_headphone_v2,
                xLow = 300,
                xHigh = 5600,
                yLow = 60,
                yHigh = 105,
                sideGainLow = 10,
                sideGainHigh = 50,
            ),
            BuiltinDsPreset(
                key = "ds_device_low_end_headphone_v2",
                nameRes = R.string.ds_device_low_end_headphone_v2,
                xLow = 600,
                xHigh = 5400,
                yLow = 60,
                yHigh = 105,
                sideGainLow = 10,
                sideGainHigh = 20,
            ),
            BuiltinDsPreset(
                key = "ds_device_common_earphone_v2",
                nameRes = R.string.ds_device_common_earphone_v2,
                xLow = 100,
                xHigh = 5600,
                yLow = 40,
                yHigh = 80,
                sideGainLow = 50,
                sideGainHigh = 50,
            ),
            BuiltinDsPreset(
                key = "ds_device_extreme_headphone_v1",
                nameRes = R.string.ds_device_extreme_headphone_v1,
                xLow = 1200,
                xHigh = 6200,
                yLow = 40,
                yHigh = 80,
                sideGainLow = 0,
                sideGainHigh = 20,
            ),
            BuiltinDsPreset(
                key = "ds_device_high_end_headphone_v1",
                nameRes = R.string.ds_device_high_end_headphone_v1,
                xLow = 1000,
                xHigh = 6200,
                yLow = 40,
                yHigh = 80,
                sideGainLow = 0,
                sideGainHigh = 10,
            ),
            BuiltinDsPreset(
                key = "ds_device_common_headphone_v1",
                nameRes = R.string.ds_device_common_headphone_v1,
                xLow = 800,
                xHigh = 6200,
                yLow = 40,
                yHigh = 80,
                sideGainLow = 10,
                sideGainHigh = 0,
            ),
            BuiltinDsPreset(
                key = "ds_device_common_earphone_v1",
                nameRes = R.string.ds_device_common_earphone_v1,
                xLow = 400,
                xHigh = 6200,
                yLow = 40,
                yHigh = 80,
                sideGainLow = 10,
                sideGainHigh = 0,
            ),
        )

    val DS_PRESET_NAME_RES: Map<String, Int> =
        BUILTIN_DS_PRESETS.associate { it.key to it.nameRes }

    val EQ_BAND_LABELS_10 =
        listOf(
            "31Hz",
            "62Hz",
            "125Hz",
            "250Hz",
            "500Hz",
            "1kHz",
            "2kHz",
            "4kHz",
            "8kHz",
            "16kHz",
        )
    val EQ_BAND_LABELS_15 =
        listOf(
            "25Hz",
            "40Hz",
            "63Hz",
            "100Hz",
            "160Hz",
            "250Hz",
            "400Hz",
            "630Hz",
            "1kHz",
            "1.6kHz",
            "2.5kHz",
            "4kHz",
            "6.3kHz",
            "10kHz",
            "16kHz",
        )
    val EQ_BAND_LABELS_25 =
        listOf(
            "20Hz",
            "31Hz",
            "40Hz",
            "50Hz",
            "80Hz",
            "100Hz",
            "125Hz",
            "160Hz",
            "250Hz",
            "315Hz",
            "400Hz",
            "500Hz",
            "800Hz",
            "1kHz",
            "1.25kHz",
            "1.6kHz",
            "2.5kHz",
            "3.15kHz",
            "4kHz",
            "5kHz",
            "8kHz",
            "10kHz",
            "12.5kHz",
            "16kHz",
            "20kHz",
        )
    val EQ_BAND_LABELS_31 =
        listOf(
            "20Hz",
            "25Hz",
            "31Hz",
            "40Hz",
            "50Hz",
            "63Hz",
            "80Hz",
            "100Hz",
            "125Hz",
            "160Hz",
            "200Hz",
            "250Hz",
            "315Hz",
            "400Hz",
            "500Hz",
            "630Hz",
            "800Hz",
            "1kHz",
            "1.25kHz",
            "1.6kHz",
            "2kHz",
            "2.5kHz",
            "3.15kHz",
            "4kHz",
            "5kHz",
            "6.3kHz",
            "8kHz",
            "10kHz",
            "12.5kHz",
            "16kHz",
            "20kHz",
        )

    fun eqBandLabelsForCount(count: Int): List<String> =
        when (count) {
            15 -> EQ_BAND_LABELS_15
            25 -> EQ_BAND_LABELS_25
            31 -> EQ_BAND_LABELS_31
            else -> EQ_BAND_LABELS_10
        }

    private fun ensureBandCount(
        rawBands: List<Double>,
        expectedCount: Int,
    ): List<Double> =
        if (rawBands.size != expectedCount) {
            List(expectedCount) { 0.0 }
        } else {
            rawBands
        }

    val EQ_GRAPH_LABELS_10 =
        listOf(
            "31",
            "62",
            "125",
            "250",
            "500",
            "1k",
            "2k",
            "4k",
            "8k",
            "16k",
        )
    val EQ_GRAPH_LABELS_15 =
        listOf(
            "25",
            "40",
            "63",
            "100",
            "160",
            "250",
            "400",
            "630",
            "1k",
            "1.6k",
            "2.5k",
            "4k",
            "6.3k",
            "10k",
            "16k",
        )
    val EQ_GRAPH_LABELS_25 =
        listOf(
            "20",
            "31",
            "40",
            "50",
            "80",
            "100",
            "125",
            "160",
            "250",
            "315",
            "400",
            "500",
            "800",
            "1k",
            "1.25k",
            "1.6k",
            "2.5k",
            "3.15k",
            "4k",
            "5k",
            "8k",
            "10k",
            "12.5k",
            "16k",
            "20k",
        )
    val EQ_GRAPH_LABELS_31 =
        listOf(
            "20",
            "25",
            "31",
            "40",
            "50",
            "63",
            "80",
            "100",
            "125",
            "160",
            "200",
            "250",
            "315",
            "400",
            "500",
            "630",
            "800",
            "1k",
            "1.25k",
            "1.6k",
            "2k",
            "2.5k",
            "3.15k",
            "4k",
            "5k",
            "6.3k",
            "8k",
            "10k",
            "12.5k",
            "16k",
            "20k",
        )

    fun eqGraphLabelsForCount(count: Int): List<String> =
        when (count) {
            15 -> EQ_GRAPH_LABELS_15
            25 -> EQ_GRAPH_LABELS_25
            31 -> EQ_GRAPH_LABELS_31
            else -> EQ_GRAPH_LABELS_10
        }

    fun dispatchFullState(
        effect: ViperEffect,
        state: MainUiState,
        masterEnabled: Boolean,
    ) {
        FileLogger.d(
            "Dispatch",
            "Dispatch: fullState master=${if (masterEnabled) "ON" else "OFF"}",
        )
        dispatchState(effect, state)
    }

    fun dispatchState(
        effect: ViperEffect,
        state: MainUiState,
    ) {
        // Output
        effect.setParameter(ViperParams.kParamMasterLimiterOutputVolume, state.out.volume)
        effect.setParameter(ViperParams.kParamMasterLimiterChannelPan, state.out.channelPan)
        effect.setParameter(ViperParams.kParamMasterLimiterThreshold, state.out.limiter)

        // AGC
        effect.setParameter(ViperParams.kParamPlaybackGainControlEnable, if (state.playbackGainControl.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamPlaybackGainControlStrength, state.playbackGainControl.strength)
        effect.setParameter(ViperParams.kParamPlaybackGainControlMaxGain, state.playbackGainControl.maxGain)
        effect.setParameter(ViperParams.kParamPlaybackGainControlOutputThreshold, state.playbackGainControl.outputThreshold)

        // LUFS
        effect.setParameter(ViperParams.kParamLufsEnable, if (state.lufs.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamLufsTarget, state.lufs.target)
        effect.setParameter(ViperParams.kParamLufsMaxGain, state.lufs.maxGain)
        effect.setParameter(ViperParams.kParamLufsSpeed, state.lufs.speed)

        // FET Compressor
        effect.setParameter(ViperParams.kParamFetCompressorEnable, if (state.fetCompressor.enable) 100 else 0)
        effect.setParameter(ViperParams.kParamFetCompressorThreshold, fetCompressorThresholdToRaw(state.fetCompressor.threshold))
        effect.setParameter(ViperParams.kParamFetCompressorRatio, state.fetCompressor.ratio)
        effect.setParameter(ViperParams.kParamFetCompressorKneeAuto, if (state.fetCompressor.kneeAuto) 100 else 0)
        effect.setParameter(ViperParams.kParamFetCompressorKnee, fetCompressorKneeToRaw(state.fetCompressor.knee))
        effect.setParameter(ViperParams.kParamFetCompressorKneeMulti, state.fetCompressor.kneeMulti)
        effect.setParameter(ViperParams.kParamFetCompressorGainAuto, if (state.fetCompressor.gainAuto) 100 else 0)
        effect.setParameter(ViperParams.kParamFetCompressorGain, fetCompressorGainToRaw(state.fetCompressor.gain))
        effect.setParameter(ViperParams.kParamFetCompressorAttackAuto, if (state.fetCompressor.attackAuto) 100 else 0)
        effect.setParameter(ViperParams.kParamFetCompressorAttack, fetCompressorAttackMsToRaw(state.fetCompressor.attack))
        effect.setParameter(ViperParams.kParamFetCompressorMaxAttack, fetCompressorAttackMsToRaw(state.fetCompressor.maxAttack))
        effect.setParameter(ViperParams.kParamFetCompressorReleaseAuto, if (state.fetCompressor.releaseAuto) 100 else 0)
        effect.setParameter(ViperParams.kParamFetCompressorRelease, fetCompressorReleaseMsToRaw(state.fetCompressor.release))
        effect.setParameter(ViperParams.kParamFetCompressorMaxRelease, fetCompressorReleaseMsToRaw(state.fetCompressor.maxRelease))
        effect.setParameter(ViperParams.kParamFetCompressorCrest, fetCompressorReleaseMsToRaw(state.fetCompressor.crest))
        effect.setParameter(ViperParams.kParamFetCompressorAdapt, state.fetCompressor.adapt)
        effect.setParameter(ViperParams.kParamFetCompressorNoClip, if (state.fetCompressor.noClip) 100 else 0)

        // Multiband Compressor
        effect.setParameter(ViperParams.kParamMultibandCompressorEnable, if (state.multibandCompressor.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamMultibandCompressorBandCount, 5)
        val mbc = state.multibandCompressor
        val mbcCrossoverDefaults = intArrayOf(120, 500, 4000, 8000)
        for (i in mbcCrossoverDefaults.indices) {
            effect.setParameter(
                ViperParams.kParamMultibandCompressorCrossoverFrequency,
                i,
                mbc.crossovers.getOrElse(i) { mbcCrossoverDefaults[i] },
            )
        }
        for (b in 0 until 5) {
            val bandEnabled = mbc.bandEnables.getOrElse(b) { true }
            effect.setParameter(ViperParams.kParamMultibandCompressorBandEnable, b, if (bandEnabled) 100 else 0)
            effect.setParameter(
                ViperParams.kParamMultibandCompressorBandThreshold,
                b,
                fetCompressorThresholdToRaw(mbc.thresholds.getOrElse(b) { -18 }),
            )
            effect.setParameter(ViperParams.kParamMultibandCompressorBandRatio, b, mbc.ratios.getOrElse(b) { 50 })
            effect.setParameter(ViperParams.kParamMultibandCompressorBandGain, b, fetCompressorGainToRaw(mbc.gains.getOrElse(b) { 0 }))
            effect.setParameter(
                ViperParams.kParamMultibandCompressorBandAttack,
                b,
                fetCompressorAttackMsToRaw(mbc.attacks.getOrElse(b) { 1 }),
            )
            effect.setParameter(
                ViperParams.kParamMultibandCompressorBandRelease,
                b,
                fetCompressorReleaseMsToRaw(mbc.releases.getOrElse(b) { 100 }),
            )
            effect.setParameter(ViperParams.kParamMultibandCompressorBandKnee, b, fetCompressorKneeToRaw(mbc.knees.getOrElse(b) { 0 }))
            effect.setParameter(ViperParams.kParamMultibandCompressorBandGainAuto, b, if (mbc.gainAutos.getOrElse(b) { true }) 100 else 0)
            effect.setParameter(
                ViperParams.kParamMultibandCompressorBandAttackAuto,
                b,
                if (mbc.attackAutos.getOrElse(b) { true }) 100 else 0,
            )
            effect.setParameter(
                ViperParams.kParamMultibandCompressorBandReleaseAuto,
                b,
                if (mbc.releaseAutos.getOrElse(b) { true }) 100 else 0,
            )
            effect.setParameter(ViperParams.kParamMultibandCompressorBandKneeAuto, b, if (mbc.kneeAutos.getOrElse(b) { true }) 100 else 0)
            effect.setParameter(ViperParams.kParamMultibandCompressorBandKneeMulti, b, mbc.kneeMultis.getOrElse(b) { 0 })
            effect.setParameter(
                ViperParams.kParamMultibandCompressorBandMaxAttack,
                b,
                fetCompressorAttackMsToRaw(mbc.maxAttacks.getOrElse(b) { 44 }),
            )
            effect
                .setParameter(
                    ViperParams.kParamMultibandCompressorBandMaxRelease,
                    b,
                    fetCompressorReleaseMsToRaw(mbc.maxReleases.getOrElse(b) { 200 }),
                )
            effect.setParameter(
                ViperParams.kParamMultibandCompressorBandCrest,
                b,
                fetCompressorReleaseMsToRaw(mbc.crests.getOrElse(b) { 100 }),
            )
            effect.setParameter(ViperParams.kParamMultibandCompressorBandAdapt, b, mbc.adapts.getOrElse(b) { 50 })
            effect.setParameter(ViperParams.kParamMultibandCompressorBandNoClip, b, if (mbc.noClips.getOrElse(b) { true }) 100 else 0)
        }

        // DDC
        effect.setParameter(ViperParams.kParamDdcEnable, if (state.ddc.enable) 1 else 0)

        // Spectrum Extension
        effect.setParameter(ViperParams.kParamSpectrumExtensionEnable, if (state.spectrumExtension.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamSpectrumExtensionStrength, state.spectrumExtension.strength)
        effect.setParameter(ViperParams.kParamSpectrumExtensionExciter, spectrumExtensionExciterToRaw(state.spectrumExtension.exciter))

        // EQ
        effect.setParameter(ViperParams.kParamEqualizerBandCount, state.eq.bandCount)
        effect.setParameter(ViperParams.kParamEqualizerEnable, if (state.eq.enable) 1 else 0)
        dispatchEqBands(effect, state.eq.bands)

        // Dynamic EQ
        effect.setParameter(ViperParams.kParamDynamicEqEnable, if (state.dynamicEq.enable) 1 else 0)
        val deq = state.dynamicEq
        for (b in 0 until deq.bandCount) {
            effect.setParameter(ViperParams.kParamDynamicEqBandFrequency, b, deq.freqs.getOrElse(b) { 1000 })
            effect.setParameter(ViperParams.kParamDynamicEqBandQ, b, deq.qs.getOrElse(b) { 150 })
            effect.setParameter(ViperParams.kParamDynamicEqBandGain, b, deq.gains.getOrElse(b) { 0 })
            effect.setParameter(ViperParams.kParamDynamicEqBandThreshold, b, deq.thresholds.getOrElse(b) { -300 })
            effect.setParameter(ViperParams.kParamDynamicEqBandAttack, b, deq.attacks.getOrElse(b) { 10 })
            effect.setParameter(ViperParams.kParamDynamicEqBandRelease, b, deq.releases.getOrElse(b) { 100 })
            effect.setParameter(ViperParams.kParamDynamicEqBandFilterType, b, deq.filterTypes.getOrElse(b) { 0 })
        }
        effect.setParameter(ViperParams.kParamDynamicEqBandCount, state.dynamicEq.bandCount)

        // Convolver
        effect.setParameter(ViperParams.kParamConvolverEnable, if (state.convolver.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamConvolverCrossChannel, state.convolver.crossChannel)

        // Field Surround
        effect.setParameter(ViperParams.kParamFieldSurroundEnable, if (state.fieldSurround.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamFieldSurroundWidening, fieldSurroundWideningToRaw(state.fieldSurround.widening))
        effect.setParameter(ViperParams.kParamFieldSurroundMidImage, fieldSurroundMidImageToRaw(state.fieldSurround.midImage))
        effect.setParameter(ViperParams.kParamFieldSurroundDepth, fieldSurroundDepthToRaw(state.fieldSurround.depth))

        // Diff Surround
        effect.setParameter(ViperParams.kParamDiffSurroundEnable, if (state.diffSurround.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamDiffSurroundDelay, diffSurroundDelayToRaw(state.diffSurround.delay))
        effect.setParameter(ViperParams.kParamDiffSurroundReverse, if (state.diffSurround.reverse) 1 else 0)
        effect.setParameter(ViperParams.kParamDiffSurroundWetDryMix, state.diffSurround.wetDryMix)
        effect.setParameter(ViperParams.kParamDiffSurroundLpCutoff, state.diffSurround.lpCutoff)

        // Stereo Imager
        effect.setParameter(ViperParams.kParamStereoImagerEnable, if (state.stereoImager.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamStereoImagerLowWidth, state.stereoImager.lowWidth)
        effect.setParameter(ViperParams.kParamStereoImagerMidWidth, state.stereoImager.midWidth)
        effect.setParameter(ViperParams.kParamStereoImagerHighWidth, state.stereoImager.highWidth)
        effect.setParameter(ViperParams.kParamStereoImagerLowCrossover, state.stereoImager.lowCrossover)
        effect.setParameter(ViperParams.kParamStereoImagerHighCrossover, state.stereoImager.highCrossover)

        // Headphone Surround
        effect.setParameter(ViperParams.kParamHeadphoneSurroundEnable, if (state.headphoneSurround.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamHeadphoneSurroundQuality, state.headphoneSurround.quality)

        // Reverb
        effect.setParameter(ViperParams.kParamReverbEnable, if (state.reverb.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamReverbRoomSize, state.reverb.roomSize * 10)
        effect.setParameter(ViperParams.kParamReverbWidth, state.reverb.width * 10)
        effect.setParameter(ViperParams.kParamReverbDamp, state.reverb.damp * 10)
        effect.setParameter(ViperParams.kParamReverbWet, state.reverb.wet)
        effect.setParameter(ViperParams.kParamReverbDry, state.reverb.dry)

        // Dynamic System
        dispatchDynamicSystem(effect, state.dynamicSystem)

        // Tube Simulator
        effect.setParameter(ViperParams.kParamTubeSimulatorEnable, if (state.tubeSimulator.enable) 1 else 0)

        // Psycho Bass
        effect.setParameter(ViperParams.kParamPsychoacousticBassEnable, if (state.psychoacousticBass.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamPsychoacousticBassCutoff, state.psychoacousticBass.cutoff)
        effect.setParameter(ViperParams.kParamPsychoacousticBassIntensity, state.psychoacousticBass.intensity)
        effect.setParameter(ViperParams.kParamPsychoacousticBassHarmonicOrder, state.psychoacousticBass.harmonicOrder)
        effect.setParameter(ViperParams.kParamPsychoacousticBassOriginalLevel, state.psychoacousticBass.originalLevel)

        // Bass
        effect.setParameter(ViperParams.kParamBassEnable, if (state.bass.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamBassMode, state.bass.mode)
        effect.setParameter(ViperParams.kParamBassFrequency, bassFrequencyToRaw(state.bass.frequency))
        effect.setParameter(ViperParams.kParamBassGain, state.bass.gain)
        effect.setParameter(ViperParams.kParamBassAntiPop, if (state.bass.antiPop) 1 else 0)

        // Bass Mono
        effect.setParameter(ViperParams.kParamBassMonoEnable, if (state.bassMono.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamBassMonoMode, state.bassMono.mode)
        effect.setParameter(ViperParams.kParamBassMonoFrequency, bassFrequencyToRaw(state.bassMono.frequency))
        effect.setParameter(ViperParams.kParamBassMonoGain, state.bassMono.gain)
        effect.setParameter(ViperParams.kParamBassMonoAntiPop, if (state.bassMono.antiPop) 1 else 0)

        // Clarity
        effect.setParameter(ViperParams.kParamClarityEnable, if (state.clarity.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamClarityMode, state.clarity.mode)
        effect.setParameter(ViperParams.kParamClarityGain, state.clarity.gain)

        // Cure
        effect.setParameter(ViperParams.kParamCureEnable, if (state.cure.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamCureCrossfeedPreset, state.cure.crossfeedPreset)

        // AnalogX
        effect.setParameter(ViperParams.kParamAnalogXEnable, if (state.analogX.enable) 1 else 0)
        effect.setParameter(ViperParams.kParamAnalogXMode, state.analogX.mode)

        // Speaker Correction
        effect.setParameter(ViperParams.kParamSpeakerCorrectionEnable, if (state.speakerCorrection.enable) 1 else 0)
    }

    fun dispatchEqBands(
        effect: ViperEffect,
        bands: List<Double>,
    ) {
        for ((index, bandDb) in bands.withIndex()) {
            val level = (bandDb * 100).toInt()
            effect.setParameter(ViperParams.kParamEqualizerBandLevel, index, level)
        }
    }

    private fun dispatchDynamicSystem(
        effect: ViperEffect,
        state: DynamicSystemState,
    ) {
        effect.setParameter(ViperParams.kParamDynamicSystemEnable, if (state.enable) 1 else 0)
        FileLogger.d(
            "Dispatch",
            "DynamicSystem: ${if (state.enable) "ON" else "OFF"} strength=${state.strength} " +
                "x=[${state.xLow},${state.xHigh}] y=[${state.yLow},${state.yHigh}] " +
                "side=[${state.sideGainLow},${state.sideGainHigh}]",
        )
        effect.setParameter(ViperParams.kParamDynamicSystemStrength, dynamicSystemStrengthToRaw(state.strength))
        effect.setParameter(ViperParams.kParamDynamicSystemXCoefficients, state.xLow, state.xHigh)
        effect.setParameter(ViperParams.kParamDynamicSystemYCoefficients, state.yLow, state.yHigh)
        effect.setParameter(ViperParams.kParamDynamicSystemSideGain, state.sideGainLow, state.sideGainHigh)
    }

    suspend fun loadFullStateFromPrefs(repository: ViperRepository): MainUiState {
        val s = loadEffectPrefs(repository)
        val eqBands = ensureBandCount(s.eq.bands, s.eq.bandCount)
        return s.copy(eq = s.eq.copy(bands = eqBands))
    }

    fun dispatchDdcCoefficients(
        effect: ViperEffect,
        sec44100: List<FloatArray>,
        sec48000: List<FloatArray>,
    ) {
        if (sec44100.size != sec48000.size) {
            FileLogger.w(
                "Dispatch",
                "dispatchDdcCoefficients: section count mismatch (44.1k=${sec44100.size} vs 48k=${sec48000.size})",
            )
            return
        }
        if (sec44100.any { it.size != 5 } || sec48000.any { it.size != 5 }) {
            FileLogger.w("Dispatch", "dispatchDdcCoefficients: section size != 5")
            return
        }
        val sectionCount = sec44100.size
        val floatsPerRate = sectionCount * 5
        val naturalSize = 4 + floatsPerRate * 4 * 2
        val wireSize =
            when {
                naturalSize <= 256 -> {
                    256
                }

                naturalSize <= 1024 -> {
                    1024
                }

                else -> {
                    FileLogger.w(
                        "Dispatch",
                        "dispatchDdcCoefficients: blob too large ($naturalSize bytes; max 1024)",
                    )
                    return
                }
            }
        val bytes = ByteArray(wireSize)
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        bb.putInt(floatsPerRate)
        for (s in sec44100) for (v in s) bb.putFloat(v)
        for (s in sec48000) for (v in s) bb.putFloat(v)
        effect.setParameter(ViperParams.kParamDdcCoefficients, bytes)
    }
}
