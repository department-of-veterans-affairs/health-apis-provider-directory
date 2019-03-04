package gov.va.api.health.providerdirectory.api.resources;

public class Location {

    Status status;

    String name;

    String description;

    Mode mode;
    public enum Mode {
        instance,
        kind
    }

    public enum Status {
        active,
        suspended,
        inactive
    }

}
