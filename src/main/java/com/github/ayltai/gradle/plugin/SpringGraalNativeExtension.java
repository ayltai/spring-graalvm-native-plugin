package com.github.ayltai.gradle.plugin;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

public class SpringGraalNativeExtension {
    private static final String SPRING_NATIVE_MODE = SpringNativeMode.FEATURE;

    //region Gradle plugin properties

    protected final Property<Boolean>    traceClassInitialization;
    protected final Property<Boolean>    removeSaturatedTypeFlows;
    protected final Property<Boolean>    reportExceptionStackTraces;
    protected final Property<Boolean>    printAnalysisCallTree;
    protected final Property<Boolean>    enabledAllSecurityServices;
    protected final Property<Boolean>    staticallyLinked;
    protected final Property<Boolean>    warnMissingSelectorHints;
    protected final Property<Boolean>    removeUnusedAutoConfig;
    protected final Property<Boolean>    verbose;
    protected final Property<Boolean>    removeYamlSupport;
    protected final Property<Boolean>    removeXmlSupport;
    protected final Property<Boolean>    removeSpelSupport;
    protected final Property<Boolean>    removeJmxSupport;
    protected final Property<Boolean>    verify;
    protected final Property<Boolean>    springNativeVerbose;
    protected final Property<String>     springNativeMode;
    protected final Property<String>     dumpConfig;
    protected final Property<String>     mainClassName;
    protected final Property<String>     maxHeapSize;
    protected final ListProperty<String> initializeAtBuildTime;

    //endregion

    public SpringGraalNativeExtension(@Nonnull final Project project) {
        this.traceClassInitialization   = project.getObjects().property(Boolean.class);
        this.removeSaturatedTypeFlows   = project.getObjects().property(Boolean.class);
        this.reportExceptionStackTraces = project.getObjects().property(Boolean.class);
        this.printAnalysisCallTree      = project.getObjects().property(Boolean.class);
        this.enabledAllSecurityServices = project.getObjects().property(Boolean.class);
        this.staticallyLinked           = project.getObjects().property(Boolean.class);
        this.verbose                    = project.getObjects().property(Boolean.class);
        this.warnMissingSelectorHints   = project.getObjects().property(Boolean.class);
        this.removeUnusedAutoConfig     = project.getObjects().property(Boolean.class);
        this.removeYamlSupport          = project.getObjects().property(Boolean.class);
        this.removeXmlSupport           = project.getObjects().property(Boolean.class);
        this.removeSpelSupport          = project.getObjects().property(Boolean.class);
        this.removeJmxSupport           = project.getObjects().property(Boolean.class);
        this.verify                     = project.getObjects().property(Boolean.class);
        this.springNativeVerbose        = project.getObjects().property(Boolean.class);
        this.springNativeMode           = project.getObjects().property(String.class);
        this.dumpConfig                 = project.getObjects().property(String.class);
        this.mainClassName              = project.getObjects().property(String.class);
        this.maxHeapSize                = project.getObjects().property(String.class);
        this.initializeAtBuildTime      = project.getObjects().listProperty(String.class);
    }

    //region Properties

    /**
     * Returns {@code true} if useful information to debug class initialization issues is provided.
     * @return {@code true} if useful information to debug class initialization issues is provided.
     */
    public boolean getTraceClassInitialization() {
        return this.traceClassInitialization.getOrElse(false);
    }

    /**
     * Sets to {@code true} if useful information to debug class initialization issues is provided.
     * @param traceClassInitialization {@code true} if useful information to debug class initialization issues is provided.
     */
    public void setTraceClassInitialization(final boolean traceClassInitialization) {
        this.traceClassInitialization.set(traceClassInitialization);
    }

    /**
     * Returns {@code true} if build time is reduced and build memory consumption is decreased, especially for big projects.
     * @return {@code true} if build time is reduced and build memory consumption is decreased, especially for big projects.
     */
    public boolean getRemoveSaturatedTypeFlows() {
        return this.removeSaturatedTypeFlows.getOrElse(false);
    }

