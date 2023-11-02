import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.05"

project(ParentProject())

// This project contains multiple subprojects
fun ParentProject() : Project {
        return Project {
            description = "This project's made from the DSL"
            params {
                param("TEST-PARAM", "pls ignore")
            }
            subProject(NightlyTests())
            subProject(MMUpstreamTesting())
        }
}

fun NightlyTests() : Project {
    var id = RelativeId("Google_NightlyTests") // Passed to child resources for making IDs

    return Project {
        id("Google_NightlyTests")
        name = "[Sarah Test] Nightly Tests"
        buildType(AccTestBuildConfig(id,1, "internal/services/packageA"))
        buildType(AccTestBuildConfig(id,2, "internal/services/packageB"))
    }
}

fun MMUpstreamTesting() : Project {
    var id = RelativeId("Google_MMUpstreamTesting") // Passed to child resources for making IDs

    return Project {
        id("Google_MMUpstreamTesting")
        name = "[Sarah Test] MM Upstream Testing"
        buildType(AccTestBuildConfig(id, 1, "internal/services/packageA"))
        buildType(AccTestBuildConfig(id,2, "internal/services/packageB"))
    }
}

object GoCodeVCSRoot : GitVcsRoot({
    name = "https://github.com/SarahFrench/teamcity-example#refs/heads/main"
    url = "https://github.com/SarahFrench/teamcity-example"
    branch = "refs/heads/main"
    branchSpec = "+refs/heads/release-*"
})

fun AccTestBuildConfig(parentId: Id, number: Number, path: String) : BuildType {

    val parallelism: Int = 12
    val testPrefix: String = "TestAcc"
    val testTimeout: String = "12"
    val sweeperRegions: String = "" // Not used
    val sweeperRun: String = "" // Not used

    return BuildType {

        id = AbsoluteId("${parentId}_my_build_${number}") // Need to re-add the replace char function
        name = "My Build $number"

        vcs {
            root(GoCodeVCSRoot)
            cleanCheckout = true
        }

        steps {
            SetGitCommitBuildId()
            TagBuildToIndicatePurpose()
            ConfigureGoEnv()
            DownloadTerraformBinary()
            RunAcceptanceTests()
            // RunSweepers(sweeperName)
        }

        features {
            Golang()
        }

        params {
            // ConfigureGoogleSpecificTestParameters(environmentVariables)
            TerraformAcceptanceTestParameters(parallelism, testPrefix, testTimeout, sweeperRegions, sweeperRun)
            TerraformLoggingParameters()
            TerraformAcceptanceTestsFlag()
            TerraformCoreBinaryTesting()
            TerraformShouldPanicForSchemaErrors()
            ReadOnlySettings()
            WorkingDirectory(path)
        }

        artifactRules = "%teamcity.build.checkoutDir%/debug*.txt"

        failureConditions {
            errorMessage = true
            executionTimeoutMin = defaultBuildTimeoutDuration
        }
    }
}