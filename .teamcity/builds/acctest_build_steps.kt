package builds

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import DefaultTerraformCoreVersion
import UseTeamCityGoTest

// NOTE: this file includes Extensions of the Kotlin DSL's class BuildSteps
// This allows us to reuse code in the config easily, while ensuring the same build steps can be used across builds.
// See the class's documentation: https://teamcity.jetbrains.com/app/dsl-documentation/root/build-steps/index.html

fun BuildSteps.ConfigureGoEnv() {
    step(ScriptBuildStep {
        name = "Configure Go version using .go-version file"
        scriptContent = "goenv install -s \$(goenv local) && goenv rehash"
    })
}

fun BuildSteps.SetGitCommitBuildId() {
    step(ScriptBuildStep {
        name = "Set build id as shortened git commit hash"
        scriptContent = """
            #!/bin/bash
            GIT_HASH=%system.build.vcs.number%
            GIT_HASH_SHORT=${'$'}{GIT_HASH:0:7}
            echo "##teamcity[buildNumber '${'$'}{GIT_HASH_SHORT}']"
        """.trimIndent()
        // ${'$'} is required to allow creating a script in TeamCity that contains
        // parts like ${GIT_HASH_SHORT} without having Kotlin syntax issues. For more info see:
        // https://youtrack.jetbrains.com/issue/KT-2425/Provide-a-way-for-escaping-the-dollar-sign-symbol-in-multiline-strings-and-string-templates
    })
}

fun BuildSteps.TagBuildToIndicatePurpose() {
    step(ScriptBuildStep {
        name = "Set build tag to indicate if build is run automatically or manually triggered"
        scriptContent = """
            #!/bin/bash
            TRIGGERED_BY_USERNAME=%teamcity.build.triggeredBy.username%

            if [[ "${'$'}TRIGGERED_BY_USERNAME" = "n/a" ]] ; then
                echo "Build was triggered as part of automated testing. We know this because the `triggeredBy.username` value was `n/a`, value: ${'$'}{TRIGGERED_BY_USERNAME}"
                TAG="nightly-test"
                echo "##teamcity[addBuildTag '${'$'}{TAG}']"
            else
                echo "Build wasn't triggered as part of automated testing. We know this because the `triggeredBy.username` value was not `n/a`, value: ${'$'}{TRIGGERED_BY_USERNAME}"
                TAG="one-off-build"
                echo "##teamcity[addBuildTag '${'$'}{TAG}']"
            fi
        """.trimIndent()
        // ${'$'} is required to allow creating a script in TeamCity that contains
        // parts like ${GIT_HASH_SHORT} without having Kotlin syntax issues. For more info see:
        // https://youtrack.jetbrains.com/issue/KT-2425/Provide-a-way-for-escaping-the-dollar-sign-symbol-in-multiline-strings-and-string-templates
    })
}

fun BuildSteps.DownloadTerraformBinary() {
    // https://releases.hashicorp.com/terraform/0.12.28/terraform_0.12.28_linux_amd64.zip
    var terraformUrl = "https://releases.hashicorp.com/terraform/%env.TERRAFORM_CORE_VERSION%/terraform_%env.TERRAFORM_CORE_VERSION%_linux_amd64.zip"
    step(ScriptBuildStep {
        name = "Download Terraform version %s".format(DefaultTerraformCoreVersion)
        scriptContent = """
        #!/bin/bash
        mkdir -p tools
        wget -O tf.zip %s
        unzip tf.zip
        mv terraform tools/
        """.format(terraformUrl).trimIndent()
    })
}

// RunSweepers runs sweepers, and relies on set build configuration parameters
fun BuildSteps.RunSweepers(sweeperStepName : String) {
    step(ScriptBuildStep{
        name = sweeperStepName
        scriptContent = "go test -v \"%PACKAGE_PATH%\" -sweep=\"%SWEEPER_REGIONS%\" -sweep-allow-failures -sweep-run=\"%SWEEP_RUN%\" -timeout 30m"
    })
}

// RunAcceptanceTests runs tests for a given directory, using either:
// - TeamCity's test runner - stops remaining tests after a failure
// - jen20/teamcity-go-test - allows tests to continue after a failure, and requires a test binary
fun BuildSteps.RunAcceptanceTests() {
    if (UseTeamCityGoTest) {
        step(ScriptBuildStep {
            name = "Run Tests"
            scriptContent = "go test -v \"%PACKAGE_PATH%\" -timeout=\"%TIMEOUT%h\" -test.parallel=\"%PARALLELISM%\" -run=\"%TEST_PREFIX%\" -json"
        })
    } else {
        step(ScriptBuildStep {
            name = "Compile Test Binary"
            workingDir = "%PACKAGE_PATH%"
            scriptContent = """
                #!/bin/bash
                export TEST_FILE_COUNT=$(ls ./*_test.go | wc -l)
                if test ${'$'}TEST_FILE_COUNT -gt "0"; then
                    echo "Compiling test binary"
                    go test -c -o test-binary
                else
                    echo "Skipping compilation of test binary; no Go test files found"
                fi
            """.trimIndent()

        })

        step(ScriptBuildStep {
            name = "Run via jen20/teamcity-go-test"
            workingDir = "%PACKAGE_PATH%"
            scriptContent = """
                #!/bin/bash
                if ! test -f "./test-binary"; then
                  echo "Skipping test execution; file ./test-binary does not exist."
                  exit 0
                fi
                
                export TEST_COUNT=${'$'}(./test-binary -test.list=%TEST_PREFIX% | wc -l)
                echo "Found ${'$'}{TEST_COUNT} tests that match the given test prefix %TEST_PREFIX%"
                if test ${'$'}TEST_COUNT -le "0"; then
                  echo "Skipping test execution; no tests to run"
                  exit 0
                fi
                
                echo "Starting tests"  
                ./test-binary -test.list="%TEST_PREFIX%" | teamcity-go-test -test ./test-binary -parallelism "%PARALLELISM%" -timeout "%TIMEOUT%h"
            """.trimIndent()
        })
    }
}