    /**
     * Sets to {@code true} if build time is reduced and build memory consumption is decreased, especially for big projects.
     * @param removeSaturatedTypeFlows {@code true} if build time is reduced and build memory consumption is decreased, especially for big projects.
     */
    public void setRemoveSaturatedTypeFlows(final boolean removeSaturatedTypeFlows) {
        this.removeSaturatedTypeFlows.set(removeSaturatedTypeFlows);
    }

    /**
     * Returns {@code true} if more details are provided should something goes wrong.
     * @return {@code true} if more details are provided should something goes wrong.
     */
    public boolean getReportExceptionStackTraces() {
        return this.reportExceptionStackTraces.getOrElse(false);
    }

    /**
     * Sets to {@code true} if more details are provided should something goes wrong.
     * @param reportExceptionStackTraces {@code true} if more details are provided should something goes wrong.
     */
    public void setReportExceptionStackTraces(final boolean reportExceptionStackTraces) {
        this.reportExceptionStackTraces.set(reportExceptionStackTraces);
    }

    /**
     * Returns {@code true} to help to find what classes, methods, and fields are used and why.
     * @return {@code true} to help to find what classes, methods, and fields are used and why.
     */
    public boolean getPrintAnalysisCallTree() {
        return this.printAnalysisCallTree.getOrElse(false);
    }

    /**
     * Sets to {@code true} to help to find what classes, methods, and fields are used and why.
     * @param printAnalysisCallTree {@code true} to help to find what classes, methods, and fields are used and why.
     */
    public void setPrintAnalysisCallTree(final boolean printAnalysisCallTree) {
        this.printAnalysisCallTree.set(printAnalysisCallTree);
    }

    /**
     * Returns {@code true} if security services are enabled for HTTPS and crypto applications.
     * @return {@code true} if security services are enabled for HTTPS and crypto applications.
     */
    public boolean getEnabledAllSecurityServices() {
        return this.enabledAllSecurityServices.getOrElse(false);
    }

    /**
     * Sets to {@code true} if security services are enabled for HTTPS and crypto applications.
     * @param enabledAllSecurityServices {@code true} if security services are enabled for HTTPS and crypto applications.
     */
    public void setEnabledAllSecurityServices(final boolean enabledAllSecurityServices) {
        this.enabledAllSecurityServices.set(enabledAllSecurityServices);
    }

    /**
     * Returns {@code true} if a statically linked executable is to be built which is useful to deploy on a {@code FROM scratch} Docker image.
     * @return {@code true} if a statically linked executable is to be built.
     */
    public boolean getStaticallyLinked() {
        return this.staticallyLinked.getOrElse(false);
    }

    /**
     * Sets to {@code true} if a statically linked executable is to be built.
     * @param staticallyLinked {@code true} if a statically linked executable is to be built.
     */
    public void setStaticallyLinked(final boolean staticallyLinked) {
        this.staticallyLinked.set(staticallyLinked);
    }

    /**
     * Returns {@code true} if image building output is more verbose.
     * @return {@code true} if image building output is more verbose.
     */
    public boolean getVerbose() {
        return this.verbose.getOrElse(false);
    }

    /**
     * Sets to {@code true} if image building output is more verbose.
     * @param verbose {@code true} if image building output is more verbose.
     */
    public void setVerbose(final boolean verbose) {
        this.verbose.set(verbose);
    }

    /**
     * Returns {@code true} if a hard error for missing hints is switched to a warning.
     * @return {@code true} if a hard error for missing hints is switched to a warning.
     * @see <a href="https://repo.spring.io/milestone/org/springframework/experimental/spring-graalvm-native-docs/0.7.1/spring-graalvm-native-docs-0.7.1.zip!/reference/index.html#troubleshooting">Troubleshooting</a>
     */
    public boolean getWarnMissingSelectorHints() {
        return this.warnMissingSelectorHints.getOrElse(false);
    }

