package io.dhoom.manager;

public class Manager
{
    protected ManagerHandler handler;
    
    public Manager(final ManagerHandler handler) {
        this.handler = handler;
    }
}
