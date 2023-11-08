package projects
import jetbrains.buildServer.configs.kotlin.*
import builds.AccTestBuildConfig
import jetbrains.buildServer.configs.kotlin.ui.createProjectFeature

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
                format = CustomChart.Format.DURATION
            }
        }

    }
}