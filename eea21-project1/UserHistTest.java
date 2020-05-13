// Emma Akbari (eea21)
// test class for UserHist

public class UserHistTest {

	public static void main(String[] args) {

		UserHist test = new UserHist();

		// test getting words in DLB
		// test getting words not in DLB
		// test repeat words (print out their number)

		//test.print_hash();
		
		test.put("opet");
		test.put("o");
		
		String[] x = test.get_prefix_keys("o");
		for(String y: x) {
			System.out.println(y);
		}
	}
}