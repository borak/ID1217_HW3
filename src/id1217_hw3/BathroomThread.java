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

/**
 *
 * @author Kim
 */
public class BathroomThread extends Thread {

    private LinkedList<WorkerThread> menQueue = new LinkedList<>();
    private LinkedList<WorkerThread> womenQueue = new LinkedList<>();
    private boolean isRunning = false;
    private Semaphore emptyQueueSem = new Semaphore(1);
    private int nm = 0, nw = 0;
    private Lock nmLock = new ReentrantLock();
    private Lock nwLock = new ReentrantLock();
    private Lock mqueueLock = new ReentrantLock();
    private Lock wqueueLock = new ReentrantLock();
    
    public BathroomThread() {
        super();
    }

    @Override
    public void run() {
        System.out.println("The bathroom has opened.");
        isRunning = true;

        while (isRunning) {
            
            //lock
            if (menQueue.isEmpty() && womenQueue.isEmpty()) {
                try {
                    emptyQueueSem.acquire();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } 
            
            boolean isIllegal = false;
            nmLock.lock();
            nwLock.lock();
            if(nm > 0 && nw > 0) isIllegal = true;
            nwLock.unlock();
            nmLock.unlock();
            
            if(isIllegal) {
                throw new IllegalStateException("There can not be active "
                        + "bathroom users of both ");
            }
            
            nmLock.lock();
            if(nm > 0) {
                
            } 
            nmLock.unlock();
            
            nwLock.lock();
            if (nw > 0) {
                
            }
            nwLock.unlock();
            
            
        }

        System.out.println("Bathroom has closed.");
    }
    
    private void useBathroom(Thread t) throws InterruptedException {
        Random rand = new Random(10 * 1000);
        long time = rand.nextLong();
        t.sleep(time);
    }

    public void leave(WorkerThread wt) {
        if(wt.getGender() == WorkerThread.Gender.FEMALE) {
            nwLock.lock();
            if(nw == 0) throw new IllegalStateException("Woman["+wt.getId()+"] can "
                    + "not leave the room because the Woman is not using the bathroom.");
            nw--;
            nwLock.unlock();
        } else if(wt.getGender() == WorkerThread.Gender.MALE) {
            nmLock.lock();
            if(nm == 0) throw new IllegalStateException("Man["+wt.getId()+"] can "
                    + "not leave the room because the Man is not using the bathroom.");
            nm--;
            nmLock.unlock();
        } else {
            throw new UnsupportedOperationException("The worker thread has an unsupported gender type.");
        }
    }

    public void enter(WorkerThread wt) {
        if(wt.getGender() == WorkerThread.Gender.FEMALE) {
            System.out.println("Woman[" + wt.getId() + "] entered the bathroom.");

            wqueueLock.lock();
            womenQueue.addLast(wt);
            wqueueLock.unlock();

            emptyQueueSem.release();
        } else if(wt.getGender() == WorkerThread.Gender.MALE) {
            System.out.println("Man[" + wt.getId() + "] entered the bathroom.");
        
            mqueueLock.lock();
            menQueue.addLast(wt);
            mqueueLock.unlock();

            emptyQueueSem.release();
        } else {
            throw new UnsupportedOperationException("The worker thread has an unsupported gender type.");
        }
    }
}
