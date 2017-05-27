/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.basic.selectable.f;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A Java Bean only containing an Float value
 */
public final class FloatBean
{
    /**
     * The value
     */
    private Float value = 0.0f;
    
    /**
     * The property change support
     */
    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * Default constructor
     */
    public FloatBean()
    {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
    /**
     * Set the value
     * 
     * @param value The value
     */
    public void setValue(Float value)
    {
        propertyChangeSupport.firePropertyChange(
            "value", this.value, this.value = value);        
    }

    /**
     * Return the value
     * 
     * @return The value
     */
    public Float getValue()
    {
        return value;
    }
    
    /**
     * Add the given listener to be informed about property changes
     *
     * @param propertyChangeListener The listener to add
     */
    public final void addPropertyChangeListener(
        PropertyChangeListener propertyChangeListener)
    {
        propertyChangeSupport.addPropertyChangeListener(
            propertyChangeListener);
    }

    /**
     * Remove the given listener
     *
     * @param propertyChangeListener The listener to remove
     */
    public final void removePropertyChangeListener(
        PropertyChangeListener propertyChangeListener)
    {
        propertyChangeSupport.removePropertyChangeListener(
            propertyChangeListener);
    }
    
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[value=" + value + "]";
    }
    
}