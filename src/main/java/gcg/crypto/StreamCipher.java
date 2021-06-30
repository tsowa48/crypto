package gcg.crypto;

public interface StreamCipher {
    String getAlgorithmName();
    void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException;
    void processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException;
    void reset();
    byte processByte(byte in);
}
