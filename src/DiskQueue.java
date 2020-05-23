
// Monitor to schedule requests to the disk

public class DiskQueue {

    private static Disk disk = null;

    // number to associate the coming thread with
    private static int threadNum = 0;

    // number to know, which thread number will go to
    // read/write operations in that iteration
    private static int currentThread = 0;

    public DiskQueue( Disk disk) {
        this.disk = disk;
    }

    /** Gets the number of blocks on the disk
     * @return the number of blocks as an int
     */
    public int getBlockCount(){
        int nBlocks = disk.DISK_SIZE;
        return nBlocks;
    }

    /** Gets the size of each block in the disk
     * @return the size of each block as an int
     */
    public int getBlockSize(){
        int bSize = disk.BLOCK_SIZE;
        return bSize;
    }

    /** Gives a number that threads can associate to themselves
     * in order to know the order in which the threads came
     * @return the size of each block as an int
     */
    public static synchronized int numberThreads (){
        int comingThread = threadNum;
        threadNum++;

        return comingThread;
    }


    /** Notifies when the read/write operation has finished,
     * to avoid time synchronizations
     */
    public static void updateCurrentThread(){
        ++currentThread;
    }

    /** Makes sure all the read and write calls to disk are
     * one at a time. No matter if its a sequence of all reads,
     * all writes, or mixed.
     * If read, modifies data[] with the disk information at block number
     * If write modifies the disk at block number with data[]
     * Operation type is 0 when read operation, and 1 if write operation
     */

    public synchronized int readWriteDiskBlock( int bNumber, byte data[], int operationType ){

        try{

            int myNumber = numberThreads();

            while( myNumber != currentThread ){
                wait();
            }

            if ( operationType == Kernel.CASE_READ ) {
                disk.beginRead(bNumber, data);
            }
            else if ( operationType == Kernel.CASE_WRITE ){
                disk.beginWrite(bNumber, data);
            }
            else {
                System.err.println(" You entered a wrong operation. Only 0 for read operation" +
                        "and 1 for write operation.");
            }

            while( myNumber == currentThread){
                Thread.sleep(1);
                /* wait disk delay */ }

            notifyAll();

        }
        catch (InterruptedException e) {
        e.printStackTrace();
        }

        return 0;
    }

}
