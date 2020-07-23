# Spring GraalVM native Gradle plugin

[![GitHub workflow status](https://img.shields.io/github/workflow/status/ayltai/spring-graalvm-native-plugin/CI?style=flat)](https://github.com/ayltai/spring-graalvm-native-plugin/actions)
[![Codacy grade](https://img.shields.io/codacy/grade/271a4011f13d4200842d3334c21e8b2f.svg?style=flat)](https://app.codacy.com/app/AlanTai/spring-graalvm-native-plugin/dashboard)
[![Sonar quality gate](https://img.shields.io/sonar/quality_gate/ayltai_spring-graalvm-native-plugin?style=flat&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_spring-graalvm-native-plugin)
[![Sonar violations (short format)](https://img.shields.io/sonar/violations/ayltai_spring-graalvm-native-plugin?style=flat&format=short&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_spring-graalvm-native-plugin)
[![Sonar Test Success Rate](https://img.shields.io/sonar/test_success_density/ayltai_spring-graalvm-native-plugin?style=flat&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_spring-graalvm-native-plugin)
[![Code Coverage](https://img.shields.io/codecov/c/github/ayltai/spring-graalvm-native-plugin.svg?style=flat)](https://codecov.io/gh/ayltai/spring-graalvm-native-plugin)
[![Sonar Coverage](https://img.shields.io/sonar/coverage/ayltai_spring-graalvm-native-plugin?style=flat&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_spring-graalvm-native-plugin)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ayltai_spring-graalvm-native-plugin&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=ayltai_spring-graalvm-native-plugin)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ayltai_spring-graalvm-native-plugin&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=ayltai_spring-graalvm-native-plugin)
[![Sonar Tech Debt](https://img.shields.io/sonar/tech_debt/ayltai_spring-graalvm-native-plugin?style=flat&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_spring-graalvm-native-plugin)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ayltai_spring-graalvm-native-plugin&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=ayltai_spring-graalvm-native-plugin)
![Maintenance](https://img.shields.io/maintenance/yes/2020?style=flat)
[![Release](https://img.shields.io/github/release/ayltai/spring-graalvm-native-plugin.svg?style=flat)](https://github.com/ayltai/spring-graalvm-native-plugin/releases)
[![License](https://img.shields.io/github/license/ayltai/spring-graalvm-native-plugin.svg?style=flat)](https://github.com/ayltai/spring-graalvm-native-plugin/blob/master/LICENSE)

Supports for building Spring Boot applications as GraalVM native images.

[https://plugins.gradle.org/plugin/com.github.ayltai.spring-graalvm-native-plugin](https://plugins.gradle.org/plugin/com.github.ayltai.spring-graalvm-native-plugin)

[![Buy me a coffee](https://img.shields.io/static/v1?label=Buy%20me%20a&message=coffee&color=important&style=flat&logo=buy-me-a-coffee&logoColor=white)](https://buymeacoff.ee/ayltai)

## Quick start

### Install GraalVM and GraalVM Native Image
1. Install the latest version of [GraalVM](https://www.graalvm.org/getting-started/#install-graalvm)
2. Set `JAVA_HOME` and `PATH` appropriately for GraalVM
3. Install GraalVM Native Image:
   ```shell script
   gu install native-image
   ```

### Apply Gradle plugin

#### Groovy
Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):
```groovy
plugins {
    id 'com.github.ayltai.spring-graalvm-native-plugin' version '1.0.7'
}
```

Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):
```groovy
buildscript {
    repositories {
        maven {
            url 'https://plugins.gradle.org/m2/'
        }
    }

    dependencies {
        classpath 'gradle.plugin.com.github.ayltai:spring-graalvm-native-plugin:1.0.7'
    }
}

apply plugin: 'com.github.ayltai.spring-graalvm-native-plugin'
```

#### Kotlin
Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):
```groovy
plugins {
    id('com.github.ayltai.spring-graalvm-native-plugin') version '1.0.7'
}
```

Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):
```groovy
buildscript {
    repositories {
        maven {
            url = uri('https://plugins.gradle.org/m2/')
        }
    }

    dependencies {
        classpath('gradle.plugin.com.github.ayltai:spring-graalvm-native-plugin:1.0.7')
    }
}
```

### Specify build arguments
This plugin uses the following example Gradle extension for configuration:
```groovy
nativeImage {
    mainClassName = 'com.example.springboot.Application'

    traceClassInitialization   = true
    reportExceptionStackTraces = true
    removeUnusedAutoConfig     = true
    removeYamlSupport          = true
    maxHeapSize                = '6G'
}
```

More configuration options can be found [here](https://github.com/ayltai/spring-graalvm-native-plugin#configuration).

### Update Spring Boot annotation
You need to specify `proxyBeanMethods = false` for your `@SpringBootApplication` annotation:
```java
@SpringBootApplication(proxyBeanMethods = false)
public class TomcatApplication {
    public static void main(final String[] args) {
        SpringApplication.run(TomcatApplication.class, args);
    }
}
```

### Build GraalVM Native Image
1. Run the Gradle task `buildNativeImage`
2. The native image can be located at `<buildDir>/native`

## Sample project
[samples](https://github.com/ayltai/spring-graalvm-native-plugin/tree/master/samples) contains various samples that demonstrate the basic usage of this Gradle plugin.

## Configuration
| Property | Type | Description |
|----------|------|-------------|
| `mainClassName` (Required) | `String` | The fully qualified name of the Java class that contains a `main` method for the entry point of the Native Image executable. |
| `traceClassInitialization` | `boolean` | Provides useful information to debug class initialization issues. |
| `removeSaturatedTypeFlows` | `boolean` | Reduces build time and decrease build memory consumption, especially for big projects. |
| `reportExceptionStackTraces` | `boolean` | Provides more detail should something go wrong. |
| `printAnalysisCallTree` | `boolean` | Helps to find what classes, methods, and fields are used and why. You can find more details in GraalVM [reports documentation](https://github.com/oracle/graal/blob/master/substratevm/REPORTS.md). |
| `enabledAllSecurityServices` | `boolean` | Required for HTTPS and crypto. |
| `staticallyLinked` | `boolean` | Builds a statically linked executable, useful to deploy on a `FROM scratch` Docker image. |
| `warnMissingSelectorHints` | `boolean` | Switches the feature from a hard error for missing hints to a warning. See the [Troubleshooting](https://repo.spring.io/milestone/org/springframework/experimental/spring-graalvm-native-docs/0.7.1/spring-graalvm-native-docs-0.7.1.zip!/reference/index.html#troubleshooting) section for more details on this. |
| `removeUnusedAutoConfig` | `boolean` | Disables removal of unused configurations. |
| `verbose` | `boolean` | Makes image building output more verbose. |
| `removeYamlSupport` | `boolean` | Removes Yaml support from Spring Boot, enabling faster compilation and smaller executables. |
| `removeXmlSupport` | `boolean` | Removes XML support from Spring Boot, enabling faster compilation and smaller executables. |
| `removeSpelSupport` | `boolean` | Removes SpEL support from Spring Boot, enabling faster compilation and smaller executables. |
| `removeJmxSupport` | `boolean` | Removes JMX support from Spring Boot, enabling faster compilation and smaller executables. |
| `verify` | `boolean` | Switches on the verifier mode. See the [Troubleshooting](https://repo.spring.io/milestone/org/springframework/experimental/spring-graalvm-native-docs/0.7.1/spring-graalvm-native-docs-0.7.1.zip!/reference/index.html#troubleshooting) section for more details on this experimental option. |
| `springNativeVerbose` | `boolean` | Outputs lots of information about the feature behavior as it processes auto-configuration and chooses which to include. |
| `springNativeMode` | `String` | Switches how much configuration the feature actually provides to native-image. The default is `feature` where it provides everything (including deep analysis of auto-configuration). `agent` should be used if only wishing the feature to provide substitutions and initialization configuration - in this mode you should have used the agent to collect the rest of the configuration. `functional` is when working with functional bean registration (Spring Fu style). In this mode the feature will provide initialization and resource configuration but nothing more. |
| `dumpConfig` | `String` | Dumps the configuration to the specified file. |
| `maxHeapSize` | `String` | Maximum allowed Java heap size for building GraalVM Native Image. |
| `initializeAtBuildTime` | `List<String>` | Use it with specific classes or package to initialize classes at build time. |

See [Spring GraalVM Native configuration options](https://repo.spring.io/milestone/org/springframework/experimental/spring-graalvm-native-docs/0.7.1/spring-graalvm-native-docs-0.7.1.zip!/reference/index.html#options) for more details.

## GitHub action
[setup-graalvm](https://github.com/marketplace/actions/setup-graalvm-action) is a GitHub action that sets up a GraalVM environment for your [GitHub workflow](https://github.com/features/actions).

## License
[MIT](https://github.com/ayltai/spring-graalvm-native-plugin/blob/master/LICENSE)

## References
* [GraalVM](https://www.graalvm.org)
* [GraalVM Native Image](https://www.graalvm.org/docs/reference-manual/native-image)
* [Spring GraalVM Native](https://github.com/spring-projects-experimental/spring-graalvm-native)
* [Spring GraalVM Native configuration options](https://repo.spring.io/milestone/org/springframework/experimental/spring-graalvm-native-docs/0.7.1/spring-graalvm-native-docs-0.7.1.zip!/reference/index.html#options)
