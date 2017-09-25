package accelerometer.client.model;

public enum ActivityEnum {

    WALKING("Walking"),
    RUNNING("Running"),
    STANDING("Standing"),
    UPSTAIRS("Upstairs"),
    DOWNSTAIRS("Downstairs");

    private String label;

    ActivityEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

}
