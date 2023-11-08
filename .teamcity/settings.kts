import jetbrains.buildServer.configs.kotlin.*
import vcs_roots.GoCodeVCSRoot
import projects.NightlyTests
import projects.MMUpstreamTesting

version = "2023.05"

project(ParentProject())

// This project contains multiple subprojects
fun ParentProject() : Project {
        return Project {
            description = "This project's made from the DSL"
            params {
                param("TEST-PARAM", "pls ignore")
            }

            // VCS Root(s) used by subprojects. This is separate to the VCS Root used to pull in versioned settings.
            vcsRoot(GoCodeVCSRoot)

            // SubProjects
            subProject(NightlyTests())
            subProject(MMUpstreamTesting())
        }
}

