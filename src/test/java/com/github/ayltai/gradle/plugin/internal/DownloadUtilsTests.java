package com.github.ayltai.gradle.plugin.internal;

import java.io.File;
import java.nio.file.Paths;
import javax.annotation.Nonnull;

import com.github.ayltai.gradle.plugin.Constants;
import com.github.ayltai.gradle.plugin.UnitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class DownloadUtilsTests extends UnitTests {
    private static final String DOWNLOAD_URL = "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-%1$s/graalvm-ce-java%2$s-%3$s-%4$s-%1$s.%5$s";

    @Test
    public void testDownload() {
        final String downloadUrl = this.getDownloadUrl();
        final File   outputFile  = Paths.get(this.getOutputDir().getAbsolutePath(), DownloadUtils.getOutputPath("20.1.0", "8")).toFile();

        Assertions.assertFalse(outputFile.exists());

        DownloadUtils.download(downloadUrl, this.getOutputDir(), Constants.DOWNLOAD_ALWAYS);

        Assertions.assertTrue(outputFile.exists());
    }

    @Nonnull
    protected String getDownloadUrl() {
        final String platform = PlatformUtils.getPlatform();
        return String.format(DownloadUtilsTests.DOWNLOAD_URL, Constants.DEFAULT_TOOL_VERSION, Constants.DEFAULT_JAVA_VERSION, platform, PlatformUtils.getArchitecture(), "windows".equals(platform) ? "zip" : "tar.gz");
    }
}
