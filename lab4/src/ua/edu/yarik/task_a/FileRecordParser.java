package ua.edu.yarik.task_a;

import java.util.Locale;

public class FileRecordParser {
    public static String getName(String inputLine, int currentLineIndex){
        String[] lineComponents = parseInputLine(inputLine, currentLineIndex);
        return lineComponents[0];
    }

    public static PhoneNumber getPhoneNumber(String inputLine, int currentLineIndex){
        String[] lineComponents = parseInputLine(inputLine, currentLineIndex);
        return new PhoneNumber(lineComponents[1]);
    }

    public static String[] parseInputLine(String inputLine, int currentLineIndex){
        String[] lineComponents = inputLine.split(":");
        if (lineComponents.length != 2){
            throw new IllegalArgumentException(
                    "Illegal format of a line index " + currentLineIndex);
        }

        String name = lineComponents[0].trim();
        String phone = lineComponents[1].trim();
        return new String[]{name, phone};
    }


}
