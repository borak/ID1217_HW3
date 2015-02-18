package id1217_hw3;

import java.util.Random;

/**
 * This application is a solution to the unisex bathroom problem using only 
 * semaphores for synchronization.
 * This class will start the workers and they will all have access to the same 
 * bathroom.
 * 
 * @author Kim
 */
public class ID1217_HW3Startup {

    public static final int NUMBER_OF_WORKERS_MAXIMUM = 100;
    public static final int NUMBER_OF_WORKERS_DEFAULT = 10;
    
    /**
     * Sets up a bathroom and starts the specified amount (the default is 10) 
     * of workers.
     * @param args [0] The number of worker threads that will run and simulate 
     * to work and use the bathroom. 
     * @throws InterruptedException If any 
     */
    public static void main(String[] args) throws InterruptedException {
        
        int numberOfWorkers;
        
        if(args.length > 0) {
            try {
                int number = Integer.parseInt(args[0]);
                if(number < 1 || number > NUMBER_OF_WORKERS_MAXIMUM) 
                    throw new IllegalArgumentException("Invalid number of "
                            + "persons specified (" + args[0] + ")");
                
                numberOfWorkers = number;
            } catch(NumberFormatException nfe) {
                throw new IllegalArgumentException("Could not parse " + 
                        args[0] + " to a number.");
            }
        } else {
            numberOfWorkers = NUMBER_OF_WORKERS_DEFAULT;;
        }
        
        Bathroom bathroom = new Bathroom();

        Random rand = new Random();
        for(int i = 0; i < numberOfWorkers; i++) {
            int type = rand.nextInt(2);
            Thread t;
            if(type == 0) t = new WorkerThread(i, WorkerThread.Gender.MALE, bathroom);
            else t = new WorkerThread(i, WorkerThread.Gender.FEMALE, bathroom);
            t.start();
        }
 
    }

}
