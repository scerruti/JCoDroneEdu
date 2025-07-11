package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.protocol.linkmanager.Error;
import com.otabi.jcodroneedu.protocol.linkmanager.*;

/**
 * Manages and stores the state related to the communication link and device identity.
 * This class acts as a cache for non-real-time, static, or semi-static data
 * such as device information, addresses, and connection status. It is populated
 * by the Receiver as messages are parsed from the drone.
 */
public class LinkManager {

    private Address address;
    private Error error;
    private Information information;
    private Message lastMessage;
    private Pairing pairing;
    private Ping lastPing;
    private Registration registration;
    private Rssi rssi;
    private SystemInformation systemInformation;

    // --- Getters and Setters ---

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public Information getInformation() {
        return information;
    }

    public void setInformation(Information information) {
        this.information = information;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Pairing getPairing() {
        return pairing;
    }

    public void setPairing(Pairing pairing) {
        this.pairing = pairing;
    }

    public Ping getLastPing() {
        return lastPing;
    }

    public void setLastPing(Ping lastPing) {
        this.lastPing = lastPing;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public Rssi getRssi() {
        return rssi;
    }

    public void setRssi(Rssi rssi) {
        this.rssi = rssi;
    }

    public SystemInformation getSystemInformation() {
        return systemInformation;
    }

    public void setSystemInformation(SystemInformation systemInformation) {
        this.systemInformation = systemInformation;
    }
}
