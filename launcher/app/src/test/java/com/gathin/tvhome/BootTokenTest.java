package com.gathin.tvhome;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BootTokenTest {
    @Rule public TemporaryFolder temporary = new TemporaryFolder();

    @Test public void tokenStaysStableDuringBootAndChangesWithKernelBootId() throws Exception {
        File id = temporary.newFile();
        write(id, "11111111-2222-3333-4444-555555555555\n");
        int first = BootToken.read(id);
        assertTrue(first >= 0);
        assertEquals(first, BootToken.read(id));
        write(id, "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee\n");
        assertNotEquals(first, BootToken.read(id));
    }

    @Test public void absentOrInvalidBootIdCannotTriggerBootActions() throws Exception {
        assertEquals(-1, BootToken.read(new File(temporary.getRoot(), "missing")));
        File id = temporary.newFile();
        write(id, "");
        assertEquals(-1, BootToken.read(id));
        write(id, "not-a-boot-id");
        assertEquals(-1, BootToken.read(id));
    }

    private static void write(File file, String text) throws Exception {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(text.getBytes(StandardCharsets.UTF_8));
        }
    }
}
