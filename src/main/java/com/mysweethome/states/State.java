/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mysweethome.states;

/**
 *
 * @author Ste
 */
public enum State {
    ON("On"),
    MANUAL("Manual"),
    OFF("Off");
    
    String iStatus;
    
    State(String aStatus){
        iStatus = aStatus;
    }
    
    public String toString(){
        return iStatus;
    }
    
}
