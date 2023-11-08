package projects

import jetbrains.buildServer.configs.kotlin.*
import builds.AccTestBuildConfig


val MMUpstreamTestingProjectId = RelativeId("MMUpstreamTesting")

fun MMUpstreamTesting() : Project {

    return Project {
        id("MMUpstreamTesting")
        name = "[Sarah Test] MM Upstream Testing"

        buildType(AccTestBuildConfig(MMUpstreamTestingProjectId, 1, "internal/services/packageA"))
        buildType(AccTestBuildConfig(MMUpstreamTestingProjectId,2, "internal/services/packageB"))
    }
}