package gcg.crypto.param;

import gcg.crypto.CipherParameters;

public class KeyParameter implements CipherParameters {
    private byte[] key;

    public KeyParameter(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }
}
