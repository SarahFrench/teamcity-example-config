package vcs_roots

import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

object GoCodeVCSRoot : GitVcsRoot({
    name = "https://github.com/SarahFrench/teamcity-example#refs/heads/main"
    url = "https://github.com/SarahFrench/teamcity-example"
    branch = "refs/heads/main"
    branchSpec = "+:*"
})
