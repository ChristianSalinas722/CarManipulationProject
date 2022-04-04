package main;

import hashdb.HashFile;
import hashdb.Vehicle;
import misc.MutableInteger;
import misc.ParseException;
import misc.ReturnCodes;

import java.io.File;
import java.util.Scanner;

/**********************************************************************
 cs3743p1Driver.c by Larry Clark
 Copyright 2019 Larry Clark, this code may not be copied to any other website.

 Re-written in Java by Docrob
 Note that some comments may be invalid due to the Java conversion.

 Purpose: This program demonstrates a Hash File.
 It uses a stream input command file to test the various
 functions written by the students.  The command file comes from stdin.

 Command Parameters:
 your_program_name < infile > outfile

 The stream input file contains commands.  There are commands
 which specify create, insert, read, delete, update, and printall.

 Commands in the input file:

 * comment
 This is just a comment which helps explain what is being tested.

 CREATE VEHICLE fileName maxRecs maxProbes
 Create the specified hash file if it doesn't already exist
 using hashCreate.
 The file will have a max of maxRecs.
 Record 0 contains information about this hash file.
 The size of each record is the size of a Vehicle structure.
 OPEN VEHICLE fileName
 Opens the specified file using hashOpen.  Place the returned
 FILE pointer in the driver's HashFile array.
 INSERT VEHICLE vehicleId,make,model,year
 Uses sscanf to get those attributes and populate a Vehicle structure.
 It invokes VehicleInsert to insert the Vehicle into the hash file.
 READ VEHICLE VehicleId
 Invokes VehicleRead to read the Vehicle from the hash file and prints
 that Vehicle.
 PRINTALL VEHICLE fileName
 Prints the contents of the specified file.
 NUKE VEHICLE fileName
 Removes the specified file.
 Results:
 All output is written to stdout.
 Prints each of the commands and their command parameters.  Some of the commands
 print information:
 CREATE - prints the record size
 INSERT - prints the hashed RBN (record block number)
 READ   - prints (if found):
 -- Actual RBN
 -- the Vehicle information
 -- original hash RBN
 PRINTALL- prints the file's contents
 Returns:
 0       Normal
 99      Processing Error

 Notes:

 **********************************************************************/

public class P2Main {
    public static final int NORMAL_EXIT = 0;
    public static final int PROCESSING_ERROR = 99;

    public static HashFile hashFile; // hash file
    public static Vehicle vehicle; // Vehicle structure used by functions
    public static String fileName;
    public static String command;
    public static String hashContent;                 // content type for hash file (e.g., Vehicle)

    public static void main(String [] args) {
        processCommands();
        System.out.print("\n");
        System.exit(NORMAL_EXIT);
    }

    private static void processCommands() {
        hashFile = new HashFile();
        Scanner input = new Scanner(System.in);

        while(input.hasNextLine()) {
            String line = input.nextLine().trim();
            if(line.length() > 0) {
                processLine(line);
            }
        }
        input.close();
    }

