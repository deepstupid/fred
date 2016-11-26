//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.bitpedia.collider.core;

import org.bitpedia.collider.core.Md4Handler;

public class Ed2Handler {
    private static final int EDSEG_SIZE = 9728000;
    private Md4Handler seg;
    private Md4Handler top;
    private long nextPos;

    public Ed2Handler() {
    }

    public void analyzeInit() {
        this.nextPos = 0L;
        this.seg = new Md4Handler();
        this.seg.analyzeInit();
        this.top = new Md4Handler();
        this.top.analyzeInit();
    }

    public void analyzeUpdate(byte[] var1, int var2) {
        this.analyzeUpdate(var1, 0, var2);
    }

    public void analyzeUpdate(byte[] var1, int var2, int var3) {
        if(0 != var3) {
            if(0L < this.nextPos && 0L == this.nextPos % 9728000L) {
                byte[] var4 = this.seg.analyzeFinal();
                this.top.analyzeUpdate(var4, 16);
                this.seg.analyzeInit();
            }

            if(this.nextPos / 9728000L == (this.nextPos + (long)var3) / 9728000L) {
                this.seg.analyzeUpdate(var1, var2, var3);
                this.nextPos += (long)var3;
            } else {
                int var5 = 9728000 - (int)(this.nextPos % 9728000L);
                this.seg.analyzeUpdate(var1, var2, var5);
                this.nextPos += (long)var5;
                this.analyzeUpdate(var1, var2 + var5, var3 - var5);
            }
        }
    }

    public byte[] analyzeFinal() {
        if(this.nextPos <= 9728000L) {
            return this.seg.analyzeFinal();
        } else {
            byte[] var1 = this.seg.analyzeFinal();
            this.top.analyzeUpdate(var1, 16);
            return this.top.analyzeFinal();
        }
    }
}
