/*
 * www.javagl.de - Flow
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
 */ 
package de.javagl.flow.modules.weka;

import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import weka.core.OptionHandler;
import weka.core.Utils;

/**
 * Utility methods for handling Weka options
 */
class WekaOptions
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(WekaOptions.class.getName());
    
    /**
     * Try to set the given options for the given Weka object.<br>
     * <br>
     * If the options are <code>null</code> or empty, then nothing is
     * done and <code>true</code> is returned.<br>
     * <br>
     * If the object is <code>null</code> or does not implement the
     * <code>OptionHandler</code> interface, then a warning is 
     * printed and <code>false</code> is returned.<br>
     * <br>
     * If the options cannot be parsed, then a warning is printed
     * and <code>false</code> is returned.<br>
     * <br>
     * If setting the options causes an exception, then a warning is printed
     * and <code>false</code> is returned.<br>
     * <br>
     * In all other cases (i.e. when the options could probably set
     * successfully), <code>true</code> is returned.
     * 
     * @param wekaObject The Weka object
     * @param options The options
     * @return Whether the options could be set
     */
    static boolean trySetOptions(Object wekaObject, String options)
    {
        if (options == null || options.isEmpty())
        {
            return true;
        }
        if (wekaObject == null || !(wekaObject instanceof OptionHandler))
        {
            logger.warning("The weka object " + wekaObject 
                + " does not have options");
            return false;
        }
        
        String[] optionsArray = {};
        try
        {
            optionsArray = Utils.splitOptions(options);
        } 
        catch (Exception e)
        {
            logger.warning("Cannot process the given options " 
                + options + ", " + e.getMessage());
            return false;
        }

        try
        {
            OptionHandler optionHandler = (OptionHandler)wekaObject;
            optionHandler.setOptions(optionsArray);
        }
        catch (Exception e)
        {
            logger.warning("The weka object " + wekaObject 
                + " cannot handle the given options " + options 
                + ", " + e.getMessage());
            return false;
        }
        return true;
    }
    
    
    /**
     * Try to get the options from the given Weka object.<br>
     * <br>
     * If the object is <code>null</code>, then <code>null</code> is 
     * returned.<br>
     * <br>
     * If the does not implement the <code>OptionHandler</code> interface, 
     * then a warning is printed and <code>null</code> is returned.<br>
     * <br>
     * Otherwise, the (joined) options string of the Weka object is returned.
     * 
     * @param wekaObject The Weka object
     * @return The options
     */
    static String tryGetOptions(Object wekaObject)
    {
        if (wekaObject == null)
        {
            return null;
        }
        if (!(wekaObject instanceof OptionHandler))
        {
            logger.warning("The weka object " + wekaObject 
                + " does not have options");
            return null;
        }
        OptionHandler optionHandler = (OptionHandler)wekaObject;
        String[] options = optionHandler.getOptions();
        return Utils.joinOptions(options);
    }
    
    /**
     * Try to format the given Weka options string, by splitting the
     * string and inserting a line break after each token. If the
     * options cannot be parsed, a warning will be printed, and
     * the given string will be returned.
     * 
     * @param options The options
     * @return The formatted options
     */
    static String tryFormatOptions(String options)
    {
        if (options == null)
        {
            return null;
        }
        try
        {
            String[] optionsArray = 
                Utils.splitOptions(options);
            String formattedOptions = 
                Stream.of(optionsArray).collect(Collectors.joining("\n"));
            return formattedOptions;
        } 
        catch (Exception e)
        {
            logger.warning("Could not parse options: " + options);
            return options;
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private WekaOptions()
    {
        // Private constructor to prevent instantiation
    }
}
