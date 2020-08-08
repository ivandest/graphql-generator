package ru.destinyman.generator;

import ru.destinyman.parsers.Entity;

import java.util.List;

public class GraphqlGenerator implements IGenerator{
    @Override
    public String generate(List<Entity> data, String fileName) {

        String fileNameWithoutExtension = fileName.substring(0, fileName.indexOf('.'));

        StringBuilder outputData = new StringBuilder("schema {\nquery: Query\nmutation: Mutation\n}\n");

        outputData.append(generateQuery(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateMutation(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateEntityType(data, fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateListRequest(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateOrderInput(fileNameWithoutExtension));
        outputData.append("\n");
        String[] orderDirection = {"ASC", "DESC"};
        outputData.append(CommonUtils.generateEnum("order_direction", orderDirection));

        return outputData.toString();
    }

    public String generateQuery(String entityName){
        StringBuilder outputData = new StringBuilder();
        String queryString = CommonUtils.makeTitleCase(entityName, false) + "List";

        outputData.append("type Query {\n")
            .append("get")
            .append(queryString)
            .append("(args: Get")
            .append(queryString)
            .append("Request): Get")
            .append(queryString)
            .append("Response");
        outputData.append("\n}");

        return outputData.toString();
    }

    public String generateEntityType(List<Entity> data, String entityName){
        StringBuilder entityType = new StringBuilder("type ");
        entityType.append(CommonUtils.makeTitleCase(entityName, false) + " {\n");
        for (Entity record : data){
            entityType.append("\"" + record.getCaption() + "\"\n");
            entityType.append(CommonUtils.makeTitleCase(record.getCode(), true) + ": ");
            entityType.append(convertDataType(record.getDataType(), record.getCode(), record.getReference()) + "\n");
        }
        entityType.append("\n}");
        return entityType.toString();
    }

    public String generateMutation(String entityName){
        StringBuilder outputData = new StringBuilder();
        String queryString = CommonUtils.makeTitleCase(entityName, false);

        outputData.append("type Mutation {\n")
                .append("save")
                .append(queryString)
                .append("(input: Save")
                .append(queryString)
                .append("Input): ")
                .append(queryString + "!\n")
                .append("removeMany")
                .append(queryString + "s")
                .append("(ids: [ID!]!): Boolean!")
        ;
        outputData.append("\n}");

        return outputData.toString();
    }

    public String generateListRequest(String entityName){
        StringBuilder outputData = new StringBuilder("input ");

        outputData.append(CommonUtils.makeTitleCase(entityName, false))
                .append("ListInput {\n")
                .append("skip: Int\ntake: Int\n")
                .append("orderBy: [" + CommonUtils.makeTitleCase(entityName, false) + "ListOrder]\n")
                .append("filter: [" + CommonUtils.makeTitleCase(entityName, false) + "ListFilterInput]\n")
                .append("ids: [ID!]\n")
                .append("}");

        return outputData.toString();
    }

    public String generateOrderInput(String entityName){
        StringBuilder outputData = new StringBuilder("input ");
        String inputName = CommonUtils.makeTitleCase(entityName, false) + "ListOrder";
        outputData.append(inputName + " {\n")
                .append("field: E" + inputName + "\n")
                .append("direction: EOrderDirection\nordering: String\n")
                .append("}");

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
                return reference;
            }
            case "varchar": return "String";
            case "timestamp":
            case "timestamptz":
                return "DateTime";
            case "enum": return CommonUtils.makeEnumName(code);
        }

        return converted;
    }


}
