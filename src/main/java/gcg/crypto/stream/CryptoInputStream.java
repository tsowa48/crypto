package gcg.crypto.stream;

import gcg.crypto.param.KeyParameter;
import gcg.crypto.StreamCipher;

import java.io.IOException;
import java.io.InputStream;

public class CryptoInputStream extends InputStream {
    private InputStream inputStream;
    private StreamCipher streamCipherInstance;

    public CryptoInputStream(InputStream inputStream, String key, Class<? extends StreamCipher> cipher) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        this.inputStream = inputStream;
        streamCipherInstance = cipher.newInstance();
        streamCipherInstance.init(false, new KeyParameter(key.getBytes()));
    }

    @Override
    public int read() throws IOException {
        int in = inputStream.read();
        return streamCipherInstance.processByte((byte)in);
    }

    public int available() throws IOException {
        return inputStream.available();
    }

    public void close() throws IOException {
        inputStream.close();
    }

    public void mark(int readLimit) {
        inputStream.mark(readLimit);
    }

    public boolean markSupported() {
        return inputStream.markSupported();
    }

    public int read(byte[] b) throws IOException {
        byte[] in = new byte[b.length];
        int length = inputStream.read(in);
        streamCipherInstance.processBytes(in, 0, length, b, 0);
        return length;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        byte[] in = new byte[len];
        int length = inputStream.read(in, off, len);
        streamCipherInstance.processBytes(in, 0, length, b, off);
        return length;
    }

    public void reset() throws IOException {
        inputStream.reset();
    }

    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }
}