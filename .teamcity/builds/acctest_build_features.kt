package builds

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.GolangFeature
import UseTeamCityGoTest

// NOTE: this file includes Extensions of the Kotlin DSL's class BuildFeature
// This allows us to reuse code in the config easily, while ensuring the same build features can be used across builds.
// See the class's documentation: https://teamcity.jetbrains.com/app/dsl-documentation/root/build-feature/index.html

fun BuildFeatures.Golang() {
    if (UseTeamCityGoTest) {
        feature(GolangFeature {
            testFormat = "json"
        })
    }
}