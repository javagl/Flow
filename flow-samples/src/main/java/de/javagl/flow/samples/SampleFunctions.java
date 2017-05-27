package de.javagl.flow.samples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Some functions (methods) that are used for the samples
 */
@SuppressWarnings("javadoc")
public class SampleFunctions
{
    public static List<String> createStrings()
    {
        System.out.println("Calling createStrings");
        return Arrays.asList("abc", "def", "ghi", "jkl", "mno");
    }
    
    public static List<String> reverseStrings(List<String> strings)
    {
        System.out.println("Calling reverseStrings with input " + strings);
        List<String> reversedStrings = new ArrayList<String>();
        for (String string : strings)
        {
            StringBuilder sb = new StringBuilder(string);
            String reversedString = sb.reverse().toString();
            reversedStrings.add(reversedString);
        }
        return reversedStrings;
    }
    
    public static void printStrings(List<String> strings)
    {
        System.out.println("Calling printStrings with input " + strings);
        System.out.println(strings);
    }
    
    public static <T> List<List<T>> splitList(List<T> list)
    {
        List<T> list0 = list.subList(0, list.size() / 2);
        List<T> list1 = list.subList(list.size() / 2, list.size());
        return Arrays.asList(list0, list1);
    }

    public static <T> List<T> mergeLists(List<T> list0, List<T> list1)
    {
        List<T> list = new ArrayList<T>();
        list.addAll(list0);
        list.addAll(list1);
        return list;
    }

    public static List<Integer> createRandomIntegers()
    {
        System.out.println("Calling createRandomIntegers");
        Random random = new Random();
        int count = 5 + random.nextInt(5);
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < count; i++)
        {
            list.add(random.nextInt(10));
        }
        return list;
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private SampleFunctions()
    {
        // Private constructor to prevent instantiation
    }
    
}
