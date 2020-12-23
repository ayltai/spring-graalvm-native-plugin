package com.github.ayltai.gradle.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.resources.ResourceException;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.internal.os.OperatingSystem;

import com.github.ayltai.gradle.plugin.internal.ArchiveUtils;
import com.github.ayltai.gradle.plugin.internal.DownloadUtils;
import com.github.ayltai.gradle.plugin.internal.PlatformUtils;
import com.github.ayltai.gradle.plugin.internal.VersionNumberComparator;

import org.slf4j.LoggerFactory;

public class SpringGraalNativeTask extends Exec {
    private static final Logger LOGGER = (Logger)LoggerFactory.getLogger(SpringGraalNativeTask.class);

    //region Constants

    protected static final String DIR_OUTPUT = "native";

    private static final String DIR_BOOT_INF  = "BOOT-INF";
    private static final String DIR_META_INF  = "META-INF";
    private static final String FILE_MANIFEST = "MANIFEST.MF";

    private static final String DOWNLOAD_URL = "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-%1$s/graalvm-ce-java%2$s-%3$s-%4$s-%1$s.%5$s";

    //endregion

    //region Properties

    protected final Property<String>     toolVersion;
    protected final Property<String>     javaVersion;
    protected final Property<String>     download;
    protected final Property<Boolean>    traceClassInitialization;
    protected final Property<Boolean>    traceClassInitializationEnabled;
    protected final ListProperty<String> traceClassInitializationFor;
    protected final Property<Boolean>    removeSaturatedTypeFlows;
    protected final Property<Boolean>    reportExceptionStackTraces;
    protected final Property<Boolean>    printAnalysisCallTree;
    protected final Property<Boolean>    disableToolchainChecking;
    protected final Property<Boolean>    enableAllSecurityServices;
    protected final Property<Boolean>    enableHttp;
    protected final Property<Boolean>    enableHttps;
    protected final ListProperty<String> enableUrlProtocols;
    protected final Property<Boolean>    staticallyLinked;
    protected final Property<Boolean>    verbose;
    protected final Property<Boolean>    warnMissingSelectorHints;
    protected final Property<Boolean>    removeUnusedAutoConfig;
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

    @Inject
    public SpringGraalNativeTask(@Nonnull final ObjectFactory factory) {
        this.toolVersion                     = factory.property(String.class);
        this.javaVersion                     = factory.property(String.class);
        this.download                        = factory.property(String.class);
        this.traceClassInitialization        = factory.property(Boolean.class);
        this.traceClassInitializationEnabled = factory.property(Boolean.class);
        this.traceClassInitializationFor     = factory.listProperty(String.class);
        this.removeSaturatedTypeFlows        = factory.property(Boolean.class);
        this.reportExceptionStackTraces      = factory.property(Boolean.class);
        this.printAnalysisCallTree           = factory.property(Boolean.class);
        this.disableToolchainChecking        = factory.property(Boolean.class);
        this.enableAllSecurityServices       = factory.property(Boolean.class);
        this.enableHttp                      = factory.property(Boolean.class);
        this.enableHttps                     = factory.property(Boolean.class);
        this.enableUrlProtocols              = factory.listProperty(String.class);
        this.staticallyLinked                = factory.property(Boolean.class);
        this.verbose                         = factory.property(Boolean.class);
        this.warnMissingSelectorHints        = factory.property(Boolean.class);
        this.removeUnusedAutoConfig          = factory.property(Boolean.class);
        this.removeYamlSupport               = factory.property(Boolean.class);
        this.removeXmlSupport                = factory.property(Boolean.class);
        this.removeSpelSupport               = factory.property(Boolean.class);
        this.removeJmxSupport                = factory.property(Boolean.class);
        this.verify                          = factory.property(Boolean.class);
        this.springNativeVerbose             = factory.property(Boolean.class);
        this.springNativeMode                = factory.property(String.class);
        this.dumpConfig                      = factory.property(String.class);
        this.mainClassName                   = factory.property(String.class);
        this.maxHeapSize                     = factory.property(String.class);
        this.initializeAtBuildTime           = factory.listProperty(String.class);

        this.setGroup("build");
        this.setDescription("Builds a native image for Spring Boot applications using GraalVM tools");
    }

    @Nonnull
    @Internal
    protected String getDownloadUrl() {
        final String platform = PlatformUtils.getPlatform();
        return String.format(SpringGraalNativeTask.DOWNLOAD_URL, this.toolVersion.getOrElse(Constants.DEFAULT_TOOL_VERSION), this.javaVersion.getOrElse(Constants.DEFAULT_JAVA_VERSION), platform, PlatformUtils.getArchitecture(), "windows".equals(platform) ? "zip" : "tar.gz");
    }

