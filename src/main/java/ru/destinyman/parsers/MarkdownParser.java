package ru.destinyman.parsers;

public class MarkdownParser implements IParser {

    @Override
    public Entity parse(String textToParse) {
        String[] fields = textToParse.split("\\|");
        String[] completeFields = new String[6];
        for (int i = 0; i < fields.length; i++) {
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
