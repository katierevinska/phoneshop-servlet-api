package com.es.phoneshop.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultDosProtectionServiceTest {
    private final DefaultDosProtectionService dosProtectionService = DefaultDosProtectionService.getInstance();

    @Test
    void testIsAllowedWhenNoPreviousRequests() {
        assertTrue(dosProtectionService.isAllowed( "192.168.1.1"));
    }

    @Test
    void testIsNotAllowedExceedingLimit() {
        String ip = "192.168.1.3";
        for (int i = 1; i <= 19; i++) {
            dosProtectionService.isAllowed(ip);
        }
        assertTrue(dosProtectionService.isAllowed(ip));
        assertFalse(dosProtectionService.isAllowed(ip));
    }

//    @Test
//    void testIsAllowedAfterMinutePasses() throws InterruptedException {
//        String ip = "192.168.1.4";
//        for (int i = 0; i < 20; i++) {
//            dosProtectionService.isAllowed(ip);
//        }
//
//        Thread.sleep(60000);
//
//        assertTrue(dosProtectionService.isAllowed(ip));
//    }

    @Test
    void testIsAllowedWithDifferentIPs() {
        String ip1 = "192.168.2.1";
        String ip2 = "192.168.3.2";

        for (int i = 0; i < 20; i++) {
            assertTrue(dosProtectionService.isAllowed(ip1));
            assertTrue(dosProtectionService.isAllowed(ip2));
        }
        assertFalse(dosProtectionService.isAllowed(ip1));
        assertFalse(dosProtectionService.isAllowed(ip2));
    }
}