    @Nonnull
    @Internal
    protected File getToolsDir() {
        return Paths.get(this.getProject().getBuildDir().getAbsolutePath(), "tmp", SpringGraalNativePlugin.TASK_NAME, DownloadUtils.getOutputPath(this.toolVersion.getOrElse(Constants.DEFAULT_TOOL_VERSION), this.javaVersion.getOrElse(Constants.DEFAULT_JAVA_VERSION))).toFile();
    }

    @Nonnull
    protected String getClassPath(@Nonnull final String classesPath, @Nonnull final File outputDir) {
        final File[] files = Paths.get(outputDir.getAbsolutePath(), SpringGraalNativeTask.DIR_BOOT_INF, "lib").toFile().listFiles();
        return files == null ? classesPath : classesPath + (OperatingSystem.current().isWindows() ? ";" : ":") + Stream.of(files)
            .map(File::getAbsolutePath)
            .collect(Collectors.joining(OperatingSystem.current().isWindows() ? ";" : ":"));
    }

    @Nonnull
    protected Iterable<String> getCommandLineArgs(@Nonnull final String classPath) {
        final List<String> args = new ArrayList<>();

        if (Constants.DOWNLOAD_SKIP.equals(this.download.getOrElse(Constants.DOWNLOAD_DEFAULT))) {
            args.add(PlatformUtils.isWindows() ? "native-image.cmd" : "native-image");
        } else {
            args.add(Paths.get(this.getToolsDir().getAbsolutePath(), "bin", PlatformUtils.isWindows() ? "native-image.cmd" : "native-image").toString());
        }

        args.add("--allow-incomplete-classpath");
        args.add("--report-unsupported-elements-at-runtime");
        args.add("--no-fallback");
        args.add("--no-server");
        args.add("--install-exit-handlers");

        if (VersionNumberComparator.getInstance().compare(this.toolVersion.getOrElse(Constants.DEFAULT_TOOL_VERSION), Constants.DEFAULT_TOOL_VERSION) < 0) {
            SpringGraalNativeTask.appendCommandLineArg(args, "-H:+TraceClassInitialization", this.traceClassInitializationEnabled.getOrNull() == null ? this.traceClassInitialization : this.traceClassInitializationEnabled);
        } else {
            if (this.traceClassInitializationFor.isPresent() && !this.traceClassInitializationFor.get().isEmpty()) args.add("--trace-class-initialization=" + String.join(",", this.traceClassInitializationFor.get()));
        }

        SpringGraalNativeTask.appendCommandLineArg(args, "-H:+RemoveSaturatedTypeFlows", this.removeSaturatedTypeFlows);
        SpringGraalNativeTask.appendCommandLineArg(args, "-H:+ReportExceptionStackTraces", this.reportExceptionStackTraces);
        SpringGraalNativeTask.appendCommandLineArg(args, "-H:+PrintAnalysisCallTree", this.printAnalysisCallTree);
        SpringGraalNativeTask.appendCommandLineArg(args, "-H:-CheckToolchain", this.disableToolchainChecking);
        SpringGraalNativeTask.appendCommandLineArg(args, "--enable-all-security-services", this.enableAllSecurityServices);
        SpringGraalNativeTask.appendCommandLineArg(args, "--enable-http", this.enableHttp);
        SpringGraalNativeTask.appendCommandLineArg(args, "--enable-https", this.enableHttps);
        SpringGraalNativeTask.appendCommandLineArg(args, "--static", this.staticallyLinked);
        SpringGraalNativeTask.appendCommandLineArg(args, "--verbose", this.verbose);
        SpringGraalNativeTask.appendCommandLineArg(args, "-Dspring.native.missing-selector-hints=warning", this.warnMissingSelectorHints);
        SpringGraalNativeTask.appendCommandLineArg(args, "-Dspring.native.remove-unused-autoconfig=true", this.removeUnusedAutoConfig);

        if (this.enableUrlProtocols.isPresent() && !this.enableUrlProtocols.get().isEmpty()) args.add("--enable-url-protocols=" + String.join(",", this.enableUrlProtocols.get()));
        if (this.removeYamlSupport.isPresent()) args.add("-Dspring.native.remove-yaml-support=" + this.removeYamlSupport.get());
        if (this.removeXmlSupport.isPresent()) args.add("-Dspring.xml.ignore=" + this.removeXmlSupport.get());
        if (this.removeSpelSupport.isPresent()) args.add("-Dspring.spel.ignore=" + this.removeSpelSupport.get());
        if (this.removeJmxSupport.isPresent()) args.add("-Dspring.native.remove-jmx-support=" + this.removeJmxSupport.get());
        if (this.verify.isPresent()) args.add("-Dspring.native.verify=" + this.verify.get());
        if (this.springNativeVerbose.isPresent()) args.add("-Dspring.native.verbose=" + this.springNativeVerbose.get());
        if (this.springNativeMode.isPresent()) args.add("-Dspring.native.mode=" + this.springNativeMode.get());
        if (this.maxHeapSize.isPresent() && !this.maxHeapSize.get().isEmpty()) args.add("-J-Xmx" + this.maxHeapSize.get());
        if (this.initializeAtBuildTime.isPresent() && !this.initializeAtBuildTime.get().isEmpty()) args.add("--initialize-at-build-time=" + String.join(",", this.initializeAtBuildTime.get()));

        args.add("-H:Name=" + this.getProject().getName());
        args.add("-cp");
        args.add(classPath);
        args.add(this.mainClassName.get());

        if (SpringGraalNativeTask.LOGGER.isEnabled(LogLevel.DEBUG)) SpringGraalNativeTask.LOGGER.debug(String.join(" ", args));

        return args;
    }

