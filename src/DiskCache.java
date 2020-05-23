
// Monitor to schedule requests to the disk

import static java.lang.System.arraycopy;
import static java.lang.System.out;

public class DiskCache {

    private static Disk disk = null;

    private static DiskQueue scheduler = null;

    // Cache Size
    private static int cacheSize;

    // whole cache
    private static byte [] cache;

    // buffer for operations
    private static byte[] buff;

    // target block
    private static int targetBlock;

    // read and write count
    public static int readCount;
    public static int writeCount;

    // number to associate the coming thread with
    private static int threadNum = 0;

    // number to know, which thread number will go to
    // read/write operations in that iteration
    private static int currentThread = 0;

    public DiskCache( Disk disk, DiskQueue scheduler, int cacheSize) {
        this.disk = disk;
        this.cacheSize = cacheSize;
        this.cache = new byte[(cacheSize+1) * disk.BLOCK_SIZE];
        this.buff = new byte[disk.BLOCK_SIZE];
        this.scheduler = scheduler;
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

    /** Makes sure all the read and write calls to cache are
     * one at a time. No matter if its a sequence of all reads,
     * all writes, or mixed.
     * If read, modifies data[] with the cache information at block number
     * If write modifies the cache at block number with data[]
     * Operation type is 0 when read operation, and 1 if write operation
     */

    public synchronized int readWriteCache( int bNumber, byte data[], int operationType) {

        this.buff = data;
        this.targetBlock = bNumber;

        if( targetBlock > this.cacheSize){
            if ( operationType == Kernel.CASE_READ ) {
                scheduler.readWriteDiskBlock(targetBlock, buff, Kernel.CASE_READ);
            }
            else if ( operationType == Kernel.CASE_WRITE ){
                scheduler.readWriteDiskBlock(targetBlock, buff, Kernel.CASE_WRITE);
            }
            else {
                System.err.println("C You entered a wrong operation. Only 0 for read operation" +
                        "and 1 for write operation.");
            }
        }
        else{
            try{
                int myNumber = numberThreads();

                while( myNumber != currentThread ){
                    wait();
                }

                if ( operationType == Kernel.CASE_READ ) {
                    arraycopy(
                            this.cache, targetBlock * disk.BLOCK_SIZE,
                            data, 0,
                            disk.BLOCK_SIZE);
                    readCount++;

                }
                else if ( operationType == Kernel.CASE_WRITE ){

                    arraycopy(
                            data, 0,
                            this.cache, targetBlock * disk.BLOCK_SIZE,
                            disk.BLOCK_SIZE);
                }
                else {
                    System.err.println("C You entered a wrong operation. Only 0 for read operation" +
                            "and 1 for write operation.");
                }

                ++currentThread;

                notifyAll();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

}

