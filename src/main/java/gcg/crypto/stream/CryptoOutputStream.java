package gcg.crypto.stream;

import gcg.crypto.param.KeyParameter;
import gcg.crypto.StreamCipher;

import java.io.IOException;
import java.io.OutputStream;

public class CryptoOutputStream extends OutputStream {
    private OutputStream outputStream;
    private StreamCipher streamCipherInstance;

    public CryptoOutputStream(OutputStream outputStream, String key, Class<? extends StreamCipher> cipher) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
        this.outputStream = outputStream;
        streamCipherInstance = cipher.newInstance();
        streamCipherInstance.init(true, new KeyParameter(key.getBytes()));
    }

    @Override
    public void write(int b) throws IOException {
        byte out = streamCipherInstance.processByte((byte)b);
        outputStream.write(out);
    }

    public void close() throws IOException {
        outputStream.close();
    }

    public void flush() throws IOException {
        outputStream.flush();
    }

    public void write(byte[] b) throws IOException {
        byte[] out = new byte[b.length];
        streamCipherInstance.processBytes(b, 0, b.length, out, 0);
        outputStream.write(out);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        byte[] out = new byte[len];
        streamCipherInstance.processBytes(b, off, len, out, 0);
        outputStream.write(out);
    }
}