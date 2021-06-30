package gcg.crypto;

import gcg.crypto.algorithm.HC128;
import gcg.crypto.stream.CryptoInputStream;
import gcg.crypto.stream.CryptoOutputStream;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HC128Test {

    private static final String KEY = "test_case_161214";
    private static final String FILE = "test.file";

    private static final byte oneByte = 'H';
    private static final byte[] manyBytes = "Hello World".getBytes();

    @Test
    @Order(1)
    void testWriteOneByte() throws IOException, InstantiationException, IllegalAccessException {
        OutputStream os = new FileOutputStream(FILE, false);
        CryptoOutputStream cos = new CryptoOutputStream(os, KEY, HC128.class);

        cos.write(oneByte);
        cos.flush();
        cos.close();
    }

    @Test
    @Order(2)
    void testReadOneByte() throws IOException, InstantiationException, IllegalAccessException {
        InputStream is = new FileInputStream(FILE);
        CryptoInputStream cis = new CryptoInputStream(is, KEY, HC128.class);

        assertEquals(1, cis.available());
        int b = cis.read();
        assertEquals(oneByte, b);
    }

    @Test
    @Order(3)
    void testWriteManyBytes() throws IOException, InstantiationException, IllegalAccessException {
        OutputStream os = new FileOutputStream(FILE, false);
        CryptoOutputStream cos = new CryptoOutputStream(os, KEY, HC128.class);

        cos.write(manyBytes);
        cos.flush();
        cos.close();
    }

    @Test
    @Order(4)
    void testReadManyBytes() throws IOException, InstantiationException, IllegalAccessException {
        InputStream is = new FileInputStream(FILE);
        CryptoInputStream cis = new CryptoInputStream(is, KEY, HC128.class);

        assertEquals(manyBytes.length, cis.available());
        byte[] buf = new byte[1024];
        int len = cis.read(buf);
        assertEquals(manyBytes.length, len);
        assertArrayEquals(manyBytes, Arrays.copyOf(buf, len));
    }

    @AfterAll
    private static void cleanUp() throws IOException {
        Files.deleteIfExists(Paths.get(FILE));
    }
}
