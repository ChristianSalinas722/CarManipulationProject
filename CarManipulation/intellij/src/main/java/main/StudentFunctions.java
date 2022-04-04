package main;

import hashdb.HashFile;
import hashdb.HashHeader;
import hashdb.Vehicle;
import misc.MutableInteger;
import misc.ReturnCodes;
import java.io.*;
//import java.util.Arrays;

public class StudentFunctions {
    /**
     * hashCreate
     * This funcAon creates a hash file containing only the HashHeader record.
     * • If the file already exists, return RC_FILE_EXISTS
     * • Create the binary file by opening it.
     * • Write the HashHeader record to the file at RBN 0.
     * • close the file.
     * • return RC_OK.
     */
    public static int hashCreate(String fileName, HashHeader hashHeader) {

        File doesExist = new File(fileName);
        if (doesExist.exists()) {
            return ReturnCodes.RC_FILE_EXISTS;
        } else {
            try {
                RandomAccessFile inFile = new RandomAccessFile(fileName, "rw");
                inFile.write(hashHeader.toByteArray());
                inFile.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return ReturnCodes.RC_OK;
        }
    }

    /**
     * hashOpen
     * This function opens an existing hash file which must contain a HashHeader record
     * , and sets the file member of hashFile
     * It returns the HashHeader record by setting the HashHeader member in hashFile
     * If it doesn't exist, return RC_FILE_NOT_FOUND.
     * Read the HashHeader record from file and return it through the parameter.
     * If the read fails, return RC_HEADER_NOT_FOUND.
     * return RC_OK
     */
    public static int hashOpen(String fileName, HashFile hashFile) {

            try {
                File inFile = new File(fileName);
                if (!inFile.exists()) {
                    return ReturnCodes.RC_FILE_NOT_FOUND;
                }
                RandomAccessFile rbn = new RandomAccessFile(fileName, "rw");
                byte[] bit = new byte[Vehicle.sizeOf() * 2];
                rbn.read(bit, 0, Vehicle.sizeOf() * 2);
                hashFile.getHashHeader().fromByteArray(bit);
                hashFile.setFile(rbn);
            }
            catch (IOException exception) {
                return ReturnCodes.RC_HEADER_NOT_FOUND;
            }
        return ReturnCodes.RC_OK;
        }

    /**
     * vehicleInsert
     * This function inserts a vehicle into the specified file.
     * Determine the RBN using the Main class' hash function.
     * Use readRec to read the record at that RBN.
     * If that location doesn't exist
     * OR the record at that location has a blank vehicleId (i.e., empty string):
     * THEN Write this new vehicle record at that location using writeRec.
     * If that record exists and that vehicle's szVehicleId matches, return RC_REC_EXISTS.
     * (Do not update it.)
     * Otherwise, return RC_SYNONYM. a SYNONYM is the same thing as a HASH COLLISION
     * Note that in program #2, we will actually insert synonyms.
     */
    public static int vehicleInsert(HashFile hashFile, Vehicle vehicle) {
        int rbn = P2Main.hash(vehicle.getVehicleId(), hashFile.getHashHeader().getMaxHash());
        Vehicle newVehicle = new Vehicle();
        int read = StudentFunctions.readRec(hashFile, rbn, newVehicle);
        if (newVehicle.getVehicleIdAsString().length() == 0){
            writeRec(hashFile, rbn, vehicle);
            return ReturnCodes.RC_OK;
        }
        else if (newVehicle.getVehicleIdAsString().equals(vehicle.getVehicleIdAsString())) {
            return ReturnCodes.RC_REC_EXISTS;
        }
        else {
            int init = rbn;
            int k = 1;
            Vehicle temp1 = new Vehicle();
            read = readRec(hashFile, init+k, temp1);
            if(newVehicle.getVehicleIdAsString().equals(vehicle.getVehicleIdAsString())){
                return ReturnCodes.RC_REC_EXISTS;
            }

            while(k < hashFile.getHashHeader().getMaxProbe()){
                Vehicle collision = new Vehicle();
                read = StudentFunctions.readRec(hashFile, init+k, collision);

                if(newVehicle.getVehicleIdAsString().equals(vehicle.getVehicleIdAsString())){
                    return ReturnCodes.RC_REC_EXISTS;
                }

                if(read == ReturnCodes.RC_LOC_NOT_FOUND || collision.getVehicleIdAsString().length() == 0){
                    writeRec(hashFile, init + k, vehicle);
                    return ReturnCodes.RC_OK;
                }
                k++;
            }
            return ReturnCodes.RC_TOO_MANY_COLLISIONS;
        }
    }

    /**
     * readRec
     * This function reads a record at the specified RBN in the specified file.
     * Determine the RBA based on RBN and the HashHeader's recSize
     * Use seek to position the file in that location.
     * Read that record and return it through the vehicle parameter.
     * If the location is not found, return RC_LOC_NOT_FOUND.  Otherwise, return RC_OK.
     * Note: if the location is found, that does NOT imply that a vehicle
     * was written to that location.  Why?
     */
    public static int readRec(HashFile hashFile, int rbn, Vehicle vehicle) {
        int rba = rbn * hashFile.getHashHeader().getRecSize();
        try {
            hashFile.getFile().seek(rba);
            byte[] bit = new byte[Vehicle.sizeOf() * 2];
            hashFile.getFile().read(bit, 0, Vehicle.sizeOf() * 2);
            if (bit[1] != 0) {
                vehicle.fromByteArray(bit);
            }
        } catch (IOException | java.nio.BufferUnderflowException exception) {
            return ReturnCodes.RC_LOC_NOT_FOUND;
        }
        return ReturnCodes.RC_OK;
    }

    /**
     * writeRec
     * This function writes a record to the specified RBN in the specified file.
     * Determine the RBA based on RBN and the HashHeader's recSize
     * Use seek to position the file in that location.
     * Write that record to the file.
     * If the write fails, return RC_LOC_NOT_WRITTEN.
     * Otherwise, return RC_OK.
     */
    public static int writeRec(HashFile hashFile, int rbn, Vehicle vehicle) {
        int rba = rbn * hashFile.getHashHeader().getRecSize();
        try {
            hashFile.getFile().seek(rba);
            char[] chars = vehicle.toFileChars();
            for (char aChar : chars) {
                hashFile.getFile().writeChar(aChar);
            }
        } catch (IOException exception) {
            return ReturnCodes.RC_LOC_NOT_WRITTEN;
        }
        return ReturnCodes.RC_OK;
    }

    /**
     * vehicleRead
     * This function reads the specified vehicle by its vehicleId.
     * Since the vehicleId was provided,
     * determine the RBN using the Main class' hash function.
     * Use readRec to read the record at that RBN.
     * If the vehicle at that location matches the specified vehicleId,
     * return the vehicle via the parameter and return RC_OK.
     * Otherwise, return RC_REC_NOT_FOUND
     */
    public static int vehicleRead(HashFile hashFile, MutableInteger rbn, Vehicle vehicle) {
        Vehicle newVehicle = new Vehicle();
        int read = readRec(hashFile, rbn.intValue(), newVehicle);
        if (newVehicle.getVehicleIdAsString().equals(vehicle.getVehicleIdAsString())){
            vehicle.fromByteArray(newVehicle.toByteArray());
            return ReturnCodes.RC_OK;
        }
        //Modify this function starting at the bulleted item in RED
        //• Change your function to use rbn as a MutableInteger
        //• Since the vehicles vehicleId was provided, determine the RBN using P2Main's hash function.
        //• Use readRec to read the record at that RBN.
        //• If the vehicle at that location matches the specified vehicle’s vehicleId, return the vehicle via the
        //vehicle parameter and return RC_OK.
        //• Otherwise, it is a synonym to the vehicle in the hashed location:
        //o Determine if it exists as a synonym using probing with a K value of 1.
        //o Be sure to store any changed rbn in the rbn parameter!! P2Main uses it.
        //o If vehicleIds match, return the vehicle via the vehicle parameter and return RC_OK.
        //o If you read past the maximum records in the file, return RC_REC_NOT_FOUND.
        //o If you have read for the maximum probes, and it wasn't found,return
        //RC_REC_NOT_FOUND.
        else{
            int k = 1;
            int init = rbn.intValue();
            while(k < hashFile.getHashHeader().getMaxProbe()){
                rbn.set(init+k);
                if(rbn.intValue() > hashFile.getHashHeader().getMaxHash()){
                    return ReturnCodes.RC_REC_NOT_FOUND;
                }
                read = readRec(hashFile, rbn.intValue(), vehicle);
                if(vehicle.getVehicleIdAsString().equals(newVehicle.getVehicleIdAsString())){
                    vehicle.fromByteArray(newVehicle.toByteArray());
                    return ReturnCodes.RC_OK;
                }
                k++;
            }
            return ReturnCodes.RC_REC_NOT_FOUND;
        }
    }
    public static int vehicleUpdate(HashFile hashfile, Vehicle vehicle){
        Vehicle temp = new Vehicle();
        MutableInteger rbn = new MutableInteger(P2Main.hash(vehicle.getVehicleId(), hashfile.getHashHeader().getMaxHash()));

        int read = readRec(hashfile, rbn.intValue(), temp);

        if(read == ReturnCodes.RC_OK && vehicle.getVehicleIdAsString().equals(temp.getVehicleIdAsString())){
            writeRec(hashfile, rbn.intValue(), vehicle);
            return ReturnCodes.RC_OK;
        }
        else{
            int init = rbn.intValue();
            int k = 1;

            read = readRec(hashfile, init + k, temp);

            if(vehicle.getVehicleIdAsString().equals(temp.getVehicleIdAsString())){
                return ReturnCodes.RC_REC_EXISTS;
            }

            int maxProbe = hashfile.getHashHeader().getMaxProbe();

            while(k < maxProbe){
                k++;
                Vehicle collision = new Vehicle();
                read = readRec(hashfile, init + k, collision);

                if(collision.getVehicleIdAsString().equals(vehicle.getVehicleIdAsString())){
                    return ReturnCodes.RC_REC_EXISTS;
                }

                if(read == ReturnCodes.RC_LOC_NOT_FOUND || collision.getVehicleIdAsString().length() ==0){
                    writeRec(hashfile, init + k, vehicle);
                    return ReturnCodes.RC_OK;
                }
            }
            return ReturnCodes.RC_REC_NOT_FOUND;
        }
    }
    public static int vehicleDelete(HashFile hashFile, char name[]){

        return ReturnCodes.RC_NOT_IMPLEMENTED;
    }
}