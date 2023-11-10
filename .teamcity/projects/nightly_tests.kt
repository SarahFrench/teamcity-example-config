package projects

import jetbrains.buildServer.configs.kotlin.*
import builds.AccTestBuildConfig
import builds.StartBuildConfig
import builds.FinishBuildConfig

import jetbrains.buildServer.configs.kotlin.ui.createProjectFeature
import jetbrains.buildServer.configs.kotlin.CustomChart.Serie
import jetbrains.buildServer.configs.kotlin.CustomChart.SeriesKey


val NightlyTestsProjectId = RelativeId("NightlyTests")

fun NightlyTests() : Project {

    return Project {
        id("NightlyTests")
        name = "[Sarah Test] Nightly Tests"

        buildType(StartBuildConfig(NightlyTestsProjectId))

        buildType(AccTestBuildConfig(NightlyTestsProjectId,1, "internal/services/packageA"))
        buildType(AccTestBuildConfig(NightlyTestsProjectId,2, "internal/services/packageB"))

        buildType(FinishBuildConfig(NightlyTestsProjectId))

    }
}