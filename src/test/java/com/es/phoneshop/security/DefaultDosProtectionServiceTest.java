package com.es.phoneshop.security;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.IntStream;

public class DefaultDosProtectionServiceTest {
    private static final int THRESHOLD = 20;
    private final DosProtectionService dosProtectionService = DefaultDosProtectionService.getInstance();

    @Test
    public void testIsAllowedAccepts() {
        Assert.assertTrue(dosProtectionService.isAllowed("127.0.0.1"));
    }

    @Test
    public void testIsAllowedRejects() {
        IntStream.range(0, THRESHOLD).forEach(i -> dosProtectionService.isAllowed("127.0.0.2"));
        Assert.assertFalse(dosProtectionService.isAllowed("127.0.0.2"));
    }
}