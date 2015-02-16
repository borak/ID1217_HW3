/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id1217_hw3;

/**
 *
 * @author Kim
 */
public class Man implements Runnable {

    private Bathroom bathroom;
    private int id;
    
    public Man(int id, Bathroom bathroom) {
        this.bathroom = bathroom;
        this.id = id;
    }
    
    @Override
    public void run() {
        System.out.println("Man["+id+"] running.");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
