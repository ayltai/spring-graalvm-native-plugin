package com.github.ayltai.gradle.plugin.internal;

import javax.annotation.Nonnull;

public final class PlatformUtils {
    private static final String ARCH_AMD64 = "amd64";
    private static final String ARCH_ARM   = "arm";
    private static final String WINDOWS    = "windows";
    private static final String MACOS      = "darwin";

    private PlatformUtils() {
    }

    @Nonnull
    public static String getPlatform() {
        final String osName = System.getProperty("os.name");
        return osName.startsWith("Windows") ? PlatformUtils.WINDOWS : osName.startsWith("Mac") ? "darwin" : osName.startsWith("FreeBSD") ? "freebsd" : osName.startsWith("SunOS") ? "solaris" : "linux";
    }

    @Nonnull
    public static String getArchitecture() {
        final String osArch = System.getProperty("os.arch");
        return "x86".equals(osArch) || "i386".equals(osArch) ? "386" : "x86_64".equals(osArch) || PlatformUtils.ARCH_AMD64.equals(osArch) ? PlatformUtils.ARCH_AMD64 : osArch.startsWith(PlatformUtils.ARCH_ARM) ? PlatformUtils.ARCH_ARM : PlatformUtils.ARCH_AMD64;
    }

    public static boolean isWindows() {
        return PlatformUtils.WINDOWS.equals(PlatformUtils.getPlatform());
    }

    public static boolean isMacOS() {
        return PlatformUtils.MACOS.equals(PlatformUtils.getPlatform());
    }
}
