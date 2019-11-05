package io.dhoom.util;

public abstract class AbstractSerializer<T>
{
    public abstract String toString(final T p0);
    
    public abstract T fromString(final Object p0);
}
