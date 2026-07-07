package com.llsl.viper4android.audio

object ViperParams {
    const val PARAM_GET_ENABLED = 1
    const val PARAM_GET_CONFIGURE = 2
    const val PARAM_GET_STREAMING = 3
    const val PARAM_GET_SAMPLING_RATE = 4
    const val PARAM_GET_CONVOLUTION_KERNEL_ID = 5
    const val PARAM_GET_DRIVER_VERSION_CODE = 6
    const val PARAM_GET_DRIVER_VERSION_NAME = 7
    const val PARAM_GET_ARCHITECTURE = 8

    const val kParamResetAllEffects = 0x10101

    const val kParamMasterLimiterThreshold = 0x10110
    const val kParamMasterLimiterOutputVolume = 0x10111
    const val kParamMasterLimiterChannelPan = 0x10112

    const val kParamPlaybackGainControlEnable = 0x10120
    const val kParamPlaybackGainControlStrength = 0x10121
    const val kParamPlaybackGainControlMaxGain = 0x10122
    const val kParamPlaybackGainControlOutputThreshold = 0x10123

    const val kParamLufsEnable = 0x10130
    const val kParamLufsTarget = 0x10131
    const val kParamLufsMaxGain = 0x10132
    const val kParamLufsSpeed = 0x10133

    const val kParamFetCompressorEnable = 0x10140
    const val kParamFetCompressorThreshold = 0x10141
    const val kParamFetCompressorRatio = 0x10142
    const val kParamFetCompressorKnee = 0x10143
    const val kParamFetCompressorKneeAuto = 0x10144
    const val kParamFetCompressorGain = 0x10145
    const val kParamFetCompressorGainAuto = 0x10146
    const val kParamFetCompressorAttack = 0x10147
    const val kParamFetCompressorAttackAuto = 0x10148
    const val kParamFetCompressorRelease = 0x10149
    const val kParamFetCompressorReleaseAuto = 0x1014A
    const val kParamFetCompressorKneeMulti = 0x1014B
    const val kParamFetCompressorMaxAttack = 0x1014C
    const val kParamFetCompressorMaxRelease = 0x1014D
    const val kParamFetCompressorCrest = 0x1014E
    const val kParamFetCompressorAdapt = 0x1014F
    const val kParamFetCompressorNoClip = 0x10150

    const val kParamBassEnable = 0x10160
    const val kParamBassMode = 0x10161
    const val kParamBassFrequency = 0x10162
    const val kParamBassGain = 0x10163
    const val kParamBassAntiPop = 0x10164

    const val kParamBassMonoEnable = 0x10170
    const val kParamBassMonoMode = 0x10171
    const val kParamBassMonoFrequency = 0x10172
    const val kParamBassMonoGain = 0x10173
    const val kParamBassMonoAntiPop = 0x10174

    const val kParamPsychoacousticBassEnable = 0x10180
    const val kParamPsychoacousticBassCutoff = 0x10181
    const val kParamPsychoacousticBassIntensity = 0x10182
    const val kParamPsychoacousticBassHarmonicOrder = 0x10183
    const val kParamPsychoacousticBassOriginalLevel = 0x10184

    const val kParamSpectrumExtensionEnable = 0x10190
    const val kParamSpectrumExtensionStrength = 0x10191
    const val kParamSpectrumExtensionExciter = 0x10192

    const val kParamEqualizerEnable = 0x101A0
    const val kParamEqualizerBandLevel = 0x101A1
    const val kParamEqualizerBandCount = 0x101A2
    const val kParamConvolverEnable = 0x101B0
    const val kParamConvolverSetKernel = 0x101B1
    const val kParamConvolverPrepareBuffer = 0x101B2
    const val kParamConvolverSetBuffer = 0x101B3
    const val kParamConvolverCommitBuffer = 0x101B4
    const val kParamConvolverCrossChannel = 0x101B5

    const val kParamDdcEnable = 0x101C0
    const val kParamDdcCoefficients = 0x101C1

