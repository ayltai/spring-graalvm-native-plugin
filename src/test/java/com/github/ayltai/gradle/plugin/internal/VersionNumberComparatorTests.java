package com.github.ayltai.gradle.plugin.internal;

import com.github.ayltai.gradle.plugin.UnitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class VersionNumberComparatorTests extends UnitTests {
    @Test
    public void testCompare() {
        Assertions.assertTrue(VersionNumberComparator.getInstance().compare("20.2.0", "20.3.0") < 0);
        Assertions.assertEquals(0, VersionNumberComparator.getInstance().compare("20.1.0", "20.1.0"));
        Assertions.assertTrue(VersionNumberComparator.getInstance().compare("19.2.0-dev-b01", "19.1.1") > 0);
    }
}
