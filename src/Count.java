
/** A very tiny example MiniKernel program.
 * Count down from 10 to 1.
 * @see Kernel
 */
public class Count {
    /** No public instances. */
    private Count() {}

    /** The main program.
     * @param args ignored.
     */
    public static void main(String args[]) {
        for (int i = 10; i > 0; i--) {
            Library.output("Counting: " + i + "\n");
        }
        Library.output("*** Blast off ***!\n");
    }
}