    const val kParamFieldSurroundEnable = 0x101D0
    const val kParamFieldSurroundWidening = 0x101D1
    const val kParamFieldSurroundMidImage = 0x101D2
    const val kParamFieldSurroundDepth = 0x101D3

    const val kParamDiffSurroundEnable = 0x101E0
    const val kParamDiffSurroundDelay = 0x101E1
    const val kParamDiffSurroundReverse = 0x101E2
    const val kParamDiffSurroundWetDryMix = 0x101E3
    const val kParamDiffSurroundLpCutoff = 0x101E4

    const val kParamStereoImagerEnable = 0x101F0
    const val kParamStereoImagerLowWidth = 0x101F1
    const val kParamStereoImagerMidWidth = 0x101F2
    const val kParamStereoImagerHighWidth = 0x101F3
    const val kParamStereoImagerLowCrossover = 0x101F4
    const val kParamStereoImagerHighCrossover = 0x101F5

    const val kParamHeadphoneSurroundEnable = 0x10200
    const val kParamHeadphoneSurroundQuality = 0x10201

    const val kParamReverbEnable = 0x10210
    const val kParamReverbRoomSize = 0x10211
    const val kParamReverbWidth = 0x10212
    const val kParamReverbDamp = 0x10213
    const val kParamReverbWet = 0x10214
    const val kParamReverbDry = 0x10215

    const val kParamDynamicSystemEnable = 0x10220
    const val kParamDynamicSystemXCoefficients = 0x10221
    const val kParamDynamicSystemYCoefficients = 0x10222
    const val kParamDynamicSystemSideGain = 0x10223
    const val kParamDynamicSystemStrength = 0x10224

    const val kParamClarityEnable = 0x10230
    const val kParamClarityMode = 0x10231
    const val kParamClarityGain = 0x10232
    const val kParamCureEnable = 0x10240
    const val kParamCureCrossfeedPreset = 0x10241

    const val kParamTubeSimulatorEnable = 0x10250
    const val kParamAnalogXEnable = 0x10260
    const val kParamAnalogXMode = 0x10261

    const val kParamSpeakerCorrectionEnable = 0x10270

    const val kParamMultibandCompressorEnable = 0x10280
    const val kParamMultibandCompressorBandCount = 0x10281
    const val kParamMultibandCompressorCrossoverFrequency = 0x10282
    const val kParamMultibandCompressorBandThreshold = 0x10283
    const val kParamMultibandCompressorBandRatio = 0x10284
    const val kParamMultibandCompressorBandKnee = 0x10285
    const val kParamMultibandCompressorBandKneeAuto = 0x10286
    const val kParamMultibandCompressorBandGain = 0x10287
    const val kParamMultibandCompressorBandGainAuto = 0x10288
    const val kParamMultibandCompressorBandAttack = 0x10289
    const val kParamMultibandCompressorBandAttackAuto = 0x1028A
    const val kParamMultibandCompressorBandRelease = 0x1028B
    const val kParamMultibandCompressorBandReleaseAuto = 0x1028C
    const val kParamMultibandCompressorBandKneeMulti = 0x1028D
    const val kParamMultibandCompressorBandMaxAttack = 0x1028E
    const val kParamMultibandCompressorBandMaxRelease = 0x1028F
    const val kParamMultibandCompressorBandCrest = 0x10290
    const val kParamMultibandCompressorBandAdapt = 0x10291
    const val kParamMultibandCompressorBandNoClip = 0x10292
    const val kParamMultibandCompressorBandEnable = 0x10293

    const val kParamDynamicEqEnable = 0x102A0
    const val kParamDynamicEqBandCount = 0x102A1
    const val kParamDynamicEqBandFrequency = 0x102A2
    const val kParamDynamicEqBandQ = 0x102A3
    const val kParamDynamicEqBandGain = 0x102A4
    const val kParamDynamicEqBandThreshold = 0x102A5
    const val kParamDynamicEqBandAttack = 0x102A6
    const val kParamDynamicEqBandRelease = 0x102A7
    const val kParamDynamicEqBandFilterType = 0x102A8
}
