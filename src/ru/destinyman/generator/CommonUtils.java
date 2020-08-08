package ru.destinyman.generator;

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
            outputData.append(field.toUpperCase());
        }

        outputData.append("}");
        return outputData.toString();
    }

    public static String makeEnumName(String fieldName){
        return "E" + makeTitleCase(fieldName, false);
    }

}
