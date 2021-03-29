package com.github.ayltai.gradle.plugin.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import javax.annotation.Nonnull;

import org.gradle.api.resources.ResourceException;

import com.github.ayltai.gradle.plugin.Constants;

import org.apache.commons.lang3.StringUtils;

public final class DownloadUtils {
    private static final String OUTPUT_PATH = "graalvm-ce-java%2$s-%1$s";

    private DownloadUtils() {
    }

    @Nonnull
    public static String getOutputPath(@Nonnull final String toolVersion, @Nonnull final String javaVersion) {
        final String outputPath = String.format(DownloadUtils.OUTPUT_PATH, toolVersion, javaVersion);
        return PlatformUtils.isMacOS() ? Paths.get(outputPath, "Contents", "Home").toString() : outputPath;
    }

    public static void download(@Nonnull final  String downloadUrl, @Nonnull final File outputDir, @Nonnull final String downloadStrategy) {
        if (!outputDir.exists() && !outputDir.mkdirs()) throw new ResourceException("Failed to create temporary directory: " + outputDir.getAbsolutePath());

        final File outputFile = new File(outputDir, downloadUrl.substring(downloadUrl.lastIndexOf('/') + 1));
        if (Constants.DOWNLOAD_DEFAULT.equals(downloadStrategy) && !outputFile.exists() || Constants.DOWNLOAD_ALWAYS.equals(downloadStrategy)) {
            try (
                ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(downloadUrl).openStream());
                FileOutputStream    outputStream        = new FileOutputStream(outputFile);
                FileChannel         fileChannel         = outputStream.getChannel()) {
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

                final boolean isWindows = PlatformUtils.isWindows();
                if (isWindows) {
                    ArchiveUtils.decompressZip(outputFile, outputDir);
                } else {
                    ArchiveUtils.decompressTarGZip(outputFile, outputDir);
                }

                final int result = new ProcessBuilder()
                    .command(Paths.get(outputDir.getAbsolutePath(), DownloadUtils.getOutputPath(StringUtils.substringBetween(downloadUrl, "/vm-", "/"), StringUtils.substringBetween(downloadUrl, "-java", "-")), "bin", "gu").toString(), "install", "native-image")
                    .start()
                    .waitFor();

                if (result != 0) throw new ResourceException("Failed to install GraalVM Native Image. Error code: " + result);
            } catch (final IOException | IllegalAccessException | InterruptedException e) {
                throw new ResourceException(e.getMessage(), e);
            }
        }
    }
}
