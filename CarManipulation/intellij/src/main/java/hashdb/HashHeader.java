package hashdb;

import java.nio.ByteBuffer;

/**
 *     int  iMaxHash;          // Number of records defined for Primary
 *                             // area.  (This isn't the number of
 *                             // records currently in the Primary Area.)
 *     int  iRecSize;          // Size in bytes for each record in the
 *                             // hash file
 *     int iMaxProbe;          // Max number of probes for a collision (Pgm 2)
 */
public class HashHeader {
    public static final int MAX_REC_SIZE = 500;

    private int maxHash;
    private int recSize;
    private int maxProbe;

    public HashHeader() {
    }

    @Override
    public String toString() {
        return "HashHeader{" +
                "maxHash=" + maxHash +
                ", recSize=" + recSize +
                ", maxProbe=" + maxProbe +
                '}';
    }

    // accessors

    public int getMaxHash() {
        return maxHash;
    }

    public void setMaxHash(int maxHash) {
        this.maxHash = maxHash;
    }

    public int getRecSize() {
        return recSize;
    }

    public void setRecSize(int recSize) {
        this.recSize = recSize;
    }

    public int getMaxProbe() {
        return maxProbe;
    }

    public void setMaxProbe(int maxProbe) {
        this.maxProbe = maxProbe;
    }

    public byte [] toByteArray() {
        ByteBuffer bytes = ByteBuffer.allocate(getRecSize());
        bytes.putInt(getMaxHash());
        bytes.putInt(getMaxProbe());
        bytes.putInt(getRecSize());
        for(int i = 12; i < getRecSize() / 2; i++) {
            bytes.putChar('\0');
        }
        return bytes.array();
    }

    public void fromByteArray(byte [] input) {
         ByteBuffer bytes = ByteBuffer.wrap(input);
         setMaxHash(bytes.getInt());
         setMaxProbe(bytes.getInt());
         setRecSize(bytes.getInt());
    }

}
