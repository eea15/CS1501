// Emma Akbari project 5 (eea21)

public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};

	private byte[] val;

	/**
	 * Construct the HeftyInteger from a given byte array
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b) {
		val = b;
	}

	/**
	 * Return this HeftyInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other HeftyInteger to sum with this
	 */
	public HeftyInteger add(HeftyInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public HeftyInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other HeftyInteger to subtract from this
	 * @return difference of this and other
	 */
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger multiply(HeftyInteger other) {
		

		// determine if the result should be negative
		boolean neg = (this.isNegative() && !other.isNegative() || !this.isNegative() && other.isNegative());

		// make operands positive
		HeftyInteger tHis = this;
		if(this.isNegative()) tHis = this.negate();
		if(other.isNegative()) other = other.negate();
		
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (tHis.length() < other.length()) {
			a = other.getVal();
			b = tHis.getVal();
		}
		else {
			a = tHis.getVal();
			b = other.getVal();
		}

		// gradeschool multiplication approach
		int prod = 0; // product calculated each iteration
		byte[] prodArray;
		byte[] toResult = new byte[a.length * b.length + 1];
		HeftyInteger res = new HeftyInteger(toResult); // result = 0
		int pad = 0;
		int placeholder = 0;
		for(int bLoc = b.length - 1; bLoc >= 0; bLoc--) { // multiplier

			for(int aLoc = a.length - 1; aLoc >= 0; aLoc--) { // multiplicand

				// compute product
				prod = (b[bLoc] & 0xFF) * (a[aLoc] & 0xFF);
				// convert to bytes
				prodArray = intToArray(prod, pad);
				// add to result
				res = res.add(new HeftyInteger(prodArray));
				pad += 8;
			}

			placeholder += 8;
			pad = placeholder;
		}

		if(neg) res = res.negate();

		return res;
	}

	/**
	 * helper method for multiplication
	 * @param num number to place in array
	 * @return num as byte[]
	 */
	private byte[] intToArray(int num, int shift) {

		int zeros = shift/8;
		byte[] arr = new byte[3 + zeros];
		arr[2] = (byte) (num & 0xFF);
		arr[1] = (byte) ((num >> 8) & 0xFF);
		arr[0] = (byte) ((num >> 16) & 0xFF);

		return arr;
	}

	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public HeftyInteger[] XGCD(HeftyInteger other) {

		HeftyInteger[] euclid = XGCD_helper(this, other);
		return euclid;
	 }

	 // Help calculate xgcd
	 private HeftyInteger[] XGCD_helper(HeftyInteger a, HeftyInteger b) {

	 	if(b.equals_zero()) { // base case remainder = 0
	 		HeftyInteger[] base = new HeftyInteger[3];
	 		byte[] one = {1};
	 		byte[] zero = {0};
	 		base[0] = a;
	 		base[1] = new HeftyInteger(one);
	 		base[2] = new HeftyInteger(zero);

	 		return base;   
	 	} else {
	 		HeftyInteger[] ab = a.divide(b);
	 		HeftyInteger q = ab[0]; // quotient
	 		HeftyInteger r = ab[1]; // remainder

	 		HeftyInteger[] results = XGCD_helper(b, r); // gcd(b, a % b)
	 		HeftyInteger gcd = results[0];
	 		HeftyInteger x = results[2];
	 		HeftyInteger y = results[1].subtract(q.multiply(x)); 

	 		results[0] = gcd;
	 		results[1] = x;
	 		results[2] = y;

	 		return results; // gcd(a, b) = ax + by = gcd
	 	}

	 }

	 /**
	 * Determine if a HeftyInteger = 0
	 * @return true if = 0
	 */
	 private boolean equals_zero() {

	 	byte[] bytes = this.getVal();

	 	// iterate thru bytes to check if all 0s
	 	for(int i = 0; i < bytes.length; i++) {
	 		if(bytes[i] != 0) return false;
	 	}

	 	return true;
	 } 

	 /**
	 * Naive approach to division
	 * @param other the divisor
	 * @return this / divisor where:
	 * 		0: quotient
	 *		1: remainder
	 */
	 public HeftyInteger[] divide(HeftyInteger other) { 

	 	HeftyInteger remainder = this;
	 	int quotient = 0;

	 	while(!remainder.subtract(other).isNegative()) { // dividend >= divisor
	 		remainder = remainder.subtract(other);
	 		quotient++;
	 	}

	 	int quoCopy = quotient;
	 	int arraySize = 0;
	 	do { // compute size of byte array for quotient
	 		quoCopy = quoCopy >> 8;
	 		arraySize++;
	 	} while(quoCopy != 0);

	 	byte[] quotientByte = new byte[arraySize];
	 	// place quotient in quotientByte
	 	for(int i = 0; i < arraySize; i++) {
	 		int shift = (arraySize - (i+1)) * 8;
	 		quotientByte[i] = (byte) (quotient >> shift);
	 	}

	 	HeftyInteger q = new HeftyInteger(quotientByte);
	 	HeftyInteger[] result = new HeftyInteger[2];
	 	result[0] = q;
	 	result[1] = remainder;
	 	return result;
	 }

}
