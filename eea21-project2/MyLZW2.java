  
/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;  // number of input chars
    private static int L = 512;        // number of codewords = 2^W
    private static int W = 9;          // codeword width
    private static String mode = "n";  // compression/expansion mode
    private static boolean justResized = false; // for debugging
    private static boolean justReset = false; // for reset mode
    private static double oldRatio = 0; // initial compression ratio
    private static double newRatio = 0; // compression ratio regenerated each iteration

    public static void compress(String m) { 
        // set mode based on user input & output to compressed file
        set_mode(m);
        System.err.println("Mode: " + mode);
        BinaryStdOut.write(m.charAt(0), 8); 

        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            //if(justResized) System.err.println("\nWriting " + st.get(s) + " for prefix " + s + ".");
            int t = s.length();

            if (t < input.length() && code < L){
                if(justResized) {
                    System.err.println("Adding String: " + input.substring(0, t + 1) + ", Codeword: " + code);
                    justResized = false; // should be 512
                }
                st.put(input.substring(0, t + 1), code++); // Add s to symbol table.
            } 
            // increase codeword size once codebook full
            if(t < input.length() && code == L) resize(input.substring(0, t + 1), (code - 1)); 
            // monitor compression ratio in monitor mode once codebook full
            if(t < input.length() && code == 65536 && mode.equals("m")) {
                // only calculate oldRatio when codebook first fills
                if(oldRatio == 0) {
                    System.err.println("monitoring compression ratio");
                    oldRatio = find_ratio(t); 
                } 
                newRatio = find_ratio(t); // calculate new ratio each time
                // reset if above threshold
                if(over_threshold()) {
                    // reset compression ratio measurements
                    oldRatio = 0;
                    newRatio = 0; 
                    System.err.print("over threshold, resetting codebook at ");
                    System.err.println(s + ", " + st.get(s));
                    st = reset_compression();
                    code = R+1;
                }
            }
            // reset the codebook once codebook max and in reset mode
            if(t < input.length() && code == 65536 && mode.equals("r")) {
                st = reset_compression();  
                code = R+1; // R is codeword for EOF
            }
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
        // read mode from file & set
        mode = Character.toString(BinaryStdIn.readChar()); // string vs. char dilemma
        System.err.println("Mode: " + mode);
        String[] st = new String[65536]; // largest possible codebook
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF


        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W); // detect W by size of codebook (i)
            if (codeword == R) break;
            String s = st[codeword];

            if(justResized) {
                System.err.println("Next codeword: " + codeword); // should be 512
            }

            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L && !justReset && i < 65535) st[i++] = val + s.charAt(0); // only add to codebook if not just reset
            else justReset = false; // if just reset, change back to not

            if(justResized) {
                System.err.println("String: " + st[i-1] + ", Codeword: " + codeword);
                justResized = false; 
            }

            if(i == L-1) resize(st[i-1], (i-1)); // resize codebook once L is full
            //System.err.println("Codeword: " + (i-1) + ", String: " + st[i-1]);
            // monitor compression ratio in monitor mode once codebook full
            if(i == 65535 && mode.equals("m")) {
                // only calculate oldRatio when codebook first fills
                if(oldRatio == 0) {
                    System.err.println("monitoring compression ratio");
                    oldRatio = find_ratio(s.length()); 
                } 
                newRatio = find_ratio(s.length()); // calculate new ratio each time
                System.err.println("new ratio: " + newRatio);
                // reset if above threshold
                if(over_threshold()) {
                    // reset compression ratio measurements
                    oldRatio = 0;
                    newRatio = 0; 
                    System.err.print("over threshold, resetting codebook at ");
                    System.err.println(s + ", " + codeword);
                    st = reset_expansion();
                    i = 257;
                }
            }
            // reset the codebook once codebook max and in reset mode
            if(i == 65535 && mode.equals("r")) {
                st = reset_expansion();
                i = 257;
            }
            val = s;
        } 
        BinaryStdOut.close();
    }

    // change codeword width once codebook has been filled
    private static void resize(String s, int codeword) {
        if(W < 16) { // only resize up to 16 bits
            W++;
            L = (int)Math.pow(2, W);
            System.err.println("Switching after (" + s + ", " + codeword + ") to " + W + " bits and " + L + " length codebook");
        }

        justResized = true;
    }

    // reset the DLB codebook once all 16 bits have been used
    private static TST<Integer> reset_compression() {
        System.err.println("resetting codebook");

        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);

        // reset codeword length
        L = 512;        // number of codewords = 2^W
        W = 9;          // codeword width

        return st;
    }

    // reset the array codebook once all 16 bits have been used
    private static String[] reset_expansion() {
        System.err.println("resetting codebook:");

        // initialize symbol table with all 1-character strings
        String[] st = new String[65536]; // largest possible codebook
        int j;
        for (j = 0; j < R; j++)
            st[j] = "" + (char) j;
        st[j++] = "";   // (unused) lookahead for EOF

        // reset codeword length
        L = 512;        // number of codewords = 2^W
        W = 9;          // codeword width

        justReset = true;

        return st;
    }

    // determine whether the codebook should be reset 
    // in monitor mode based on threshold
    private static boolean over_threshold() {
        return((oldRatio/newRatio) > 1.1);
    }

    // calculate ratio of uncompressed to compressed data
    // @param uncompressed String length
    // @return ratio size in bits
    private static double find_ratio(int len) {
        double uncompressed = len * 8; // size of String in bits
        double compressed = W; // codeword size in bits
        return(uncompressed/compressed);
    }

    // set mode based on user input
    private static void set_mode(String m) {
        if(m.equals("n")) {
            // do nothing mode/default
        } else if(m.equals("r")) {
            mode = "r"; // reset mode
        } else if(m.equals("m")) {
            mode = "m"; // monitor mode
        } else {
            throw new IllegalArgumentException("Invalid mode");
        }
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress(args[1]);
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}