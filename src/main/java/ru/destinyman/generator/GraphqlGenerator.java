package ru.destinyman.generator;

import ru.destinyman.parsers.Entity;
import ru.destinyman.utils.file.MarkdownFileUtils;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class GraphqlGenerator implements IGenerator{
    @Override
    public String generate(Map<String, List<Entity>> data, String fileName) {

        String[] orderDirection = {"ASC", "DESC"};

        String result = "";
        for (String key : data.keySet()) {
            List<Entity> items = data.get(key);
            result = generateQuery(key) +
                    "\n" +
                    generateMutation(key) +
                    "\n" +
                    generateEntityType(data) +
                    "\n" +
                    generateListRequest(key) +
                    "\n" +
                    generateResponse(key) +
                    "\n" +
                    generateOrderInput(key) +
                    "\n" +
                    CommonUtils.generateEnum("order_direction", orderDirection) +
                    "\n" +
                    CommonUtils.generateEnum(key + "ListOrderFields", CommonUtils.getFieldCodes(items)) +
                    "\n" +
                    generateFilterInput(data) +
                    "\n" +
                    CommonUtils.generateEnumFromComment(items, key) +
                    "\n" +
                    generateSaveInput(data);
        }
        return result;
    }

    public String generateQuery(String entityName){
        StringBuilder outputData = new StringBuilder();
        String queryString = CommonUtils.makeTitleCase(entityName, false) + "List";

        outputData.append("type Query {\n")
            .append("get")
            .append(queryString)
            .append("(args: ")
            .append(queryString)
            .append("Request): ")
            .append(queryString)
            .append("Response");
        outputData.append("\n}");

        return outputData.toString();
    }

    @Override
    public String generateEntityType(Map<String, List<Entity>> data){
        StringBuilder result = new StringBuilder();
        for (String key : data.keySet()) {
            StringBuilder entityType = new StringBuilder("type ");
            entityType.append(CommonUtils.makeTitleCase(key, false)).append(" {\n");
            generateFieldsWithTypesForEntity(data.get(key), entityType);
            entityType.append("}\n");
            result.append(entityType);
        }

        return result.toString();
    }

    @Override
    public String generateOnlyQueries(Map<String, List<Entity>> data) {
        StringBuilder result = new StringBuilder();
        for (String key : data.keySet()) {
            result.append(generateQuery(key))
                    .append("\n")
                    .append(generateMutation(key))
                    .append("\n")
                    .append(generateListRequest(key))
                    .append("\n")
                    .append(generateResponse(key))
                    .append("\n")
                    .append(generateSaveInput(data));
        }
        return result.toString();
    }

    private void generateFieldsWithTypesForEntity(List<Entity> data, StringBuilder entityType) {
        for (Entity record : data){
            entityType.append("\"").append(record.getCaption()).append("\"\n");
            entityType.append(CommonUtils.makeTitleCase(record.getCode(), true)).append(": ");
            entityType.append(convertDataType(record.getDataType(), record.getCode(), record.getReference()));
            if (record.getIsNullable() == null || record.getIsNullable().equals("NO")) {
                entityType.append("!");
            }
            entityType.append("\n");
        }
    }

    public String generateMutation(String entityName){
        StringBuilder outputData = new StringBuilder();
        String queryString = CommonUtils.makeTitleCase(entityName, false);

        outputData.append("type Mutation {\n")
                .append("save")
                .append(queryString)
                .append("(input: Save")
                .append(queryString)
                .append("Input): ").append(queryString).append("!\n")
                .append("removeMany").append(queryString).append("s")
                .append("(ids: [ID!]!): Boolean!")
        ;
        outputData.append("\n}");

        return outputData.toString();
    }

    public String generateListRequest(String entityName){

        return "input " + CommonUtils.makeTitleCase(entityName, false) +
                "ListRequest {\n" +
                "skip: Int\ntake: Int\n" + "orderBy: [" + CommonUtils.makeTitleCase(entityName, false) +
                "ListOrder]\n" + "filter: " + CommonUtils.makeTitleCase(entityName, false) +
                "ListFilterInput\n" +
                "ids: [ID!]\n" +
                "}";
    }

    public String generateOrderInput(String entityName){
        StringBuilder outputData = new StringBuilder("input ");
        String inputName = CommonUtils.makeTitleCase(entityName, false) + "ListOrder";
        outputData.append(inputName).append(" {\n").append("field: E").append(inputName).append("Fields\n")
                .append("direction: EOrderDirection\nordering: String\n")
                .append("}");

        return outputData.toString();
    }

    public String generateFilterInput(Map<String, List<Entity>> data){
        StringBuilder result = new StringBuilder();
        for (String key : data.keySet()) {
            String inputName = CommonUtils.makeTitleCase(key, false) + "ListFilterInput";
            StringBuilder outputData = new StringBuilder("input ");
            outputData.append(inputName).append(" {\n");
            for (Entity record : data.get(key)){
                outputData.append("\"").append(record.getCaption()).append("\"\n");
                outputData.append(CommonUtils.makeTitleCase(record.getCode(), true)).append(": ");
                outputData.append(convertDataTypeForFilters(record.getDataType(), record.getCode())).append("\n");
            }
            outputData.append("\n}");
            result.append(outputData);
        }

        return result.toString();
    }

    private String convertDataType(String dataType, String code, String reference){
        String converted = "";

        if (dataType.contains("("))
            dataType = dataType.substring(0, dataType.indexOf("("));

        switch (dataType.trim()){
            case "id":
            case "uuid": {
                if (reference == null || reference.equals(""))
                    return "ID";
                return makeLinkedEntity(reference);
            }
            case "varchar":
            case "jsonb":
                return "String";
            case "timestamp":
            case "timestamptz": {
                MarkdownFileUtils mfu = new MarkdownFileUtils();
                mfu.write("scalar DateTime", Paths.get("global.graphql"));
                return "DateTime";
            }
            case "enum": return CommonUtils.makeEnumName(code);
        }

        return converted;
    }

    private String convertDataTypeForFilters(String dataType, String code){
        String converted = "";

        if (dataType.contains("("))
            dataType = dataType.substring(0, dataType.indexOf("("));

        return switch (dataType.trim()) {
            case "id", "uuid" -> "ID";
            case "varchar", "jsonb" -> "String";
            case "timestamp", "timestamptz" -> "DateTime";
            case "enum" -> CommonUtils.makeEnumName(code);
            default -> converted;
        };

    }

    private String makeLinkedEntity(String entityName){
        StringBuilder outputData = new StringBuilder("ID\n");
        String entity = entityName.split("\\.")[0];
        outputData.append(entity).append(": ");
        outputData.append(CommonUtils.makeTitleCase(entity, false));
        return outputData.toString();
    }

    public String generateResponse(String entityName){
        return "type " + CommonUtils.makeTitleCase(entityName, false) + "ListResponse {\n" +
                "items: [" + CommonUtils.makeTitleCase(entityName, false) + "]!\n" +
                "totalCount: Int!\n}";
    }

    public String generateSaveInput(Map<String, List<Entity>> data){
        StringBuilder result = new StringBuilder();
        for (String key : data.keySet()){
            StringBuilder entityType = new StringBuilder("input Save");
            entityType.append(CommonUtils.makeTitleCase(key, false)).append("Input {\n");
            generateFieldsWithTypesForEntity(data.get(key), entityType);
            entityType.append("}");
            result.append(entityType);
        }

        return result.toString();
    }
}
