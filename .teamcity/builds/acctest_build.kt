package builds

import jetbrains.buildServer.configs.kotlin.AbsoluteId
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.Id
import vcs_roots.GoCodeVCSRoot
import DefaultBuildTimeoutDuration

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
            executionTimeoutMin = DefaultBuildTimeoutDuration
        }
    }
}