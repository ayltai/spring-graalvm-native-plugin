package com.github.ayltai.gradle.plugin;

public final class SpringNativeMode {
    /**
     * This should be used if only wishing the feature to provide substitutions and initialization configuration. In this mode you should have used the agent to collect the rest of the configuration.
     */
    public static final String AGENT = "agent";

    /**
     * Provides everything (including deep analysis of auto-configuration).
     */
    public static final String FEATURE = "feature";

    /**
     * This should be used when working with functional bean registration (Spring Fu style). In this mode the feature will provide initialization and resource configuration but nothing more.
     */
    public static final String FUNCTIONAL = "functional";

    private SpringNativeMode() {
    }
}
