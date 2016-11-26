//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.bitpedia.collider.core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class Md4Handler {
    private static final int S11 = 3;
    private static final int S12 = 7;
    private static final int S13 = 11;
    private static final int S14 = 19;
    private static final int S21 = 3;
    private static final int S22 = 5;
    private static final int S23 = 9;
    private static final int S24 = 13;
    private static final int S31 = 3;
    private static final int S32 = 9;
    private static final int S33 = 11;
    private static final int S34 = 15;
    private static byte P0 = -128;
    private static byte[] PADDING;
    private int[] state;
    private int[] count;
    private byte[] buffer;

    public Md4Handler() {
    }

    private static int F(int var0, int var1, int var2) {
        return var0 & var1 | ~var0 & var2;
    }

    private static int G(int var0, int var1, int var2) {
        return var0 & var1 | var0 & var2 | var1 & var2;
    }

    private static int H(int var0, int var1, int var2) {
        return var0 ^ var1 ^ var2;
    }

    private static int rotateLeft(int var0, int var1) {
        return var0 << var1 | var0 >>> 32 - var1;
    }

    private static int FF(int var0, int var1, int var2, int var3, int var4, int var5) {
        var0 += F(var1, var2, var3) + var4;
        return rotateLeft(var0, var5);
    }

    private static int GG(int var0, int var1, int var2, int var3, int var4, int var5) {
        var0 += G(var1, var2, var3) + var4 + 1518500249;
        return rotateLeft(var0, var5);
    }

    private static int HH(int var0, int var1, int var2, int var3, int var4, int var5) {
        var0 += H(var1, var2, var3) + var4 + 1859775393;
        return rotateLeft(var0, var5);
    }

    private static void encode(byte[] var0, int[] var1, int var2) {
        ByteBuffer var3 = ByteBuffer.allocate(var2);
        var3.order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer var4 = var3.asIntBuffer();
        var4.put(var1, 0, var2 / 4);
        var3.get(var0, 0, var2);
    }

    private static void decode(int[] var0, byte[] var1, int var2, int var3) {
        ByteBuffer var4 = ByteBuffer.wrap(var1, var2, var3);
        var4.order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer var5 = var4.asIntBuffer();
        var5.get(var0, 0, var3 / 4);
    }

    private static void md4Transform(int[] var0, byte[] var1, int var2) {
        int var3 = var0[0];
        int var4 = var0[1];
        int var5 = var0[2];
        int var6 = var0[3];
        int[] var7 = new int[16];
        decode(var7, var1, var2, 64);
        var3 = FF(var3, var4, var5, var6, var7[0], 3);
        var6 = FF(var6, var3, var4, var5, var7[1], 7);
        var5 = FF(var5, var6, var3, var4, var7[2], 11);
        var4 = FF(var4, var5, var6, var3, var7[3], 19);
        var3 = FF(var3, var4, var5, var6, var7[4], 3);
        var6 = FF(var6, var3, var4, var5, var7[5], 7);
        var5 = FF(var5, var6, var3, var4, var7[6], 11);
        var4 = FF(var4, var5, var6, var3, var7[7], 19);
        var3 = FF(var3, var4, var5, var6, var7[8], 3);
        var6 = FF(var6, var3, var4, var5, var7[9], 7);
        var5 = FF(var5, var6, var3, var4, var7[10], 11);
        var4 = FF(var4, var5, var6, var3, var7[11], 19);
        var3 = FF(var3, var4, var5, var6, var7[12], 3);
        var6 = FF(var6, var3, var4, var5, var7[13], 7);
        var5 = FF(var5, var6, var3, var4, var7[14], 11);
        var4 = FF(var4, var5, var6, var3, var7[15], 19);
        var3 = GG(var3, var4, var5, var6, var7[0], 3);
        var6 = GG(var6, var3, var4, var5, var7[4], 5);
        var5 = GG(var5, var6, var3, var4, var7[8], 9);
        var4 = GG(var4, var5, var6, var3, var7[12], 13);
        var3 = GG(var3, var4, var5, var6, var7[1], 3);
        var6 = GG(var6, var3, var4, var5, var7[5], 5);
        var5 = GG(var5, var6, var3, var4, var7[9], 9);
        var4 = GG(var4, var5, var6, var3, var7[13], 13);
        var3 = GG(var3, var4, var5, var6, var7[2], 3);
        var6 = GG(var6, var3, var4, var5, var7[6], 5);
        var5 = GG(var5, var6, var3, var4, var7[10], 9);
        var4 = GG(var4, var5, var6, var3, var7[14], 13);
        var3 = GG(var3, var4, var5, var6, var7[3], 3);
        var6 = GG(var6, var3, var4, var5, var7[7], 5);
        var5 = GG(var5, var6, var3, var4, var7[11], 9);
        var4 = GG(var4, var5, var6, var3, var7[15], 13);
        var3 = HH(var3, var4, var5, var6, var7[0], 3);
        var6 = HH(var6, var3, var4, var5, var7[8], 9);
        var5 = HH(var5, var6, var3, var4, var7[4], 11);
        var4 = HH(var4, var5, var6, var3, var7[12], 15);
        var3 = HH(var3, var4, var5, var6, var7[2], 3);
        var6 = HH(var6, var3, var4, var5, var7[10], 9);
        var5 = HH(var5, var6, var3, var4, var7[6], 11);
        var4 = HH(var4, var5, var6, var3, var7[14], 15);
        var3 = HH(var3, var4, var5, var6, var7[1], 3);
        var6 = HH(var6, var3, var4, var5, var7[9], 9);
        var5 = HH(var5, var6, var3, var4, var7[5], 11);
        var4 = HH(var4, var5, var6, var3, var7[13], 15);
        var3 = HH(var3, var4, var5, var6, var7[3], 3);
        var6 = HH(var6, var3, var4, var5, var7[11], 9);
        var5 = HH(var5, var6, var3, var4, var7[7], 11);
        var4 = HH(var4, var5, var6, var3, var7[15], 15);
        var0[0] += var3;
        var0[1] += var4;
        var0[2] += var5;
        var0[3] += var6;
    }

    public void analyzeInit() {
        this.count = new int[]{0, 0};
        this.state = new int[]{1732584193, -271733879, -1732584194, 271733878};
        this.buffer = new byte[64];
    }

    public void analyzeUpdate(byte[] var1, int var2) {
        this.analyzeUpdate(var1, 0, var2);
    }

    public void analyzeUpdate(byte[] var1, int var2, int var3) {
        boolean var4 = false;
        int var5 = this.count[0] >> 3 & 63;
        this.count[0] += var3 << 3;
        if(this.count[0] < var3 << 3) {
            ++this.count[1];
        }

        this.count[1] += var3 >> 29;
        int var6 = 64 - var5;
        int var7;
        if(var6 <= var3) {
            System.arraycopy(var1, var2, this.buffer, var5, var6);
            md4Transform(this.state, this.buffer, 0);

            for(var7 = var6; var7 + 63 < var3; var7 += 64) {
                md4Transform(this.state, var1, var7 + var2);
            }

            var5 = 0;
        } else {
            var7 = 0;
        }

        System.arraycopy(var1, var7 + var2, this.buffer, var5, var3 - var7);
    }

    public byte[] analyzeFinal() {
        byte[] var1 = new byte[8];
        encode(var1, this.count, 8);
        int var2 = this.count[0] >> 3 & 63;
        int var3 = var2 < 56?56 - var2:120 - var2;
        this.analyzeUpdate(PADDING, var3);
        this.analyzeUpdate(var1, 8);
        byte[] var4 = new byte[16];
        encode(var4, this.state, 16);
        return var4;
    }

    static {
        PADDING = new byte[]{P0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }
}
