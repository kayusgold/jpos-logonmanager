package com.kayusgold.logonmanager.controller;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

public class MyISOResponseListener implements ISORequestListener {
    @Override
    public boolean process(ISOSource source, ISOMsg m) {
        //log the source and the message
        System.out.println("ISOResponseListener Source: " + source.toString());
        System.out.println("ISOResponseListener Message: " + m.toString());
        return false;
    }
}
