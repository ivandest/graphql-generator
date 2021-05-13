package ru.destinyman.generator;

import ru.destinyman.parsers.Entity;
import ru.destinyman.utils.file.MarkdownFileUtils;

import java.nio.file.Paths;
import java.util.List;

public class GraphqlGenerator implements IGenerator{
    @Override
    public String generate(List<Entity> data, String fileName) {

        String fileNameWithoutExtension = fileName.indexOf('.') != -1 ? fileName.substring(0, fileName.indexOf('.')) : fileName;

        StringBuilder outputData = new StringBuilder("schema {\nquery: Query\nmutation: Mutation\n}\n");

        outputData.append(generateQuery(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateMutation(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateEntityType(data, fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateListRequest(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateResponse(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateOrderInput(fileNameWithoutExtension));
        outputData.append("\n");
        String[] orderDirection = {"ASC", "DESC"};
        outputData.append(CommonUtils.generateEnum("order_direction", orderDirection));
        outputData.append("\n");
        outputData.append(CommonUtils.generateEnum(fileNameWithoutExtension + "ListOrderFields", CommonUtils.getFieldCodes(data)));
        outputData.append("\n");
        outputData.append(generateFilterInput(data, fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(CommonUtils.generateEnumFromComment(data));
        outputData.append("\n");
        outputData.append(generateSaveInput(data, fileNameWithoutExtension));

        return outputData.toString();
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

    public String generateEntityType(List<Entity> data, String entityName){
        StringBuilder entityType = new StringBuilder("type ");
        entityType.append(CommonUtils.makeTitleCase(entityName, false)).append(" {\n");
        generateFieldsWithTypesForEntity(data, entityType);
        entityType.append("}");
        return entityType.toString();
    }

    private void generateFieldsWithTypesForEntity(List<Entity> data, StringBuilder entityType) {
        for (Entity record : data){
            entityType.append("\"").append(record.getCaption()).append("\"\n");
            entityType.append(CommonUtils.makeTitleCase(record.getCode(), true)).append(": ");
            entityType.append(convertDataType(record.getDataType(), record.getCode(), record.getReference())).append("\n");
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

    public String generateFilterInput(List<Entity> data, String entityName){
        StringBuilder outputData = new StringBuilder("input ");
        String inputName = CommonUtils.makeTitleCase(entityName, false) + "ListFilterInput";
        outputData.append(inputName).append(" {\n");
        for (Entity record : data){
            outputData.append("\"").append(record.getCaption()).append("\"\n");
            outputData.append(CommonUtils.makeTitleCase(record.getCode(), true)).append(": ");
            outputData.append(convertDataTypeForFilters(record.getDataType(), record.getCode())).append("\n");
        }
        outputData.append("\n}");

        return outputData.toString();
    }

    private String convertDataType(String dataType, String code, String reference){
        String converted = "";

        if (dataType.contains("("))
            dataType = dataType.substring(0, dataType.indexOf("("));

        switch (dataType.trim()){
            case "id": {
                if (reference.equals(""))
                    return "ID";
                return makeLinkedEntity(reference);
            }
            case "varchar": return "String";
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

        switch (dataType.trim()){
            case "id": return "ID";
            case "varchar": return "String";
            case "timestamp":
            case "timestamptz":
                return "DateTime";
            case "enum": return CommonUtils.makeEnumName(code);
        }

        return converted;
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

    public String generateSaveInput(List<Entity> data, String entityName){
        StringBuilder entityType = new StringBuilder("input Save");
        entityType.append(CommonUtils.makeTitleCase(entityName, false)).append("Input {\n");
        generateFieldsWithTypesForEntity(data, entityType);
        entityType.append("}");
        return entityType.toString();
    }

}