    private static void processLine(String line) {
        String [] tokens = line.split(" ");
        try {
            command = tokens[0];
            if(command.equals("*")) {
                System.out.println(line);
                return;
            }
            System.out.println(" >> " + line);
            switch (command) {
                case "CREATE": // CREATE VEHICLE vehicle.dat 19 5
                    if (tokens.length != 5) {
                        throw new ParseException("invalid # of tokens for CREATE: " + line);
                    }
                    hashContent = tokens[1];
                    fileName = tokens[2];
                    hashFile.getHashHeader().setMaxHash(Integer.parseInt(tokens[3]));
                    hashFile.getHashHeader().setMaxProbe(Integer.parseInt(tokens[4]));
                    hashFile.getHashHeader().setRecSize(Vehicle.sizeOf());
                    System.out.printf("    Record size is %d\n", hashFile.getHashHeader().getRecSize());
                    int rc = StudentFunctions.hashCreate(fileName, hashFile.getHashHeader());
                    if (rc != ReturnCodes.RC_OK)
                        ReturnCodes.printRC(rc);
                    break;
                case "OPEN":
                    if (tokens.length != 3) {
                        throw new ParseException("invalid # of tokens for OPEN: " + line);
                    }
                    hashContent = tokens[1];
                    fileName = tokens[2];
                    rc = StudentFunctions.hashOpen(fileName, hashFile);
                    if (rc != ReturnCodes.RC_OK)
                        ReturnCodes.printRC(rc);
                    break;
                case "INSERT": // INSERT VEHICLE CB001,CHEVY,BelAir,1957
                    if (tokens.length != 3) {
                        throw new ParseException("invalid # of tokens for INSERT: " + line);
                    }
                    hashContent = tokens[1];
                    String [] fields = tokens[2].split(",");
                    vehicle = new Vehicle();
                    vehicle.setVehicleId(fields[0].toCharArray());
                    vehicle.setMake(fields[1].toCharArray());
                    vehicle.setModel(fields[2].toCharArray());
                    vehicle.setYear(Integer.parseInt(fields[3]));
                    System.out.printf("            Hash RBN is %d\n",
                            hash(vehicle.getVehicleId(), hashFile.getHashHeader().getMaxHash()));
                    rc = StudentFunctions.vehicleInsert(hashFile, vehicle);
                    if(rc != ReturnCodes.RC_OK)
                        ReturnCodes.printRC(rc);
                    break;
                case "UPDATE": // UPDATE VEHICLE HA004,HONDA,Accord LX,1988
                    if (tokens.length != 3) {
                        throw new ParseException("invalid # of tokens for UPDATE: " + line);
                    }
                    hashContent = tokens[1];
                    fields = tokens[2].split(",");
                    vehicle = new Vehicle();
                    vehicle.setVehicleId(fields[0].toCharArray());
                    vehicle.setMake(fields[1].toCharArray());
                    vehicle.setModel(fields[2].toCharArray());
                    vehicle.setYear(Integer.parseInt(fields[3]));
                    System.out.printf("            Hash RBN is %d\n",
                            hash(vehicle.getVehicleId(), hashFile.getHashHeader().getMaxHash()));
                    rc = StudentFunctions.vehicleUpdate(hashFile, vehicle);
                    if(rc != ReturnCodes.RC_OK)
                        ReturnCodes.printRC(rc);
                    break;
                case "READ": // READ VEHICLE CB001
                    if (tokens.length != 3) {
                        throw new ParseException("invalid # of tokens for NUKE: " + line);
                    }
                    hashContent = tokens[1];
                    vehicle.setVehicleId(tokens[2].toCharArray());
                    MutableInteger rbn = new MutableInteger(hash(vehicle.getVehicleId(), hashFile.getHashHeader().getMaxHash()));
                    System.out.printf("            Hash RBN is %d\n", rbn.intValue());
                    rc = StudentFunctions.vehicleRead(hashFile, rbn, vehicle);
                    if(rc != ReturnCodes.RC_OK)
                        ReturnCodes.printRC(rc);
                    else {
                        System.out.printf("    %2d", rbn.intValue());  //indent like the PRINTALL command
                        printVehicle(vehicle, hashFile.getHashHeader().getMaxHash());
                    }
                    break;
                case "DELETE": // DELETE VEHICLE T4006
                    if (tokens.length != 3) {
                        throw new ParseException("invalid # of tokens for DELETE: " + line);
                    }
                    hashContent = tokens[1];
                    rc = StudentFunctions.vehicleDelete(hashFile, tokens[2].toCharArray());
                    if(rc != ReturnCodes.RC_OK) {
                        ReturnCodes.printRC(rc);
                    }
                    break;
                case "PRINTALL": // PRINTALL VEHICLE vehicle.dat
                    if (tokens.length != 3) {
                        throw new ParseException("invalid # of tokens for NUKE: " + line);
                    }
                    hashContent = tokens[1];
                    fileName = tokens[2];
                    rc = printAll(fileName);
                    if (rc != ReturnCodes.RC_OK)
                        ReturnCodes.printRC(rc);
                    break;
                case "NUKE": // NUKE VEHICLE vehicle.dat
                    if (tokens.length != 3) {
                        throw new ParseException("invalid # of tokens for NUKE: " + line);
                    }
                    fileName = tokens[2];
                    File file = new File(fileName);
                    // ignore whether or not the file is successfully deleted
                    file.delete();
                    break;
                default:
                    break;
            }
        } catch(Exception e) {
            throw new ParseException(e);
        }
    }

    private static void printVehicle(Vehicle vehicle, int maxHash) {
        System.out.printf(" %6s %4s %-12s %11s Hash=%d\n"
                , vehicle.getVehicleIdAsString()
                , vehicle.getYearAsString()
                , vehicle.getMakeAsString()
                , vehicle.getModelAsString()
                , hash(vehicle.getVehicleId(), maxHash));
    }

    public static int hash(char[] vehicleId, int maxHash) {
        int hashVal = 0;
        if(maxHash <= 0)
            errExit("hash function received an invalid iMaxHash value: " + maxHash);

        for(char c: vehicleId) {
            hashVal += c;
        }
        // restrict it to the hash area
        hashVal = Math.abs(hashVal) % maxHash +1;
        return hashVal;
    }

    private static void errExit(String msg) {
        System.out.println(msg);
        System.exit(PROCESSING_ERROR);
    }

    private static int printAll(String fileName) {
        int rc = StudentFunctions.hashOpen(fileName, hashFile);
        if (rc != ReturnCodes.RC_OK)
            ReturnCodes.printRC(rc);

        System.out.printf("    MaxHash=%d, RecSize=%d, MaxProbes=%d\n"
                , hashFile.getHashHeader().getMaxHash()
                , hashFile.getHashHeader().getRecSize()
                , hashFile.getHashHeader().getMaxProbe());
        if (hashFile.getHashHeader().getRecSize() < Vehicle.sizeOf())
            return ReturnCodes.RC_BAD_REC_SIZE;

        for(int rbn = 1; rbn <= hashFile.getHashHeader().getMaxHash(); rbn++) {
            vehicle = new Vehicle();
            rc = StudentFunctions.readRec(hashFile, rbn, vehicle);
            if(rc == ReturnCodes.RC_OK && vehicle.getVehicleId()[1] != '\0') {
                System.out.printf("    %2d", rbn);
                printVehicle(vehicle, hashFile.getHashHeader().getMaxHash());
            }
        }
        return ReturnCodes.RC_OK;
    }
}