    /**
     * Sets to {@code true} if a hard error for missing hints is switched to a warning.
     * @param warnMissingSelectorHints {@code true} if a hard error for missing hints is switched to a warning..
     */
    public void setWarnMissingSelectorHints(final boolean warnMissingSelectorHints) {
        this.warnMissingSelectorHints.set(warnMissingSelectorHints);
    }

    /**
     * Returns {@code true} if the removal of unused configurations is disabled.
     * @return {@code true} if the removal of unused configurations is disabled.
     */
    public boolean getRemoveUnusedAutoConfig() {
        return this.removeUnusedAutoConfig.getOrElse(false);
    }

    /**
     * Sets to {@code true} if the removal of unused configurations is disabled.
     * @param removeUnusedAutoConfig {@code true} if the removal of unused configurations is disabled.
     */
    public void setRemoveUnusedAutoConfig(final boolean removeUnusedAutoConfig) {
        this.removeUnusedAutoConfig.set(removeUnusedAutoConfig);
    }

    /**
     * Returns {@code true} if Yaml support is removed from Spring Boot, enabling faster compilation and smaller executables.
     * @return {@code true} if Yaml support is removed from Spring Boot.
     */
    public boolean getRemoveYamlSupport() {
        return this.removeYamlSupport.getOrElse(false);
    }

    /**
     * Sets to {@code true} if Yaml support is removed from Spring Boot.
     * @param removeYamlSupport {@code true} if Yaml support is removed from Spring Boot.
     */
    public void setRemoveYamlSupport(final boolean removeYamlSupport) {
        this.removeYamlSupport.set(removeYamlSupport);
    }

    /**
     * Returns {@code true} if XML support is removed from Spring Boot, enabling faster compilation and smaller executables.
     * @return {@code true} if XML support is removed from Spring Boot.
     */
    public boolean getRemoveXmlSupport() {
        return this.removeXmlSupport.getOrElse(false);
    }

    /**
     * Sets to {@code true} if XML support is removed from Spring Boot.
     * @param removeXmlSupport {@code true} if XML support is removed from Spring Boot.
     */
    public void setRemoveXmlSupport(final boolean removeXmlSupport) {
        this.removeXmlSupport.set(removeXmlSupport);
    }

    /**
     * Returns {@code true} if SpEL support is removed from Spring Boot, enabling faster compilation and smaller executables.
     * @return {@code true} if SpEL support is removed from Spring Boot.
     */
    public boolean getRemoveSpelSupport() {
        return this.removeSpelSupport.getOrElse(false);
    }

    /**
     * Sets to {@code true} if SpEL support is removed from Spring Boot.
     * @param removeSpelSupport {@code true} if SpEL support is removed from Spring Boot.
     */
    public void setRemoveSpelSupport(final boolean removeSpelSupport) {
        this.removeSpelSupport.set(removeSpelSupport);
    }

    /**
     * Returns {@code true} if JMX support is removed from Spring Boot, enabling faster compilation and smaller executables.
     * @return {@code true} if JMX support is removed from Spring Boot.
     */
    public boolean getRemoveJmxSupport() {
        return this.removeJmxSupport.getOrElse(false);
    }

    /**
     * Sets to {@code true} if JMX support is removed from Spring Boot.
     * @param removeJmxSupport {@code true} if JMX support is removed from Spring Boot.
     */
    public void setRemoveJmxSupport(final boolean removeJmxSupport) {
        this.removeJmxSupport.set(removeJmxSupport);
    }

    /**
     * Returns {@code true} if the verifier mode is switched on.
     * @return {@code true} if the verifier mode is switched on.
     * @see <a href="https://repo.spring.io/milestone/org/springframework/experimental/spring-graalvm-native-docs/0.7.1/spring-graalvm-native-docs-0.7.1.zip!/reference/index.html#troubleshooting">Troubleshooting</a>
     */
    public boolean getVerify() {
        return this.verify.getOrElse(false);
    }

