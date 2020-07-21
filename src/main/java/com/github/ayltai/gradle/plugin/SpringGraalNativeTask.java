package com.github.ayltai.gradle.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.resources.ResourceException;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.internal.os.OperatingSystem;

import org.slf4j.LoggerFactory;

public class SpringGraalNativeTask extends Exec {
    private static final Logger LOGGER = (Logger)LoggerFactory.getLogger(SpringGraalNativeTask.class);

    //region Constants

    protected static final String DIR_OUTPUT = "native";

    private static final String DIR_BOOT_INF  = "BOOT-INF";
    private static final String DIR_META_INF  = "META-INF";
    private static final String FILE_MANIFEST = "MANIFEST.MF";

    private static final int BUFFER_SIZE = 4096;

    //endregion

    //region Properties

    protected final Property<Boolean>    traceClassInitialization;
    protected final Property<Boolean>    removeSaturatedTypeFlows;
    protected final Property<Boolean>    reportExceptionStackTraces;
    protected final Property<Boolean>    printAnalysisCallTree;
    protected final Property<Boolean>    enabledAllSecurityServices;
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

    public SpringGraalNativeTask() {
        this.traceClassInitialization   = this.getProject().getObjects().property(Boolean.class);
        this.removeSaturatedTypeFlows   = this.getProject().getObjects().property(Boolean.class);
        this.reportExceptionStackTraces = this.getProject().getObjects().property(Boolean.class);
        this.printAnalysisCallTree      = this.getProject().getObjects().property(Boolean.class);
        this.enabledAllSecurityServices = this.getProject().getObjects().property(Boolean.class);
        this.staticallyLinked           = this.getProject().getObjects().property(Boolean.class);
        this.verbose                    = this.getProject().getObjects().property(Boolean.class);
        this.warnMissingSelectorHints   = this.getProject().getObjects().property(Boolean.class);
        this.removeUnusedAutoConfig     = this.getProject().getObjects().property(Boolean.class);
        this.removeYamlSupport          = this.getProject().getObjects().property(Boolean.class);
        this.removeXmlSupport           = this.getProject().getObjects().property(Boolean.class);
        this.removeSpelSupport          = this.getProject().getObjects().property(Boolean.class);
        this.removeJmxSupport           = this.getProject().getObjects().property(Boolean.class);
        this.verify                     = this.getProject().getObjects().property(Boolean.class);
        this.springNativeVerbose        = this.getProject().getObjects().property(Boolean.class);
        this.springNativeMode           = this.getProject().getObjects().property(String.class);
        this.dumpConfig                 = this.getProject().getObjects().property(String.class);
        this.mainClassName              = this.getProject().getObjects().property(String.class);
        this.maxHeapSize                = this.getProject().getObjects().property(String.class);
        this.initializeAtBuildTime      = this.getProject().getObjects().listProperty(String.class);

        this.setGroup("build");
        this.setDescription("Support for building Spring Boot applications as GraalVM native images");
    }

    @Nonnull
    protected String getClassPath(@Nonnull final String classesPath, @Nonnull final File outputDir) {
        final File[] files = Paths.get(outputDir.getAbsolutePath(), SpringGraalNativeTask.DIR_BOOT_INF, "lib").toFile().listFiles();
        return files == null ? classesPath : classesPath + ":" + Stream.of(files)
            .map(File::getAbsolutePath)
            .collect(Collectors.joining(":"));
    }

