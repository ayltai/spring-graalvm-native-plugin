package com.github.ayltai.gradle.plugin.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import javax.annotation.Nonnull;

import org.gradle.api.resources.ResourceException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public final class ArchiveUtils {
    private ArchiveUtils() {
    }

    public static void decompressTarGZip(@Nonnull final File archive, @Nonnull final File outputDir) throws IOException, IllegalAccessException {
        try (ArchiveInputStream inputStream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(archive)), true))) {
            ArchiveUtils.decompress(inputStream, outputDir);
        }
    }

    public static void decompressZip(@Nonnull final File archive, @Nonnull final File outputDir) throws IOException, IllegalAccessException {
        try (ArchiveInputStream inputStream = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(archive)))) {
            ArchiveUtils.decompress(inputStream, outputDir);
        }
    }

    public static void decompressJar(@Nonnull final File archive, @Nonnull final File outputDir) throws IOException, IllegalAccessException {
        try (ArchiveInputStream inputStream = new JarArchiveInputStream(new BufferedInputStream(new FileInputStream(archive)))) {
            ArchiveUtils.decompress(inputStream, outputDir);
        }
    }

    public static void decompress(@Nonnull final ArchiveInputStream inputStream, @Nonnull final File outputDir) throws IOException, IllegalAccessException {
        ArchiveEntry entry;
        while ((entry = inputStream.getNextEntry()) != null) {
            final File destination = new File(outputDir, entry.getName());
            if (!destination.getCanonicalPath().startsWith(outputDir.getCanonicalPath() + File.separator)) throw new IllegalAccessException("Archive entry is outside of the output directory: " + entry.getName());

            if (!entry.isDirectory()) {
                final File parent = destination.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) throw new ResourceException("Failed to create output directory: " + parent.getAbsolutePath());

                if (entry instanceof TarArchiveEntry) {
                    final TarArchiveEntry tarArchiveEntry = (TarArchiveEntry)entry;

                    if (tarArchiveEntry.isSymbolicLink()) {
                        Files.createSymbolicLink(FileSystems.getDefault().getPath(destination.getAbsolutePath()), FileSystems.getDefault().getPath(tarArchiveEntry.getLinkName()));

                        continue;
                    }
                }

                try (OutputStream outputStream = new FileOutputStream(destination)) {
                    IOUtils.copy(inputStream, outputStream);

                    if ("bin".equals(parent.getName()) && !destination.setExecutable(true)) throw new ResourceException("Failed to set executable permission: " + destination.getAbsolutePath());
                }
            }
        }
    }
}
