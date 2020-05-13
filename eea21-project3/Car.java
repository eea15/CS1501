// Emma Akbari (eea21) 
// class representing cars

public class Car {

	// car properties
	private String vin;
	private String make;
	private String model;
	private int price;
	private int mileage;
	private String color;

	public Car() {} // empty constructor

	// initialize all properties
	public Car(String v, String ma, String mo, int p, int mi, String c) {
		vin = v;
		make = ma;
		model = mo;
		price = p;
		mileage = mi;
		color = c;
	}

	// accessors and mutators for properties
	public String get_vin() {
		return vin;
	}

	public void set_vin(String v) {
		vin = v;
	}

	public String get_make() {
		return make;
	}

	public void set_make(String m) {
		make = m;
	}

	public String get_model() {
		return model;
	}

	public void set_model(String m) {
		model = m;
	}

	public int get_price() {
		return price;
	}

	public void set_price(int p) {
		price = p;
	}

	public int get_mileage() {
		return mileage;
	}

	public void set_mileage(int m) {
		mileage = m;
	}

	public String get_color() {
		return color;
	}

	public void set_color(String c) {
		color = c;
	}

	// return properties as String
	public String toString() {
		String s = "VIN: " + vin + "\n"
				 + "make: " + make + "\n"
				 + "model: " + model + "\n"
				 + "price: $" + price + "\n"
				 + "mileage: " + mileage + "\n"
				 + "color: " + color + "\n";

		return s;
	}
}