    @Nonnull
    protected Iterable<String> getCommandLineArgs(@Nonnull final String classPath) {
        final List<String> args = new ArrayList<>();
        args.add(OperatingSystem.current().isWindows() ? "native-image.cmd" : "native-image");
        args.add("--allow-incomplete-classpath");
        args.add("--report-unsupported-elements-at-runtime");
        args.add("--no-fallback");
        args.add("--no-server");
        args.add("--install-exit-handlers");

        SpringGraalNativeTask.appendCommandLineArg(args, "-H:+TraceClassInitialization", this.traceClassInitialization);
        SpringGraalNativeTask.appendCommandLineArg(args, "-H:+RemoveSaturatedTypeFlows", this.removeSaturatedTypeFlows);
        SpringGraalNativeTask.appendCommandLineArg(args, "-H:+ReportExceptionStackTraces", this.reportExceptionStackTraces);
        SpringGraalNativeTask.appendCommandLineArg(args, "-H:+PrintAnalysisCallTree", this.printAnalysisCallTree);
        SpringGraalNativeTask.appendCommandLineArg(args, "--enable-all-security-services", this.enabledAllSecurityServices);
        SpringGraalNativeTask.appendCommandLineArg(args, "--static", this.staticallyLinked);
        SpringGraalNativeTask.appendCommandLineArg(args, "--verbose", this.verbose);
        SpringGraalNativeTask.appendCommandLineArg(args, "-Dspring.native.missing-selector-hints=warning", this.warnMissingSelectorHints);
        SpringGraalNativeTask.appendCommandLineArg(args, "-Dspring.native.remove-unused-autoconfig=true", this.removeUnusedAutoConfig);

        if (this.removeYamlSupport.isPresent()) args.add("-Dspring.native.remove-yaml-support=" + this.removeYamlSupport.get());
        if (this.removeXmlSupport.isPresent()) args.add("-Dspring.native.remove-xml-support=" + this.removeXmlSupport.get());
        if (this.removeSpelSupport.isPresent()) args.add("-Dspring.native.remove-spel-support=" + this.removeSpelSupport.get());
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
            this.explodeJar(((Jar)SpringGraalNativePlugin.getDependency(this.getProject())).getArchiveFile().get().getAsFile(), outputDir);

            SpringGraalNativeTask.LOGGER.info("Copy dependencies to output directory");

            final File destination = new File(classesPath.toString(), SpringGraalNativeTask.DIR_META_INF);
            if (!destination.exists() && !destination.mkdirs()) throw new ResourceException("Failed to create directory: " + destination.getAbsolutePath());

            Files.copy(Paths.get(outputDir.getAbsolutePath(), SpringGraalNativeTask.DIR_META_INF, SpringGraalNativeTask.FILE_MANIFEST), Paths.get(classesPath.toString(), SpringGraalNativeTask.DIR_META_INF, SpringGraalNativeTask.FILE_MANIFEST));
        } catch (final IOException e) {
            throw new ResourceException(e.getMessage(), e);
        }
    }

    protected void explodeJar(@Nonnull final File archive, @Nonnull final File outputDir) throws IOException {
        SpringGraalNativeTask.LOGGER.info("Decompress dependencies");

        try (JarFile jarFile = new JarFile(archive)) {
            jarFile.stream()
                .sorted((entry1, entry2) -> (entry1.isDirectory() ? -1 : 0) + (entry2.isDirectory() ? 1 : 0))
                .forEachOrdered(entry -> {
                    try {
                        this.explodeJarEntry(outputDir, jarFile, entry);
                    } catch (final IOException e) {
                        throw new ResourceException("Failed to decompress JAR entry: " + entry.getName(), e);
                    }
                });
        }
    }

    protected void explodeJarEntry(@Nonnull final File outputDir, @Nonnull final JarFile jarFile, @Nonnull final JarEntry entry) throws IOException {
        final File file = new File(outputDir, entry.getName());

        if (entry.isDirectory()) {
            if (!file.mkdirs()) throw new IOException("Failed to create folder(s): " + file.getAbsolutePath());
        } else {
            final byte[] buffer = new byte[SpringGraalNativeTask.BUFFER_SIZE];

            try (
                InputStream inputStream   = new BufferedInputStream(jarFile.getInputStream(entry));
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                int length;
                while ((length = inputStream.read(buffer)) >= 0) outputStream.write(buffer, 0, length);
            }
        }
    }
}
