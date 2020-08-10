package com.github.ayltai.gradle.plugin.internal;

import java.io.File;
import java.io.IOException;

import com.github.ayltai.gradle.plugin.UnitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ArchiveUtilsTests extends UnitTests {
    @Test
    public void testDecompressJar() throws IOException, IllegalAccessException {
        Assertions.assertFalse(this.getOutputDir().exists());

        ArchiveUtils.decompressJar(this.jarFile, this.getOutputDir());

        Assertions.assertTrue(this.getOutputDir().exists());
        Assertions.assertTrue(new File(new File(this.getOutputDir(), "BOOT-INF"), "classes").exists());
        Assertions.assertTrue(new File(new File(this.getOutputDir(), "BOOT-INF"), "lib").exists());
        Assertions.assertTrue(new File(new File(this.getOutputDir(), "META-INF"), "MANIFEST.MF").exists());
        Assertions.assertTrue(new File(new File(this.getOutputDir(), "org"), "springframework").exists());
    }
}
