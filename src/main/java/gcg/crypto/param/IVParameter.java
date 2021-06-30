package gcg.crypto.param;

import gcg.crypto.CipherParameters;

public class IVParameter implements CipherParameters {
    private byte[] iv;
    private CipherParameters cipherParameters;

    public IVParameter(CipherParameters cipherParameters, byte[] iv) {
        this.cipherParameters = cipherParameters;
        this.iv = iv;
    }

    public byte[] getIV() {
        return iv;
    }

    public CipherParameters getParameters() {
        return cipherParameters;
    }
}
