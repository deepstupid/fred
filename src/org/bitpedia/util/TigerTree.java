//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.bitpedia.util;

import java.security.DigestException;
import java.security.MessageDigest;
import java.util.LinkedList;

public class TigerTree extends MessageDigest {
    private static final int BLOCKSIZE = 1024;
    private static final int HASHSIZE = 24;
    private final byte[] buffer = new byte[1024];
    private int bufferOffset = 0;
    private long byteCount = 0L;
    private MessageDigest tiger = new Tiger();
    private LinkedList nodes = new LinkedList();
    long blockCount = 0L;

    public TigerTree() {
        super("tigertree");
    }

    protected int engineGetDigestLength() {
        return 24;
    }

    protected void engineUpdate(byte var1) {
        ++this.byteCount;
        this.buffer[this.bufferOffset++] = var1;
        if(this.bufferOffset == 1024) {
            this.blockUpdate();
            this.bufferOffset = 0;
        }

    }

    protected void engineUpdate(byte[] var1, int var2, int var3) {
        int var4;
        for(this.byteCount += (long)var3; var3 >= (var4 = 1024 - this.bufferOffset); this.bufferOffset = 0) {
            System.arraycopy(var1, var2, this.buffer, this.bufferOffset, var4);
            this.bufferOffset += var4;
            this.blockUpdate();
            var3 -= var4;
            var2 += var4;
        }

        System.arraycopy(var1, var2, this.buffer, this.bufferOffset, var3);
        this.bufferOffset += var3;
    }

    protected byte[] engineDigest() {
        byte[] var1 = new byte[24];

        try {
            this.engineDigest(var1, 0, 24);
            return var1;
        } catch (DigestException var3) {
            return null;
        }
    }

    protected int engineDigest(byte[] var1, int var2, int var3) throws DigestException {
        if(var3 < 24) {
            throw new DigestException();
        } else {
            this.blockUpdate();

            while(this.nodes.size() > 1) {
                this.composeNodes();
            }

            System.arraycopy(this.nodes.get(0), 0, var1, var2, 24);
            this.engineReset();
            return 24;
        }
    }

    protected void engineReset() {
        this.bufferOffset = 0;
        this.byteCount = 0L;
        this.nodes = new LinkedList();
        this.tiger.reset();
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    protected void blockUpdate() {
        this.tiger.reset();
        this.tiger.update((byte)0);
        this.tiger.update(this.buffer, 0, this.bufferOffset);
        if(!(this.bufferOffset == 0 & this.nodes.size() > 0)) {
            this.nodes.add(this.tiger.digest());
            ++this.blockCount;

            for(long var1 = this.blockCount; var1 % 2L == 0L; var1 >>= 1) {
                this.composeNodes();
            }

        }
    }

    protected void composeNodes() {
        byte[] var1 = (byte[])((byte[])this.nodes.removeLast());
        byte[] var2 = (byte[])((byte[])this.nodes.removeLast());
        this.tiger.reset();
        this.tiger.update((byte)1);
        this.tiger.update(var2);
        this.tiger.update(var1);
        this.nodes.add(this.tiger.digest());
    }
}
