package hashdb;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *     char szVehicleId[7];     // Individual Vehicle Id
 *     char szMake[13];         // Vheicle Make (e.g., FORD, TOYOTA)
 *     char szModel[12];        // Vehicle Model (e.g., EXPLORER, 4RUNNER, PRIUS)
 *     int  iYear;              // year this particular vehicle was manufactured
 */
public class Vehicle {
    public static final int MAX_VEHICLE_ID_SIZE = 7;
    public static final int MAX_MAKE_SIZE = 13;
    public static final int MAX_MODEL_SIZE = 12;

    private char [] vehicleId;
    private char [] make;
    private char [] model;
    private char [] year;

    public Vehicle() {
        vehicleId = new char[MAX_VEHICLE_ID_SIZE];
        make = new char[MAX_MAKE_SIZE];
        model = new char[MAX_MODEL_SIZE];
        year = new char[4]; // only support 4-digit years
    }

    public char[] getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(char[] vehicleId) {
        System.arraycopy(vehicleId, 0, this.vehicleId, 0, vehicleId.length);
    }

    public char[] getMake() {
        return make;
    }

    public void setMake(char[] make) {
        System.arraycopy(make, 0, this.make, 0, make.length);
    }

    public char[] getModel() {
        return model;
    }

    public void setModel(char[] model) {
        System.arraycopy(model, 0, this.model, 0, model.length);
    }

    public char[] getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = ("" + year).toCharArray();
    }

    // returns size in BYTES (72)
    // REMEMBER: chars are 2 bytes in Java. while a vehicle is 36 chars in size, it is 72 bytes
    public static int sizeOf() {
        return 2 * (MAX_VEHICLE_ID_SIZE + MAX_MAKE_SIZE + MAX_MODEL_SIZE + 4);
    }

    public byte [] toByteArray() {
        ByteBuffer bytes = ByteBuffer.allocate(sizeOf() * 2);
        for(char c: vehicleId)
            bytes.putChar(c);
        for(char c: make)
            bytes.putChar(c);
        for(char c: model)
            bytes.putChar(c);
        for(char c: year)
            bytes.putChar(c);
        return bytes.array();
    }

    public void fromByteArray(byte [] input) {
        ByteBuffer bytes = ByteBuffer.wrap(input);
        for(int i = 0; i < vehicleId.length; i++)
            vehicleId[i] = bytes.getChar();
        for(int i = 0; i < make.length; i++)
            make[i] = bytes.getChar();
        for(int i = 0; i < model.length; i++)
            model[i] = bytes.getChar();
        for(int i = 0; i < year.length; i++)
            year[i] = bytes.getChar();
    }

    public char [] toFileChars() {
        char [] chars = new char[sizeOf() / 2];
        for(int i = 0; i < MAX_VEHICLE_ID_SIZE; i++) {
            chars[i] = vehicleId[i];
        }
        for(int i = 0; i < MAX_MAKE_SIZE; i++) {
            chars[MAX_VEHICLE_ID_SIZE + i] = make[i];
        }
        for(int i = 0; i < MAX_MODEL_SIZE; i++) {
            chars[MAX_MAKE_SIZE + MAX_VEHICLE_ID_SIZE + i] = model[i];
        }
        for(int i = 0; i < 4; i++) {
            chars[MAX_MODEL_SIZE + MAX_MAKE_SIZE + MAX_VEHICLE_ID_SIZE + i] = year[i];
        }
        return chars;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + Arrays.toString(vehicleId) +
                ", make=" + Arrays.toString(make) +
                ", model=" + Arrays.toString(model) +
                ", year=" + Arrays.toString(year) +
                '}';
    }

    // intellij generated this
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Arrays.equals(vehicleId, vehicle.vehicleId);
    }

    // intellij generated this
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(vehicleId);
        result = 31 * result + Arrays.hashCode(make);
        result = 31 * result + Arrays.hashCode(model);
        result = 31 * result + Arrays.hashCode(year);
        return result;
    }

    public String getMakeAsString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < MAX_MAKE_SIZE; i++) {
            if(make[i] != '\0')
                sb.append(make[i]);
        }
        return sb.toString();
    }

    public String getModelAsString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < MAX_MODEL_SIZE; i++) {
            if(model[i] != '\0')
                sb.append(model[i]);
        }
        return sb.toString();
    }

    public String getVehicleIdAsString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < MAX_VEHICLE_ID_SIZE; i++) {
            if(vehicleId[i] != '\0')
                sb.append(vehicleId[i]);
        }
        return sb.toString();
    }

    public String getYearAsString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            sb.append(year[i]);
        }
        return sb.toString();
    }

    public boolean isSynonym(Vehicle vehicle) {
        if (this == vehicle) return true;
        if (vehicle == null || getClass() != vehicle.getClass()) return false;
        return Arrays.equals(vehicleId, vehicle.vehicleId)
                && Arrays.equals(make, vehicle.make)
                && Arrays.equals(model, vehicle.model)
                && Arrays.equals(year, vehicle.year);
    }
}
