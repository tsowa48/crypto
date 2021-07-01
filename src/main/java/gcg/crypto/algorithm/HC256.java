package gcg.crypto.algorithm;

import gcg.crypto.CipherParameters;
import gcg.crypto.DataLengthException;
import gcg.crypto.StreamCipher;
import gcg.crypto.param.IVParameter;
import gcg.crypto.param.KeyParameter;

public class HC256 implements StreamCipher {
    private int[] p = new int[1024];
    private int[] q = new int[1024];
    private int cnt = 0;
    private byte[] key, iv;
    private boolean initialized;

    private int step() {
        int j = cnt & 0x3FF;
        int ret;
        if (cnt < 1024) {
            int x = p[(j - 3 & 0x3FF)];
            int y = p[(j - 1023 & 0x3FF)];
            p[j] += p[(j - 10 & 0x3FF)] + (rotateRight(x, 10) ^ rotateRight(y, 23)) + q[((x ^ y) & 0x3FF)];
            x = p[(j - 12 & 0x3FF)];
            ret = (q[x & 0xFF] + q[((x >> 8) & 0xFF) + 256] + q[((x >> 16) & 0xFF) + 512] + q[((x >> 24) & 0xFF) + 768]) ^ p[j];
        } else {
            int x = q[(j - 3 & 0x3FF)];
            int y = q[(j - 1023 & 0x3FF)];
            q[j] += q[(j - 10 & 0x3FF)] + (rotateRight(x, 10) ^ rotateRight(y, 23)) + p[((x ^ y) & 0x3FF)];
            x = q[(j - 12 & 0x3FF)];
            ret = (p[x & 0xFF] + p[((x >> 8) & 0xFF) + 256] + p[((x >> 16) & 0xFF) + 512] + p[((x >> 24) & 0xFF) + 768]) ^ q[j];
        }
        cnt = cnt + 1 & 0x7FF;
        return ret;
    }

    private void init() {
        if (key.length != 32) {
            byte[] k = new byte[32];
            System.arraycopy(key, 0, k, 0, Math.min(32, key.length));
            key = k;
        }
        if (iv.length < 32) {
            byte[] newIV = new byte[32];
            System.arraycopy(iv, 0, newIV, 0, iv.length);
            if(iv.length > 0) {
                System.arraycopy(iv, 0, newIV, iv.length, newIV.length - iv.length);
            }
            iv = newIV;
        }
        idx = 0;
        cnt = 0;
        int[] w = new int[2560];
        for (int i = 0; i < 32; i++) {
            w[i >> 2] |= (key[i] & 0xff) << (8 * (i & 0x3));
        }
        for (int i = 0; i < 32; i++) {
            w[(i >> 2) + 8] |= (iv[i] & 0xff) << (8 * (i & 0x3));
        }
        for (int i = 16; i < 2560; i++) {
            int x = w[i - 2];
            int y = w[i - 15];
            w[i] = (rotateRight(x, 17) ^ rotateRight(x, 19) ^ (x >>> 10))
                    + w[i - 7]
                    + (rotateRight(y, 7) ^ rotateRight(y, 18) ^ (y >>> 3))
                    + w[i - 16] + i;
        }
        System.arraycopy(w, 512, p, 0, 1024);
        System.arraycopy(w, 1536, q, 0, 1024);
        for (int i = 0; i < 4096; i++) {
            step();
        }
        cnt = 0;
    }

    @Override
    public String getAlgorithmName() {
        return "HC-256";
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        CipherParameters keyParam = params;
        if (params instanceof IVParameter) {
            iv = ((IVParameter) params).getIV();
            keyParam = ((IVParameter) params).getParameters();
        } else {
            iv = new byte[0];
        }
        if (keyParam instanceof KeyParameter) {
            key = ((KeyParameter) keyParam).getKey();
            init();
        } else {
            throw new IllegalArgumentException("Invalid parameter passed to HC256 init - " + params.getClass().getName());
        }
        initialized = true;
    }

    private byte[] buf = new byte[4];
    private int idx = 0;

    private byte getByte() {
        if (idx == 0) {
            int step = step();
            buf[0] = (byte) (step & 0xFF);
            step >>= 8;
            buf[1] = (byte) (step & 0xFF);
            step >>= 8;
            buf[2] = (byte) (step & 0xFF);
            step >>= 8;
            buf[3] = (byte) (step & 0xFF);
        }
        byte ret = buf[idx];
        idx = idx + 1 & 0x3;
        return ret;
    }

    @Override
    public void processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (!initialized) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if ((inOff + len) > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if ((outOff + len) > out.length) {
            throw new DataLengthException("output buffer too short");
        }
        for (int i = 0; i < len; i++) {
            out[outOff + i] = (byte) (in[inOff + i] ^ getByte());
        }
    }

    @Override
    public void reset() {
        init();
    }

    @Override
    public byte processByte(byte in) {
        return (byte) (in ^ getByte());
    }

    private static int rotateRight(int x, int bits) {
        return (x >>> bits) | (x << -bits);
    }
}