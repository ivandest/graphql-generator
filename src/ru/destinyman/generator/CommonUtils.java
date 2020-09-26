package ru.destinyman.generator;

import ru.destinyman.parsers.Entity;

import java.util.List;

public class CommonUtils {

    public static String makeTitleCase(String textToConvert, Boolean firstLower) {
        char[] parseString = textToConvert.toCharArray();
        StringBuilder outputData = new StringBuilder();
        boolean convertNext = true;
        if (firstLower) {
            parseString[0] = Character.toLowerCase(parseString[0]);
            convertNext = false;
        }
        for (char textItem : parseString){
            if (textItem == '_'){
                convertNext = true;
            } else if (convertNext){
                textItem = Character.toTitleCase(textItem);
                convertNext = false;
                outputData.append(textItem);
            } else if (Character.isUpperCase(textItem)) {
                outputData.append(textItem);
            }
            else{
                textItem = Character.toLowerCase(textItem);
                outputData.append(textItem);
            }
        }
        return outputData.toString();
    }


    public static String generateEnum(String enumName, String[] fields){
        StringBuilder outputData = new StringBuilder("enum E" + makeTitleCase(enumName, false) + " {\n");
        for (String field : fields){
            outputData.append(field.toUpperCase().trim()).append("\n");
        }

        outputData.append("}");
        return outputData.toString();
    }

    public static String generateProtoEnum(String enumName, String[] fields){
        StringBuilder outputData = new StringBuilder("enum E" + makeTitleCase(enumName, false) + " {\n");
        for (int i = 0; i < fields.length; i++){
            outputData.append(fields[i].toUpperCase().trim()).append(" = ").append(i).append(";\n");
        }

        outputData.append("}");
        return outputData.toString();
    }

    public static String makeEnumName(String fieldName){
        return "E" + makeTitleCase(fieldName, false);
    }

    public static String[] getFieldCodes(List<Entity> data){
        String[] codes = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            codes[i] = data.get(i).getCode().toUpperCase();
        }
        return codes;
    }

    public static String generateEnumFromComment(List<Entity> data) {
        StringBuilder enumText = new StringBuilder();

        for (Entity entity : data){
            if (!entity.getComment().equals("")){
                enumText.append(generateEnum(entity.getCode(), entity.getComment().split(",")));
            }
        }

        return enumText.toString();
    }

    public static String generateProtoEnumFromComment(List<Entity> data) {
        StringBuilder enumText = new StringBuilder();

        for (Entity entity : data){
            if (!entity.getComment().equals("")){
                enumText.append(generateProtoEnum(entity.getCode(), entity.getComment().split(",")));
            }
        }

        return enumText.toString();
    }
}
