package ru.destinyman.generator;

import ru.destinyman.parsers.Entity;
import java.util.List;

public class ProtoGenerator implements IGenerator {
    @Override
    public String generate(List<Entity> data, String fileName) {
        String fileNameWithoutExtension = fileName.substring(0, fileName.indexOf('.'));
        String[] orderDirection = {"ASC", "DESC"};
        return "syntax = \"proto3\";\n" +
                "\n" +
                "package org.service;" + "\n" +
                generateService(fileNameWithoutExtension) +
                "\n" +
                generateListRequest(fileNameWithoutExtension) +
                "\n" +
                generateListResponse(fileNameWithoutExtension) +
                "\n" +
                generateOrderInput(fileNameWithoutExtension) +
                "\n" +
                generateEntityType(data, fileNameWithoutExtension) +
                "\n" +
                generateFilterInput(data, fileNameWithoutExtension) +
                "\n" +
                CommonUtils.generateProtoEnum("order_direction", orderDirection) +
                "\n" +
                CommonUtils.generateProtoEnum(fileNameWithoutExtension + "ListOrderFields", CommonUtils.getFieldCodes(data)) +
                "\n" +
                CommonUtils.generateProtoEnumFromComment(data) +
                "\n" +
                generateSaveRequest(data, fileNameWithoutExtension) +
                "\n" +
                generateSaveResponse(fileNameWithoutExtension) +
                "\n" +
                generateRemoveRequest(fileNameWithoutExtension) +
                "\n" +
                generateRemoveResponse(fileNameWithoutExtension);
    }

    @Override
    public String generateEntityType(List<Entity> data, String entityName) {
        StringBuilder entityType = new StringBuilder("message ");
        entityType.append(CommonUtils.makeTitleCase(entityName, false)).append(" {\n");
        generateFieldsWithTypesForEntity(data, entityType);
        return entityType.toString();
    }

    private void generateFieldsWithTypesForEntity(List<Entity> data, StringBuilder entityType){
        int i = 1;
        for (Entity record : data){
            entityType.append(convertDataType(record.getDataType(), record.getCode())).append(" ");
            entityType.append(CommonUtils.makeTitleCase(record.getCode(), true)).append(" = ").append(i).append(";\n");
            i++;
        }
        entityType.append("}");

    }

    public String generateService(String entityName) {
        return "service " + CommonUtils.makeTitleCase(entityName, false) + "Service {\n" + "rpc Get" + CommonUtils.makeTitleCase(entityName, false) + "List (" +
                CommonUtils.makeTitleCase(entityName, false) + "ListRequest) " +
                "returns (" + CommonUtils.makeTitleCase(entityName, false) + "ListResponse);\n" +
                "rpc Save" + CommonUtils.makeTitleCase(entityName, false) + " (Save" + CommonUtils.makeTitleCase(entityName, false) + "Request) " +
                "returns (Save" + CommonUtils.makeTitleCase(entityName, false) + "Response);\n" +
                "rpc RemoveMany" + CommonUtils.makeTitleCase(entityName, false) + "s (RemoveMany" + CommonUtils.makeTitleCase(entityName, false) + "Request) " +
                "returns (RemoveMany" + CommonUtils.makeTitleCase(entityName, false) + "Response);\n" +
                "}";
    }

    public String generateListRequest(String entityName){

        return "message " + CommonUtils.makeTitleCase(entityName, false) +
                "ListRequest {\n" +
                "uint32 skip = 1;\nuint32 take = 2;\n" + "repeated " + CommonUtils.makeTitleCase(entityName, false) +
                "ListOrder orderBy = 3;\n" + CommonUtils.makeTitleCase(entityName, false) +
                "ListFilterInput filter = 4;\n" + "repeated string ids = 5;\n" +
                "}";
    }

    public String generateListResponse(String entityName){
        return "message " + CommonUtils.makeTitleCase(entityName, false) + "ListResponse {\n" +
                "repeated " + CommonUtils.makeTitleCase(entityName, false) + " items = 1;\n" +
                "uint32 total_count = 2;\n}";
    }

    public String generateSaveRequest(List<Entity> data, String entityName){
        StringBuilder entityType = new StringBuilder("message Save");
        entityType.append(CommonUtils.makeTitleCase(entityName, false)).append("Request {\n");
        generateFieldsWithTypesForEntity(data, entityType);
        return entityType.toString();
    }

    public String generateSaveResponse(String entityName){
        return "message Save" + CommonUtils.makeTitleCase(entityName, false) + "Response {\n" +
                CommonUtils.makeTitleCase(entityName, false) + " result = 1;\n" +
                "}";
    }

    public String generateRemoveRequest(String entityName){
        return "message RemoveMany" + CommonUtils.makeTitleCase(entityName, false) + "Request {\n" +
                "repeated string ids = 1;\n}\n";
    }

    public String generateRemoveResponse(String entityName){
        return "message RemoveMany" + CommonUtils.makeTitleCase(entityName, false) + "Response {\n" +
                "bool result = 1;\n" +
                "}";
    }

    public String generateFilterInput(List<Entity> data, String entityName){
        StringBuilder outputData = new StringBuilder("message ");
        String inputName = CommonUtils.makeTitleCase(entityName, false) + "ListFilterInput";
        outputData.append(inputName).append(" {\n");
        generateFieldsWithTypesForEntity(data, outputData);

        return outputData.toString();
    }

    public String generateOrderInput(String entityName){
        StringBuilder outputData = new StringBuilder("message ");
        String inputName = CommonUtils.makeTitleCase(entityName, false) + "ListOrder";
        outputData.append(inputName).append(" {\n").append("E").append(inputName).append("Fields field = 1;\n")
                .append("EOrderDirection direction = 2;\n}\n");

        return outputData.toString();
    }

    private String convertDataType(String dataType, String code){
        String converted = "";

        if (dataType.contains("("))
            dataType = dataType.substring(0, dataType.indexOf("("));

        switch (dataType.trim()){
            case "id":
            case "varchar":
                return "string";
            case "timestamp":
            case "timestamptz": {
                return "uint64";
            }
            case "enum": return CommonUtils.makeEnumName(code);
        }

        return converted;
    }

}
