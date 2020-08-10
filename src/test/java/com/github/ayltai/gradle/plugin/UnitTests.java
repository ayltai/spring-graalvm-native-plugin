package com.github.ayltai.gradle.plugin;

import java.io.File;
import java.io.IOException;
import javax.annotation.Nonnull;

import org.gradle.api.Project;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder;
import org.gradle.testfixtures.ProjectBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

public abstract class UnitTests {
    private static final String PROJECT_NAME = "sample";

    protected Project project;
    protected File    jarFile;

    @BeforeEach
    public void setUp() throws IOException {
        final TemporaryFolder temporaryFolder = TemporaryFolder.builder().assureDeletion().build();
        temporaryFolder.create();

        this.project = ProjectBuilder.builder()
            .withName(UnitTests.PROJECT_NAME)
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

    @Nonnull
    protected File getOutputDir() {
        return new File(this.project.getBuildDir().getAbsolutePath(), SpringGraalNativeTask.DIR_OUTPUT);
    }
}
