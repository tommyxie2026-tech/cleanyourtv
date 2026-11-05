package com.gathin.tvhome;

import static org.junit.Assert.*;
import org.junit.Test;

public class XgimiH2InputsActivityTest {
    @Test public void exposesOnlyTheTwoPhysicalHdmiPorts() {
        assertEquals(23, XgimiH2InputsActivity.sourceForPort(1));
        assertEquals(24, XgimiH2InputsActivity.sourceForPort(2));
    }

    @Test public void rejectsUnconnectedVendorInputsInsteadOfSwitchingThem() {
        for (int port : new int[] {-1, 0, 3, 4, Integer.MAX_VALUE}) {
            try {
                XgimiH2InputsActivity.sourceForPort(port);
                fail("Unsupported port " + port);
            } catch (IllegalArgumentException expected) { }
        }
    }
}
