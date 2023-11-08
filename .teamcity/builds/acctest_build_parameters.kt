package builds

import jetbrains.buildServer.configs.kotlin.*
import ProviderName
import DefaultTerraformCoreVersion

// NOTE: this file includes Extensions of the Kotlin DSL's class ParametrizedWithType
// This allows us to reuse code in the config easily, while ensuring the same parameters are set across builds.
// See the class's documentation: https://teamcity.jetbrains.com/app/dsl-documentation/root/parametrized-with-type/index.html

fun ParametrizedWithType.TerraformAcceptanceTestParameters(parallelism : Int, prefix : String, timeout: String, sweeperRegions: String, sweepRun: String) {
    text("PARALLELISM", "%d".format(parallelism))
    text("TEST_PREFIX", prefix)
    text("TIMEOUT", timeout)
    text("SWEEPER_REGIONS", sweeperRegions)
    text("SWEEP_RUN", sweepRun)
}

fun ParametrizedWithType.TerraformLoggingParameters() {
    // Set logging levels to match old projects
    text("env.TF_LOG", "DEBUG")
    text("env.TF_LOG_CORE", "WARN")
    text("env.TF_LOG_SDK_FRAMEWORK", "INFO")

    // Set where logs are sent
    text("PROVIDER_NAME", ProviderName)
    text("env.TF_LOG_PATH_MASK", "%system.teamcity.build.checkoutDir%/debug-%PROVIDER_NAME%-%env.BUILD_NUMBER%-%s.txt")
}

fun ParametrizedWithType.ReadOnlySettings() {
    hiddenVariable("teamcity.ui.settings.readOnly", "true", "Requires build configurations be edited via Kotlin")
}

fun ParametrizedWithType.TerraformAcceptanceTestsFlag() {
    hiddenVariable("env.TF_ACC", "1", "Set to a value to run the Acceptance Tests")
}

fun ParametrizedWithType.TerraformCoreBinaryTesting() {
    text("env.TERRAFORM_CORE_VERSION", DefaultTerraformCoreVersion, "The version of Terraform Core which should be used for testing")
    hiddenVariable("env.TF_ACC_TERRAFORM_PATH", "%system.teamcity.build.checkoutDir%/tools/terraform", "The path where the Terraform Binary is located")
}

fun ParametrizedWithType.TerraformShouldPanicForSchemaErrors() {
    hiddenVariable("env.TF_SCHEMA_PANIC_ON_ERROR", "1", "Panic if unknown/unmatched fields are set into the state")
}

fun ParametrizedWithType.WorkingDirectory(path : String) {
    text("PACKAGE_PATH", path, "", "The path at which to run - automatically updated", ParameterDisplay.HIDDEN)
}

fun ParametrizedWithType.hiddenVariable(name: String, value: String, description: String) {
    text(name, value, "", description, ParameterDisplay.HIDDEN)
}

fun ParametrizedWithType.hiddenPasswordVariable(name: String, value: String, description: String) {
    password(name, value, "", description, ParameterDisplay.HIDDEN)
}