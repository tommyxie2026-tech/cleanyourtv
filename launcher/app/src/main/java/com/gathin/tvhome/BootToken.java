package com.gathin.tvhome;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Android 6 has no BOOT_COUNT. The kernel UUID changes at boot, including across wall-clock changes. */
final class BootToken {
    private BootToken() { }

    static int read(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.US_ASCII))) {
            char[] value = new char[37];
            int length = reader.read(value);
            if (length < 36) return -1;
            String id = new String(value, 0, length).trim();
            UUID parsed = UUID.fromString(id);
            if (!parsed.toString().equalsIgnoreCase(id)) return -1;
            return parsed.hashCode() & Integer.MAX_VALUE;
        } catch (Exception unavailable) {
            return -1;
        }
    }
}
