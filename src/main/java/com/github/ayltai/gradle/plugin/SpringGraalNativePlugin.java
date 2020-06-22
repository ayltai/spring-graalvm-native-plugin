package com.github.ayltai.gradle.plugin;

import javax.annotation.Nonnull;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class SpringGraalNativePlugin implements Plugin<Project> {
    //region Constants

    static final String CURRENT_TASK   = "buildNativeImage";
    static final String DEPENDENT_TASK = "bootJar";

    private static final String DEPENDENT_REPO     = "https://repo.spring.io/milestone";
    private static final String DEPENDENT_ARTIFACT = "org.springframework.experimental:spring-graalvm-native:0.7.1";

    //endregion

    @Override
    public void apply(@Nonnull final Project project) {
        project.getRepositories()
            .maven(repo -> repo.setUrl(SpringGraalNativePlugin.DEPENDENT_REPO));

        project.getConfigurations()
            .maybeCreate("implementation")
            .getDependencies()
            .add(project.getDependencies()
                .create(SpringGraalNativePlugin.DEPENDENT_ARTIFACT));

        final SpringGraalNativeExtension extension = project.getExtensions().create("nativeImage", SpringGraalNativeExtension.class, project.getObjects());

        project.getTasks()
            .register(SpringGraalNativePlugin.CURRENT_TASK, SpringGraalNativeTask.class)
            .configure(task -> {
                task.dependsOn(SpringGraalNativePlugin.getDependency(project));

                task.traceClassInitialization.set(extension.getTraceClassInitialization());
                task.removeSaturatedTypeFlows.set(extension.getRemoveSaturatedTypeFlows());
                task.reportExceptionStackTraces.set(extension.getReportExceptionStackTraces());
                task.printAnalysisCallTree.set(extension.getPrintAnalysisCallTree());
                task.enabledAllSecurityServices.set(extension.getEnabledAllSecurityServices());
                task.staticallyLinked.set(extension.getStaticallyLinked());
                task.verbose.set(extension.getVerbose());
                task.warnMissingSelectorHints.set(extension.getWarnMissingSelectorHints());
                task.removeUnusedAutoConfig.set(extension.getRemoveUnusedAutoConfig());
                task.removeYamlSupport.set(extension.getRemoveYamlSupport());
                task.removeXmlSupport.set(extension.getRemoveXmlSupport());
                task.removeSpelSupport.set(extension.getRemoveSpelSupport());
                task.removeJmxSupport.set(extension.getRemoveJmxSupport());
                task.verify.set(extension.getVerify());
                task.springNativeVerbose.set(extension.getSpringNativeVerbose());
                task.springNativeMode.set(extension.getSpringNativeMode());
                task.dumpConfig.set(extension.getDumpConfig());
                task.mainClassName.set(extension.getMainClassName());
                task.maxHeapSize.set(extension.getMaxHeapSize());
                task.initializeAtBuildTime.set(extension.getInitializeAtBuildTime());
            });
    }

    @Nonnull
    protected static Task getDependency(@Nonnull final Project project) {
        return project.getTasks().getByName(SpringGraalNativePlugin.DEPENDENT_TASK);
    }
}
