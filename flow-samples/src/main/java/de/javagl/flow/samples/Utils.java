package de.javagl.flow.samples;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Utility methods for the samples
 */
public class Utils
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(Utils.class.getName());
    
    /**
     * Perform some default initialization of the samples
     */
    public static void defaultInitialization()
    {
        Locale.setDefault(Locale.ENGLISH);
        initLogging();
        //trySetSystemLookAndFeel();
    }
    
    /**
     * Try to set the "System Look And Feel". If this fails, a warning 
     * will be printed. 
     */
    private static void trySetSystemLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e)
        {
            logger.warning(e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            logger.warning(e.getMessage());
        }
        catch (InstantiationException e)
        {
            logger.warning(e.getMessage());
        }
        catch (IllegalAccessException e)
        {
            logger.warning(e.getMessage());
        }
    }
    
    /**
     * Utility method to initialize the logging
     */
    private static void initLogging()
    {
        configureLoggerDefault("de.javagl.flow");
    }
    
    /**
     * Perform a default initialization for the logger with the given name
     * 
     * @param name The name
     */
    private static void configureLoggerDefault(String name)
    {
        Logger logger = Logger.getLogger(name);
        LoggerUtil.configureDefault(logger);
        logger.setLevel(Level.INFO);
    }

    /**
     * Private constructor to prevent instantiation. I wouldn't instantiate
     * this anyhow, but ... here it is.
     */
    private Utils()
    {
        // Private constructor to prevent instantiation
    }

}
