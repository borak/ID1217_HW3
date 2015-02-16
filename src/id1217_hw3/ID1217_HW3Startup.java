package id1217_hw3;

import java.util.Random;

/**
 *
 * @author Kim
 */
public class ID1217_HW3Startup {

    public static final int NUMBER_OF_WORKERS_MAXIMUM = 30;
    public static final int NUMBER_OF_WORKERS_DEFAULT = 10;
    
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
        
        BathroomThread bathroom = new BathroomThread();
        bathroom.start();

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
