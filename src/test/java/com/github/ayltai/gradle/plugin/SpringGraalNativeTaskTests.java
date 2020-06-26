package com.github.ayltai.gradle.plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.internal.provider.MissingValueException;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder;
import org.gradle.testfixtures.ProjectBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public final class SpringGraalNativeTaskTests {
    //region Constants

    private static final String PROJECT_NAME    = "sample";
    private static final String CLASS_PATH      = "test";
    private static final String CLASSES_PATH    = "test1:test2";
    private static final String MAIN_CLASS_NAME = "main";

    //endregion

    private Project project;
    private File    jarFile;

    @BeforeEach
    public void setUp() throws IOException {
        final TemporaryFolder temporaryFolder = TemporaryFolder.builder().assureDeletion().build();
        temporaryFolder.create();

        this.project = ProjectBuilder.builder()
            .withName(SpringGraalNativeTaskTests.PROJECT_NAME)
            .withProjectDir(temporaryFolder.newFolder())
            .build();

        this.jarFile = new File(this.getClass()
            .getClassLoader()
            .getResource("spring-boot-0.0.1-SNAPSHOT.jar")
            .getFile());

        final RegularFile regularFile = Mockito.mock(RegularFile.class);
        Mockito.doReturn(this.jarFile).when(regularFile).getAsFile();

        final RegularFileProperty archiveFile = Mockito.mock(RegularFileProperty.class);
        Mockito.doReturn(regularFile).when(archiveFile).get();

        final Jar jar = Mockito.spy(this.project.getTasks().create(SpringGraalNativePlugin.DEPENDENT_TASK, Jar.class));
        Mockito.doReturn(archiveFile).when(jar).getArchiveFile();

        this.project.getPluginManager().apply("com.github.ayltai.spring-graalvm-native-plugin");
    }

    @Test
    public void testSetUp() {
        Assertions.assertTrue(this.getTask() instanceof SpringGraalNativeTask);
    }

    @Test
    public void testDeleteOutputDir() throws IOException {
        final File outputDir = this.getOutputDir();
        Assertions.assertFalse(outputDir.exists());

        final SpringGraalNativeTask task = this.getTask();
        task.deleteOutputDir(outputDir);
        Assertions.assertFalse(outputDir.exists());

        Assertions.assertTrue(outputDir.mkdirs());
        Assertions.assertTrue(outputDir.exists());

        task.deleteOutputDir(outputDir);
        Assertions.assertFalse(outputDir.exists());
    }

    @Test
    public void testGetClassPath() {
        final SpringGraalNativeTask task      = this.getTask();
        final String                classPath = task.getClassPath(SpringGraalNativeTaskTests.CLASSES_PATH, this.getOutputDir());

        Assertions.assertEquals(SpringGraalNativeTaskTests.CLASSES_PATH, classPath);
    }

    @Test
    public void given_missingMainClassName_when_getCommandLineArgsIsCalled_then_throwsMissingValueException() {
        Assertions.assertThrows(MissingValueException.class, () -> ((SpringGraalNativeTask)this.getTask()).getCommandLineArgs(SpringGraalNativeTaskTests.CLASS_PATH));
    }

    @Test
    public void testExplodeJar() throws IOException {
        Assertions.assertFalse(this.getOutputDir().exists());

        final SpringGraalNativeTask task = this.getTask();
        task.explodeJar(this.jarFile, this.getOutputDir());

        Assertions.assertTrue(this.getOutputDir().exists());
        Assertions.assertTrue(new File(new File(this.getOutputDir(), "BOOT-INF"), "classes").exists());
        Assertions.assertTrue(new File(new File(this.getOutputDir(), "BOOT-INF"), "lib").exists());
        Assertions.assertTrue(new File(new File(this.getOutputDir(), "META-INF"), "MANIFEST.MF").exists());
        Assertions.assertTrue(new File(new File(this.getOutputDir(), "org"), "springframework").exists());
    }

    @Test
    public void testGetCommandLineArgs() {
        final SpringGraalNativeTask task = this.getTask();
        task.mainClassName.set(SpringGraalNativeTaskTests.MAIN_CLASS_NAME);

        List<String> args = StreamSupport.stream(task.getCommandLineArgs(SpringGraalNativeTaskTests.CLASS_PATH).spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals("native-image", args.get(0));
        Assertions.assertTrue(args.contains("-H:Name=" + this.project.getName()));
        Assertions.assertTrue(args.contains(SpringGraalNativeTaskTests.MAIN_CLASS_NAME));
        Assertions.assertFalse(args.contains("-Dspring.native.remove-yaml-support=true"));
        Assertions.assertFalse(args.contains("-Dspring.native.remove-xml-support=true"));
        Assertions.assertFalse(args.contains("-Dspring.native.remove-spel-support=true"));
        Assertions.assertFalse(args.contains("-Dspring.native.remove-jmx-support=true"));

        task.removeYamlSupport.set(true);
        task.removeXmlSupport.set(true);
        task.removeSpelSupport.set(true);
        task.removeJmxSupport.set(true);

        args = StreamSupport.stream(task.getCommandLineArgs(SpringGraalNativeTaskTests.CLASS_PATH).spliterator(), false).collect(Collectors.toList());
        Assertions.assertTrue(args.contains("-Dspring.native.remove-yaml-support=true"));
        Assertions.assertTrue(args.contains("-Dspring.native.remove-xml-support=true"));
        Assertions.assertTrue(args.contains("-Dspring.native.remove-spel-support=true"));
        Assertions.assertTrue(args.contains("-Dspring.native.remove-jmx-support=true"));
    }

    @Nonnull
    private File getOutputDir() {
        return new File(this.project.getBuildDir().getAbsolutePath(), SpringGraalNativeTask.DIR_OUTPUT);
    }

    @Nonnull
    private <T extends Task> T getTask() {
        return (T)project.getTasks().getByName(SpringGraalNativePlugin.CURRENT_TASK);
    }
}
