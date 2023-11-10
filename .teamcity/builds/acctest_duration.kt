package builds

import jetbrains.buildServer.configs.kotlin.AbsoluteId
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.Id
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import vcs_roots.GoCodeVCSRoot


fun StartBuildConfig(parentId: Id) : BuildType {

    return BuildType {

        id = AbsoluteId("${parentId}_start") // Need to re-add the replace char function
        name = "Start"

        vcs {
            root(GoCodeVCSRoot)
            cleanCheckout = true
        }

        steps {
            step(ScriptBuildStep {
                name = "Record the start of the test suite"
                scriptContent = """
                    #!/bin/bash
                    GIT_HASH=%system.build.vcs.number%
                    GIT_HASH_SHORT=${'$'}{GIT_HASH:0:7}
                    echo "#teamcity[testStarted name='nightly-test-${'$'}{GIT_HASH_SHORT}']"
                """.trimIndent()
                // ${'$'} is required to allow creating a script in TeamCity that contains
                // parts like ${GIT_HASH_SHORT} without having Kotlin syntax issues. For more info see:
                // https://youtrack.jetbrains.com/issue/KT-2425/Provide-a-way-for-escaping-the-dollar-sign-symbol-in-multiline-strings-and-string-templates
            })
        }
    }
}

fun FinishBuildConfig(parentId: Id) : BuildType {

    return BuildType {

        id = AbsoluteId("${parentId}_finish") // Need to re-add the replace char function
        name = "Finish"

        vcs {
            root(GoCodeVCSRoot)
            cleanCheckout = true
        }

        steps {
            step(ScriptBuildStep {
                name = "Record the start of the test suite"
                scriptContent = """
                    #!/bin/bash
                    GIT_HASH=%system.build.vcs.number%
                    GIT_HASH_SHORT=${'$'}{GIT_HASH:0:7}
                    echo "#teamcity[testFinshed name='nightly-test-${'$'}{GIT_HASH_SHORT}']"
                """.trimIndent()
                // ${'$'} is required to allow creating a script in TeamCity that contains
                // parts like ${GIT_HASH_SHORT} without having Kotlin syntax issues. For more info see:
                // https://youtrack.jetbrains.com/issue/KT-2425/Provide-a-way-for-escaping-the-dollar-sign-symbol-in-multiline-strings-and-string-templates
            })
        }
    }
}