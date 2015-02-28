package id1217_hw3;

import java.util.Date;
import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Bathroom class represents a bathroom which will allow concurrent males
 * or concurrent females to use it but not both genders at one time.
 * The class will use only java.util.concurrent.Semaphores for it's 
 * synchronization.
 * 
 * Developed for course ID1217 Parallel Programming, KTH
 * 
 * @author Kim
 */
public class Bathroom {

    private final Semaphore femaleBathroomSem;  //Number of locks acquired represents the number of female users.
    private final Semaphore maleBathroomSem;    //Number of locks acquired represents the number of male users.
    private final Semaphore emptySemLock = new Semaphore(1);    //When acquired, the bathroom is NOT empty.
    private final Semaphore maleBathroomSemLock = new Semaphore(1); //Used to block males from entering.
    private final Semaphore femaleBathroomSemLock = new Semaphore(1); //Used to block females from entering.
    private final int numberOfStalls;
    private final static int USE_TIME = 3 * 100; 
    public final static int NUMBER_OF_STALLS_DEFAULT = 1000;

    /**
     * Sets the number of stalls (i.e. concurrent users) to 
     * NUMBER_OF_STALLS_DEFAULT.
     */
    public Bathroom() {
        this(NUMBER_OF_STALLS_DEFAULT);
    }

    /**
     * Sets the number of stalls (i.e. concurrent users) to the specified
     * parameter.
     * @param numberOfStalls The number of concurrent users to use the bathroom
     */
    public Bathroom(int numberOfStalls) {
        if (numberOfStalls < 1 || numberOfStalls > 10000) {
            throw new IllegalArgumentException("The number of stalls specified was not allowed.");
        }
        this.numberOfStalls = numberOfStalls;
        this.femaleBathroomSem = new Semaphore(numberOfStalls);
        this.maleBathroomSem = new Semaphore(numberOfStalls);
    }

    /**
     * Simulates a bathroom usage by letting the specified thread sleep for 
     * a random amount of time.
     * @param wt The thread that will be put to sleep.
     * @throws InterruptedException The exception will be thrown if the sleep 
     * were to be interrupted.
     */
    public void use(WorkerThread wt) throws InterruptedException {
        Random rand = new Random();
        int time = rand.nextInt(USE_TIME);

        System.out.println(new Timestamp(new Date().getTime()) + ": "  +
                wt.getGender() + "[" + wt.getId() + "] uses the bathroom.");
        try {
            wt.sleep(time);
        } catch (InterruptedException ex) {
            System.err.println(wt.getGender() + "[" + wt.getId() + ": " + ex.getMessage());
        }
    }

    /**
     * Simulates the worker to leave the bathroom.
     * @param wt The worker that will leave bathroom.
     * @throws InterruptedException The exception will be thrown if the 
     * acquiring of the semaphore were to be interrupted.
     */
    public void leave(WorkerThread wt) throws InterruptedException {

        if (wt.getGender() == WorkerThread.Gender.FEMALE) {
            femaleBathroomSem.release();
            
            femaleBathroomSemLock.acquire();
            if(femaleBathroomSem.availablePermits() == numberOfStalls) {
                emptySemLock.release();
            }
            femaleBathroomSemLock.release();
        } else if (wt.getGender() == WorkerThread.Gender.MALE) {
            maleBathroomSem.release();
            
            maleBathroomSemLock.acquire();
            if(maleBathroomSem.availablePermits() == numberOfStalls) {
                emptySemLock.release();
            }
            maleBathroomSemLock.release();
        } else {
            throw new UnsupportedOperationException("The worker thread has an unsupported gender type.");
        }
        System.out.println(new Timestamp(new Date().getTime()) + ": "  +
                wt.getGender() + "[" + wt.getId() + "] has left the bathroom.");
    }

    /**
     * Simulates the worker to be queueing to the bathroom and then entering.
     * @param wt The worker that will be queueing
     */
    public void enter(WorkerThread wt) {
        System.out.println(new Timestamp(new Date().getTime()) + ": "  +
                wt.getGender() + "[" + wt.getId() + "] is QUEUED for the bathroom.");
        if (wt.getGender() == WorkerThread.Gender.FEMALE) {
            try {
                
                while (!femaleBathroomSemLock.tryAcquire()) {
                    wt.sleep(200);
                }
                femaleBathroomSem.acquire();
                
                if(femaleBathroomSem.availablePermits() == numberOfStalls - 1)
                    while(!emptySemLock.tryAcquire()) {
                        wt.sleep(200);
                    }
                femaleBathroomSemLock.release();
            } catch (InterruptedException ex) {
                System.err.println(wt.getGender() + "[" + wt.getId() + ": " + ex.getMessage());
            }
        } else if (wt.getGender() == WorkerThread.Gender.MALE) {
            try {
                while (!maleBathroomSemLock.tryAcquire()) {
                    wt.sleep(200);
                }
                maleBathroomSem.acquire();
                
                if(maleBathroomSem.availablePermits() == numberOfStalls - 1)
                    while(!emptySemLock.tryAcquire()) {
                        wt.sleep(200);
                    }
                maleBathroomSemLock.release();
            } catch (InterruptedException ex) {
                System.err.println(wt.getGender() + "[" + wt.getId() + ": " + ex.getMessage());
            }
        } else {
            throw new UnsupportedOperationException("The worker thread has an unsupported gender type.");
        }
        System.out.println(new Timestamp(new Date().getTime()) + ": "  +
                wt.getGender() + "[" + wt.getId() + "] entered the bathroom.");
    }

}
