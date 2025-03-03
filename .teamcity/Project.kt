import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.CheckoutMode
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs


object Project : Project({
    buildType(Verify)
    buildType(Publish)
    params {
        param("env.GRADLE_ENTERPRISE_ACCESS_KEY", "%ge.gradle.org.access.key%")
        param("env.GRADLE_CACHE_REMOTE_URL", "%gradle.cache.remote.url%")
        param("env.GRADLE_CACHE_REMOTE_USERNAME", "%gradle.cache.remote.username%")
        password("env.GRADLE_CACHE_REMOTE_PASSWORD", "%gradle.cache.remote.password%")
    }
})

object Verify : BuildType({
    id = AbsoluteId("Build_Tool_Services_Exemplar_Verify")
    uuid = "Build_Tool_Services_Exemplar_Verify"
    name = "Verify Exemplar"
    description = "Verify integrity of Exemplar libraries"

    vcs {
        root(AbsoluteId("Exemplar_Master"))
        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
    }

    requirements {
        contains("teamcity.agent.jvm.os.name", "Linux")
    }

    triggers {
        vcs {
            branchFilter = """
    +:refs/heads/*
""".trimIndent()
        }
    }

    steps {
        gradle {
            useGradleWrapper = true
            tasks = "check"
            gradleParams = "--build-cache -Dgradle.cache.remote.push=%env.BUILD_CACHE_PUSH%"
            buildFile = "build.gradle.kts"
        }
    }
})

object Publish : BuildType({
    id = AbsoluteId("Build_Tool_Services_Exemplar_Publish")
    uuid = "Build_Tool_Services_Exemplar_Publish"
    name = "Publish Exemplar"
    description = "Publish Exemplar libraries to Maven Central staging repository"

    vcs {
        root(AbsoluteId("Exemplar_Master"))
        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
    }

    requirements {
        contains("teamcity.agent.jvm.os.name", "Linux")
    }

    steps {
        gradle {
            useGradleWrapper = true
            tasks = "clean publishMavenJavaPublicationToSonatypeRepository"
            gradleParams = "--build-cache -Dgradle.publish.skip.namespace.check=true"
            buildFile = "build.gradle.kts"
        }
    }
    params {
        param("env.MAVEN_CENTRAL_STAGING_REPO_USER", "%mavenCentralStagingRepoUser%")
        password("env.MAVEN_CENTRAL_STAGING_REPO_PASSWORD", "%mavenCentralStagingRepoPassword%")
    }
})
