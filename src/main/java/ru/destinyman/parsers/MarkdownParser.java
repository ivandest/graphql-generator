package ru.destinyman.parsers;

import java.util.ArrayList;
import java.util.Arrays;

public class MarkdownParser implements IParser {

    @Override
    public Entity parse(String textToParse) {
        String[] fields = textToParse.split("\\|");
        ArrayList<String> rawFields = new ArrayList<>(Arrays.asList(fields));

        if (rawFields.get(0).isEmpty()){
            rawFields.remove(0);
        }

        for (int i = 0; i < rawFields.size(); i++){
            rawFields.set(i, rawFields.get(i).trim());
        }

        String[] completeFields = rawFields.toArray(new String[6]);
        for (int i = 0; i < fields.length; i++) {
            if (fields[0].isEmpty()) {
                continue;
            }
            fields[i] = fields[i].trim();
            if (i == 3) {
                fields[i] = convertToBooleanString(fields[i]);
            }
            completeFields[i] = fields[i];
        }

        if (fields.length < completeFields.length){
            int diff = completeFields.length - fields.length;
            for (int i = 0; i < diff; i++){
                completeFields[fields.length + i] = "";
            }
        }

        return new Entity(completeFields[0], completeFields[1], completeFields[2], completeFields[3], completeFields[4], completeFields[5]);
    }

    private String convertToBooleanString(String field) {
        if (field.contains("plus")) return "NO";
        return "YES";
    }
}
