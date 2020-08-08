package ru.destinyman.parsers;

public class MarkdownParser implements IParser {

    @Override
    public Entity parse(String textToParse) {
        String[] fields = textToParse.split("\\|");
        String[] completeFields = new String[6];
        Entity entity = new Entity();
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
            completeFields[i] = fields[i];
        }

        if (fields.length < completeFields.length){
            int diff = completeFields.length - fields.length;
            for (int i = 0; i < diff; i++){
                completeFields[fields.length + i] = "";
            }
        }

        return new Entity(completeFields[0], completeFields[1], completeFields[2], completeFields[3], completeFields[4], completeFields[5]);

    /*    List<String> fieldsList = new ArrayList<>(Arrays.asList(fields));
        if (headers.length > fields.length){
            int diff = headers.length - fields.length;
            for (int i = 0; i < diff; i++){
                fieldsList.add("");
            }
        }
        String[] completeFields = (String[]) fieldsList.toArray();

       for (int i = 0; i < headers.length; i++){
           result.put(headers[i], completeFields[i]);
       }

       return result;*/
    }
}
