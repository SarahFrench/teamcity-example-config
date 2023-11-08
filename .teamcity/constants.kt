// This file contains some constants used elsewhere in the Kotlin code.
// They should be moved elsewhere when the config is finished and then refactored for clarity.

// The name of the Terraform provider
const val ProviderName = "google"

// Specifies the default version of Terraform Core which should be used for testing
const val DefaultTerraformCoreVersion = "1.2.5"

// Used to change build step behaviour - should we use TeamCity's native Go test
const val UseTeamCityGoTest = false

// How long a build can run before being terminated with a timeout
const val DefaultBuildTimeoutDuration = 60 * 12 //12 hours in minutes