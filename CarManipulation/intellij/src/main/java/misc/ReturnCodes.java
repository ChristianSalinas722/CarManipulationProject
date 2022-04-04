package misc;

public class ReturnCodes {
    public static final int RC_OK = 0;
    public static final int RC_FILE_EXISTS = 1;   // file already exists
    public static final int RC_REC_EXISTS = 2;   // record already exists
    public static final int RC_REC_NOT_FOUND = 3;   // record not found
    public static final int RC_FILE_NOT_FOUND = 4;   // file not found
    public static final int RC_HEADER_NOT_FOUND = 5;   // Header record not found
    public static final int RC_BAD_REC_SIZE = 6;   // invalid record size in info record
    public static final int RC_LOC_NOT_FOUND = 7;   // Location not found for read
    public static final int RC_LOC_NOT_WRITTEN = 8;   // Location not written for write
    public static final int RC_SYNONYM = -9;   // Synonym exists at the specified location
    public static final int RC_NOT_IMPLEMENTED = 10;  // Not implemented
    public static final int RC_TOO_MANY_COLLISIONS = -11;  // probing found too many collisions (pgm2)
    public static final int RC_INVALID_PARM = 99;

    public static void printRC(int rc) {
        String msg = "";
        switch(rc) {
            case RC_OK:
                return;
            case RC_FILE_EXISTS:
                msg = "file already exists";
                break;
            case RC_REC_EXISTS:
                msg = "record already exists";
                break;
            case RC_REC_NOT_FOUND:
                msg = "record not found";
                break;
            case RC_INVALID_PARM:
                msg = "invalid parameter";
                break;
            case RC_FILE_NOT_FOUND:
                msg = "file not found or invalid header record";
                break;
            case RC_HEADER_NOT_FOUND:
                msg = "header record not found";
                break;
            case RC_BAD_REC_SIZE:
                msg = "invalid record size in header recordd";
                break;
            case RC_LOC_NOT_FOUND:
                msg = "Location not found for read";
                break;
            case RC_LOC_NOT_WRITTEN:
                msg = "Location not written for write";
                break;
            case RC_SYNONYM:
                msg = "Synonym exists at the specified location";
                break;
            case RC_NOT_IMPLEMENTED:
                msg = "Function not implemented";
                break;
            case RC_TOO_MANY_COLLISIONS:
                msg = "Too Many Collisions to write record";
                break;
            default:
                msg = "unknown return code: " + rc;
        }
        System.out.printf("    **** ERROR: %s\n", msg);
    }
}
