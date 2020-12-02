package com.github.ayltai.gradle.plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

import org.gradle.api.Task;
import org.gradle.api.internal.provider.MissingValueException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SpringGraalNativeTaskTests extends UnitTests {
    //region Constants

    private static final String CLASS_PATH      = "test";
    private static final String CLASSES_PATH    = "test1:test2";
    private static final String MAIN_CLASS_NAME = "main";

    //endregion

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
    public void testGetCommandLineArgs() {
        final SpringGraalNativeTask task = this.getTask();
        task.download.set(Constants.DOWNLOAD_SKIP);
        task.mainClassName.set(SpringGraalNativeTaskTests.MAIN_CLASS_NAME);

        List<String> args = StreamSupport.stream(task.getCommandLineArgs(SpringGraalNativeTaskTests.CLASS_PATH).spliterator(), false).collect(Collectors.toList());
        Assertions.assertEquals("native-image", args.get(0));
        Assertions.assertTrue(args.contains("-H:Name=" + this.project.getName()));
        Assertions.assertTrue(args.contains(SpringGraalNativeTaskTests.MAIN_CLASS_NAME));
        Assertions.assertFalse(args.contains("-Dspring.native.remove-yaml-support=true"));
        Assertions.assertFalse(args.contains("-Dspring.xml.ignore=true"));
        Assertions.assertFalse(args.contains("-Dspring.spel.ignore=true"));
        Assertions.assertFalse(args.contains("-Dspring.native.remove-jmx-support=true"));

        task.removeYamlSupport.set(true);
        task.removeXmlSupport.set(true);
        task.removeSpelSupport.set(true);
        task.removeJmxSupport.set(true);

        args = StreamSupport.stream(task.getCommandLineArgs(SpringGraalNativeTaskTests.CLASS_PATH).spliterator(), false).collect(Collectors.toList());
        Assertions.assertTrue(args.contains("-Dspring.native.remove-yaml-support=true"));
        Assertions.assertTrue(args.contains("-Dspring.xml.ignore=true"));
        Assertions.assertTrue(args.contains("-Dspring.spel.ignore=true"));
        Assertions.assertTrue(args.contains("-Dspring.native.remove-jmx-support=true"));
    }

    @Nonnull
    private <T extends Task> T getTask() {
        return (T)project.getTasks().getByName(SpringGraalNativePlugin.TASK_NAME);
    }
}
