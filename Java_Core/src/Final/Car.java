package Final;

public final class Car {  // not inherit this class.
    private String brand;
    private  String model;
    private static final int speed = 300; // not update this variable value;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public final void set(){ // not override child class in this method.
        System.out.println("4 sets here.");
    }
}
