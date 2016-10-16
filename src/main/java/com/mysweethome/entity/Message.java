/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mysweethome.entity;

/**
 *
 * @author Ste
 */
public class Message {
    
    User iUser;
    String iBody;

    public Message(User aUser, String aBody){
        iUser = aUser;
        iBody = aBody;
    }

    public User getUser() {
        return iUser;
    }

    public void setUser(User aUser) {
        this.iUser = aUser;
    }
    
    public String getBody() {
        return iBody;
    }

    public void setBody(String aBody) {
        this.iBody = aBody;
    }
    
}