    protected static void appendCommandLineArg(@Nonnull final List<String> args, @Nonnull final String arg, @Nonnull final Property<Boolean> property) {
        if (Boolean.TRUE.equals(property.getOrNull())) args.add(arg);
    }

    @TaskAction
    @Override
    protected void exec() {
        if (!this.mainClassName.isPresent()) throw new InvalidUserDataException("mainClassName is null");

        DownloadUtils.download(this.getDownloadUrl(), Paths.get(this.getProject().getBuildDir().getAbsolutePath(), "tmp", SpringGraalNativePlugin.TASK_NAME).toFile(), this.download.getOrElse(Constants.DOWNLOAD_DEFAULT));

        final File outputDir = new File(this.getProject().getBuildDir().getAbsolutePath(), SpringGraalNativeTask.DIR_OUTPUT);

        try {
            final Path classesPath = Paths.get(outputDir.getAbsolutePath(), SpringGraalNativeTask.DIR_BOOT_INF, "classes");

            this.deleteOutputDir(outputDir);
            this.copyFiles(classesPath, outputDir);

            this.workingDir(outputDir)
                .commandLine(this.getCommandLineArgs(this.getClassPath(classesPath.toString(), outputDir)));

            super.exec();
        } catch (final IOException e) {
            throw new ResourceException(e.getMessage(), e);
        }
    }

    protected void deleteOutputDir(@Nonnull final File outputDir) throws IOException {
        if (outputDir.exists()) {
            SpringGraalNativeTask.LOGGER.info("Clear output directory");

            try (Stream<Path> stream = Files.walk(outputDir.toPath())) {
                stream.map(Path::toFile)
                    .sorted(Comparator.reverseOrder())
                    .forEach(file -> {
                        try {
                            Files.deleteIfExists(file.toPath());
                        } catch (final IOException e) {
                            throw new ResourceException("Failed to delete directory or file: " + file.getAbsolutePath(), e);
                        }
                    });
            }
        } else {
            SpringGraalNativeTask.LOGGER.info("Skip clearing output directory as it does not exist");
        }
    }

    protected void copyFiles(@Nonnull final Path classesPath, @Nonnull final File outputDir) {
        try {
            ArchiveUtils.decompressJar(((Jar)SpringGraalNativePlugin.getDependency(this.getProject())).getArchiveFile().get().getAsFile(), outputDir);

            SpringGraalNativeTask.LOGGER.info("Copy dependencies to output directory");

            final File destination = new File(classesPath.toString(), SpringGraalNativeTask.DIR_META_INF);
            if (!destination.exists() && !destination.mkdirs()) throw new ResourceException("Failed to create directory: " + destination.getAbsolutePath());

            Files.copy(Paths.get(outputDir.getAbsolutePath(), SpringGraalNativeTask.DIR_META_INF, SpringGraalNativeTask.FILE_MANIFEST), Paths.get(classesPath.toString(), SpringGraalNativeTask.DIR_META_INF, SpringGraalNativeTask.FILE_MANIFEST));
        } catch (final IOException | IllegalAccessException e) {
            throw new ResourceException(e.getMessage(), e);
        }
    }
}
