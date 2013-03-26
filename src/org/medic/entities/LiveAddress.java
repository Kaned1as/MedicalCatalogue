package org.medic.entities;

public class LiveAddress
{
    private String street;
    private String apartment;

    @Override
    public String toString()
    {
        return street + " кв." + apartment;
    }
}
