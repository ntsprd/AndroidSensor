package accelerometer.client.model;

public class Acceleration {

    private double x;
    private double y;
    private double z;

    private long timestamp;

    public Acceleration(double x, double y, double z, long timestamp) {
        this.x= new Double(x);
        this.y= new Double(y);
        this.z= new Double(z);
        this.timestamp = timestamp;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

}
