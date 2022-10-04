package ua.edu.yarik.task_b;

import java.util.*;

public class Manager implements Runnable{
    private boolean isDone = false;
    private String[] strings;
    private int threadsCount;
    Random rand = new Random();

    public Manager(int n){
        threadsCount = n;

        strings = new String[n];
        final int STRING_LENGTH = 10;
        for (int i = 0; i < strings.length; i++){
            strings[i] = genRandomString(STRING_LENGTH);
        }
    }

    private String genRandomString(int length){
        String result = "";
        final char[] possibleSymbols = new char[]{'A', 'B', 'C', 'D'};

        for (int i = 0; i < length; i++){
            result += possibleSymbols[rand.nextInt(possibleSymbols.length)];
        }

        return result;
    }

    public String getString(int index){
        return strings[index];
    }

    // method is not sync, because threads will address to different sells of array
    public void setString(int threadId, String str){
        if (strings[threadId].length() != str.length()){
            throw new IllegalArgumentException("Strings must have same lengths");
        }
        strings[threadId] = str;
    }

    public boolean isDone(){
        return isDone;
    }

    // action when barrier is opened
    @Override
    public void run() {
        System.out.println("Barrier is opened");

        // store number of 'A' and 'B' in strings[i]
        Integer[] specifiedCharsCount = new Integer[threadsCount];

        for (int i = 0; i < threadsCount; i++){
            specifiedCharsCount[i] =
                    countEntries(strings[i], 'A') + countEntries(strings[i], 'B');
        }

        List<Integer> asList = Arrays.asList(specifiedCharsCount);
        Set<Integer> setOfCounts = new HashSet<>(asList);

        for(Integer n : setOfCounts){
            if (Collections.frequency(asList, n.intValue()) >= 3) {
                isDone = true;

                System.out.println("\nTermination condition is TRUE");
                System.out.println("Resulting strings: " + Arrays.toString(strings));
                System.out.println("AB-count: " + Arrays.toString(specifiedCharsCount) + "\n");
                break;
            }
        }
    }


    private int countEntries(String string, char ch){
        int count = 0;
        for (int i = 0; i < string.length(); i++){
            if (string.charAt(i) == ch){
                count++;
            }
        }
        return count;
    }
}
