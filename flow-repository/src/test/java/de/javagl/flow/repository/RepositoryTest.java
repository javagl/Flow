package de.javagl.flow.repository;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.function.Function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test cases for the {@link Repository} class.
 */
@RunWith(JUnit4.class)
public class RepositoryTest
{
    /**
     * Basic test for the {@link Repository} class
     */
    @Test
    public void testBasic()
    {
        String value0 = "value0";
        String value1 = "value1";
        String value2 = "value2";
        String key0 = "keyOf(" + value0 + ")";
        String key1 = "keyOf(" + value1 + ")";
        String key2 = "keyOf(" + value2 + ")";
        
        Function<String, String> keyFunction = 
            new Function<String, String>()
        {
            @Override
            public String apply(String value)
            {
                return "keyOf("+value+")";
            }
        };
        
        Repository<String, String> repository = 
            Repositories.create(keyFunction);
        repository.add(Arrays.asList(value0, value1));
        
        assertEquals(value0, repository.get(key0)); 
        assertEquals(value1, repository.get(key1)); 
        assertEquals(null  , repository.get(key2)); 
        
        repository.add(Arrays.asList(value1, value2));

        assertEquals(value0, repository.get(key0)); 
        assertEquals(value1, repository.get(key1)); 
        assertEquals(value2, repository.get(key2)); 
        
        repository.remove(Arrays.asList(value0, value1));
        
        assertEquals(null  , repository.get(key0)); 
        assertEquals(value1, repository.get(key1)); 
        assertEquals(value2, repository.get(key2)); 
    }

}
