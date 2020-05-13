// driver for project 5 to figure out wth is going on
import java.math.BigInteger;

public class HeftyTest {

	public static void main(String[] args) {

		/*
		byte[] array1 = {1, 1, -1};
		byte[] array2 = {-2, 0, 3};

		HeftyInteger sum = new HeftyInteger(array1);
		sum = sum.add(new HeftyInteger(array2));

		byte[] result = sum.getVal(); // -1, 1, 2

		for(byte x: result) {
			System.out.println(x);
		}
		*/

		/*
		BigInteger one = new BigInteger("42");
		BigInteger two = new BigInteger("5");
		BigInteger quotient = one.divide(two);
		System.out.println(quotient);*/

		byte[] ah = {3, -55};
		byte[] ah2 = {1, -92};
		HeftyInteger uno = new HeftyInteger(ah);
		HeftyInteger dos = new HeftyInteger(ah2);
		HeftyInteger[] unoDos = uno.divide(dos);
		byte[] result1 = unoDos[0].getVal();
		byte[] result2 = unoDos[1].getVal();
		System.out.println("quotient = " + new BigInteger(result1)); 
		System.out.println("remainder = " + new BigInteger(result2));

	}
}