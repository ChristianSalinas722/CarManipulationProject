package hashdb;

import java.io.File;
import java.io.RandomAccessFile;

/**
 *     FILE *pFile;            // FILE pointer to the hash file
 *     HashHeader hashHeader;  // the header record contents for a hash file
 */
public class HashFile {
    private RandomAccessFile file;
    HashHeader hashHeader;

    public HashFile() {
        this.hashHeader = new HashHeader();
    }

    // accessors
    public RandomAccessFile getFile() {
        return file;
    }

    public void setFile(RandomAccessFile file) {
        this.file = file;
    }

    public HashHeader getHashHeader() {
        return hashHeader;
    }

    public void setHashHeader(HashHeader hashHeader) {
        this.hashHeader = hashHeader;
    }
}
