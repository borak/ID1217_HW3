/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id1217_hw3;

import java.util.Random;

/**
 *
 * @author Kim
 */
public class WorkerThread extends Thread implements Runnable {

    public enum Gender {
        MALE, FEMALE;
    }
    private BathroomThread bathroom;
    private int id;
    private Gender gender;
    
    public WorkerThread(int id, Gender gender, BathroomThread bathroom) {
        super();
        this.bathroom = bathroom;
        this.id = id;
        this.gender = gender;
    }
    
    @Override
    public void run() {
        System.out.println(gender.toString()+"["+id+"] started working.");
        Random rand = new Random();
        
        while(true) {
            long workTime = rand.nextInt(1000 * 30);
            System.out.println("gender.toString()"+id+"] works for " + workTime + " minutes.");
            try {
                this.sleep(workTime);
            } catch (InterruptedException ex) {
                System.out.println(gender.toString()+"["+id+"]'s work time interrupted.");
            }
            
            bathroom.enter(this);
            bathroom.use(this);
            bathroom.leave(this);
        }
        //System.out.println("Woman["+id+"] finished.");
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    public Gender getGender() {
        return gender;
    }
}