    /**
     * Sets to {@code true} if the verifier mode is switched on.
     * @param verify {@code true} if the verifier mode is switched on.
     */
    public void setVerify(final boolean verify) {
        this.verify.set(verify);
    }

    /**
     * Returns {@code true} if a lot of information about the feature behavior outputs is enabled as it processes auto-configuration and chooses which to include.
     * @return {@code true} if a lot of information about the feature behavior outputs is enabled as it processes auto-configuration and chooses which to include.
     */
    public boolean getSpringNativeVerbose() {
        return this.springNativeVerbose.getOrElse(false);
    }

    /**
     * Sets to {@code true} if a lot of information about the feature behavior outputs is enabled as it processes auto-configuration and chooses which to include.
     * @param springNativeVerbose {@code true} if a lot of information about the feature behavior outputs is enabled as it processes auto-configuration and chooses which to include.
     */
    public void setSpringNativeVerbose(final boolean springNativeVerbose) {
        this.springNativeVerbose.set(springNativeVerbose);
    }

    /**
     * Returns the mode that switches how much configuration the feature actually provides to native-image.
     * @return The mode that switches how much configuration the feature actually provides to native-image.
     * @see SpringNativeMode#AGENT
     * @see SpringNativeMode#FEATURE
     * @see SpringNativeMode#FUNCTIONAL
     */
    @Nonnull
    public String getSpringNativeMode() {
        return this.springNativeMode.getOrElse(SpringGraalNativeExtension.SPRING_NATIVE_MODE);
    }

    /**
     * Sets the mode that switches how much configuration the feature actually provides to native-image.
     * @param springNativeMode The mode that switches how much configuration the feature actually provides to native-image.
     */
    public void setSpringNativeMode(@Nonnull final String springNativeMode) {
        this.springNativeMode.set(springNativeMode);
    }

    /**
     * Returns the file path to dump the configuration to.
     * @return The file path to dump the configuration to.
     */
    @Nullable
    public String getDumpConfig() {
        return this.dumpConfig.getOrNull();
    }

    /**
     * Sets the file path to dump the configuration to.
     * @param dumpConfig The file path to dump the configuration to.
     */
    public void setDumpConfig(@Nullable final String dumpConfig) {
        this.dumpConfig.set(dumpConfig);
    }

    /**
     * Returns the fully qualified name of the Java class that contains the {@code main()} method as the entry point of the executable.
     * @return The fully qualified name of the Java class that contains the {@code main()} method as the entry point of the executable.
     */
    @Nullable
    public String getMainClassName() {
        return this.mainClassName.getOrNull();
    }

    /**
     * Sets the fully qualified name of the Java class that contains the {@code main()} method as the entry point of the executable.
     * @param mainClassName The fully qualified name of the Java class that contains the {@code main()} method as the entry point of the executable.
     */
    public void setMainClassName(@Nullable final String mainClassName) {
        this.mainClassName.set(mainClassName);
    }

    /**
     * Returns the maximum Java heap size allowed for building the native image.
     * @return The maximum Java heap size allowed for building the native image.
     */
    @Nullable
    public String getMaxHeapSize() {
        return this.maxHeapSize.getOrNull();
    }

    /**
     * Sets the maximum Java heap size allowed for building the native image.
     * @param maxHeapSize The maximum Java heap size allowed for building the native image.
     */
    public void setMaxHeapSize(@Nullable final String maxHeapSize) {
        this.maxHeapSize.set(maxHeapSize);
    }

    /**
     * Returns the classes to be initialized by default at build time.
     * @return The classes to be initialized by default at build time.
     */
    @Nullable
    public List<String> getInitializeAtBuildTime() {
        return this.initializeAtBuildTime.getOrNull();
    }

    /**
     * Sets the classes to be initialized by default at build time.
     * @param initializeAtBuildTime The classes to be initialized by default at build time.
     */
    public void setInitializeAtBuildTime(@Nullable final List<String> initializeAtBuildTime) {
        this.initializeAtBuildTime.set(initializeAtBuildTime);
    }

    //endregion
}
