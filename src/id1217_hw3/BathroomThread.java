/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id1217_hw3;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kim
 */
public class BathroomThread extends Thread {

    private final LinkedList<WorkerThread> maleQueue = new LinkedList<>();
    private final LinkedList<WorkerThread> femaleQueue = new LinkedList<>();
    private boolean isRunning = false;
    private final Semaphore emptyQueueSem = new Semaphore(1);
    private final Semaphore femaleBathroomSem; // if acquired, blocks the other gender
    private final Semaphore maleBathroomSem;
    private int nm = 0, nf = 0; // active bathroom users
    private final Lock nmLock = new ReentrantLock(); // Change locks to semaphores
    private final Lock nfLock = new ReentrantLock();
    private final Lock mqueueLock = new ReentrantLock();
    private final Lock fqueueLock = new ReentrantLock();
    private final static long USE_TIME = 10 * 1000; // Represents 10 minutes, but performs in 10 seconds
    private final int numberOfStalls;
    
    public BathroomThread() {
        this(100);
    }
    
    public BathroomThread(int numberOfStalls) {
        super();
        if(numberOfStalls < 1 || numberOfStalls > 10000) 
            throw new IllegalArgumentException("The number of stalls specified was not allowed.");
        this.numberOfStalls = numberOfStalls;
        this.femaleBathroomSem = new Semaphore(numberOfStalls);
        this.maleBathroomSem = new Semaphore(numberOfStalls);
    }

    private boolean isEmpty() {
        boolean isEmpty = false;
        fqueueLock.lock();
        mqueueLock.lock();
        if (maleQueue.isEmpty() && femaleQueue.isEmpty()) {
            isEmpty = true;
        } 
        fqueueLock.unlock();
        mqueueLock.unlock();
        return isEmpty;
    }
    
    private void checkState() {
        boolean isIllegal = false;
        nmLock.lock();
        nfLock.lock();
        if(nm > 0 && nf > 0) isIllegal = true;
        nfLock.unlock();
        nmLock.unlock();

        if(isIllegal) {
            throw new IllegalStateException("There can not be active "
                    + "bathroom users of both ");
        }
    }
    
    // nf == 0 && nm >= 0
    private void acquireMaleBathroom() throws InterruptedException {
        if(gotActiveFemales()) {
            
        }
        
    }
    
    public void use(WorkerThread wt) {
        
        if(wt.getGender() == WorkerThread.Gender.FEMALE) {
            try {
                /*
                boolean hasWaited = false;
                if(gotActiveMales()) {
                hasWaited = true;
                //wait until nm == 0
                // after waiting, pass the baton
                try {
                maleBathroomSem.acquire(numberOfStalls); // Will be unblocked when there is no male in the bathroom
                    
                } catch (InterruptedException ex) {
                Logger.getLogger(BathroomThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
                
                if(!hasWaited && !maleQueueIsEmpty()) {
                //wait nf == 0
                //wait nm == 0
                //acquire
            }// lock nm == 0 somehow
                */
                 
                maleBathroomSem.acquire(numberOfStalls); // Will be unblocked when there is no male in the bathroom
            } catch (InterruptedException ex) {
                Logger.getLogger(BathroomThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                femaleBathroomSem.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(BathroomThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            nfLock.lock();
            try {
                nf++;
            } finally {
                nfLock.unlock();
            }
            useBathroomRandomTime(wt);
            
        } else if(wt.getGender() == WorkerThread.Gender.MALE) {
            //handle ^ first
            /*nfLock.lock();
            if(nf > 0) 
                //wait
                ;
            nfLock.unlock();
            
            nmLock.lock();
            nm++;
            nmLock.unlock();*/
        } else {
            throw new UnsupportedOperationException("The worker thread has"
                    + " an unsupported gender type.");
        }
    }
    
    private boolean gotActiveFemales() {
        boolean gotActiveFemales = true;
        nfLock.lock();
        try {
            gotActiveFemales = (nf > 0);
        } finally {
            nfLock.unlock();
        }
        return gotActiveFemales;
    }
    
    private boolean gotActiveMales() {
        boolean gotActiveMales = true;
        nmLock.lock();
        try {
            gotActiveMales = (nm > 0);
        } finally {
            nmLock.unlock();
        }
        return gotActiveMales;
    }
    
    private boolean maleQueueIsEmpty() {
        boolean isEmpty = true;
        mqueueLock.lock();
        try {
            isEmpty = maleQueue.isEmpty();
        } finally {
            mqueueLock.unlock();
        }
        return isEmpty;
    }
    
    private boolean femaleQueueIsEmpty() {
        boolean isEmpty = true;
        fqueueLock.lock();
        try {
            isEmpty = femaleQueue.isEmpty();
        } finally {
            fqueueLock.unlock();
        }
        return isEmpty;
    }
    
    private void useBathroomRandomTime(WorkerThread wt) {
        Random rand = new Random(USE_TIME);
        long time = rand.nextLong();
        
        try {
            wt.sleep(time);
        } catch (InterruptedException ex) {
            System.out.println("Interruption of use.");
        }
    }

    public void leave(WorkerThread wt) {
        if(wt.getGender() == WorkerThread.Gender.FEMALE) {
            nfLock.lock();
            try {
                if(nf == 0) throw new IllegalStateException(wt.getGender()+"["+wt.getId()+"] can "
                        + "not leave the room because the Woman is not using the bathroom.");
                nf--;
            } finally {
                nfLock.unlock();
                femaleBathroomSem.release();
            }
        } else if(wt.getGender() == WorkerThread.Gender.MALE) {
            nmLock.lock();
            try {
                if(nm == 0) throw new IllegalStateException(wt.getGender()+"["+wt.getId()+"] can "
                        + "not leave the room because the Man is not using the bathroom.");
                nm--;
            } finally {
                nmLock.unlock();
                maleBathroomSem.release();
            }
        } else {
            throw new UnsupportedOperationException("The worker thread has an unsupported gender type.");
        }
    }

    public void enter(WorkerThread wt) {
        if(wt.getGender() == WorkerThread.Gender.FEMALE) {
            System.out.println(wt.getGender()+"[" + wt.getId() + "] entered the bathroom.");

            fqueueLock.lock();
            try {
                femaleQueue.addLast(wt);
            } finally {
                fqueueLock.unlock();
            }

            emptyQueueSem.release();
        } else if(wt.getGender() == WorkerThread.Gender.MALE) {
            System.out.println(wt.getGender()+"[" + wt.getId() + "] entered the bathroom.");
        
            mqueueLock.lock();
            try {
                maleQueue.addLast(wt);
            } finally {
                mqueueLock.unlock();
            }

            emptyQueueSem.release();
        } else {
            throw new UnsupportedOperationException("The worker thread has an unsupported gender type.");
        }
    }
    
}
