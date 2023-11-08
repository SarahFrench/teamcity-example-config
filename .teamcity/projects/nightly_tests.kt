package projects
import jetbrains.buildServer.configs.kotlin.*
import builds.AccTestBuildConfig
import jetbrains.buildServer.configs.kotlin.ui.createProjectFeature
import jetbrains.buildServer.configs.kotlin.CustomChart.Serie
import jetbrains.buildServer.configs.kotlin.CustomChart.SeriesKey


val NightlyTestsProjectId = RelativeId("NightlyTests")

fun NightlyTests() : Project {

    return Project {
        id("NightlyTests")
        name = "[Sarah Test] Nightly Tests"

        buildType(AccTestBuildConfig(NightlyTestsProjectId,1, "internal/services/packageA"))
        buildType(AccTestBuildConfig(NightlyTestsProjectId,2, "internal/services/packageB"))

        createProjectFeature {
            projectCustomChart {
                id = "ProjectCustomChart_1"
                title = "Chart Title"
                seriesTitle = "Series Title"
                format = CustomChart.Format.PERCENT
                series = listOf(
                    Serie(title = "Success Rate", key = SeriesKey.SUCCESS_RATE, sourceBuildTypeId = AbsoluteId("${NightlyTestsProjectId}_my_build_${1}"))
                )
            }
        }

    }
}