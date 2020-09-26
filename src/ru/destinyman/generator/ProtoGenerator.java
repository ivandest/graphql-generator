package ru.destinyman.generator;

import ru.destinyman.parsers.Entity;
import ru.destinyman.utils.MarkdownFileUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class ProtoGenerator implements IGenerator {
    @Override
    public String generate(List<Entity> data, String fileName) {
        String fileNameWithoutExtension = fileName.substring(0, fileName.indexOf('.'));
        StringBuilder outputData = new StringBuilder("syntax = \"proto3\";\n" +
                "\n" +
                "package org.service;");
        outputData.append("\n");
        outputData.append(generateService(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateListRequest(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateListResponse(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateOrderInput(fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateEntityType(data, fileNameWithoutExtension));
        outputData.append("\n");
        outputData.append(generateFilterInput(data, fileNameWithoutExtension));
        outputData.append("\n");
        String[] orderDirection = {"ASC", "DESC"};
        outputData.append(CommonUtils.generateProtoEnum("order_direction", orderDirection));
        outputData.append("\n");
        outputData.append(CommonUtils.generateProtoEnum(fileNameWithoutExtension + "ListOrderFields", CommonUtils.getFieldCodes(data)));
        outputData.append("\n");
        outputData.append(CommonUtils.generateProtoEnumFromComment(data));
        return outputData.toString();
    }

    @Override
    public String generateEntityType(List<Entity> data, String entityName) {
        StringBuilder entityType = new StringBuilder("message ");
        entityType.append(CommonUtils.makeTitleCase(entityName, false)).append(" {\n");
        int i = 1;
        for (Entity record : data){
            entityType.append(convertDataType(record.getDataType(), record.getCode(), record.getReference())).append(" ");
            entityType.append(CommonUtils.makeTitleCase(record.getCode(), true)).append(" = ").append(i).append(";\n");
            i++;
        }
        entityType.append("}");
        return entityType.toString();
    }

    public String generateService(String entityName) {
        return "service " + CommonUtils.makeTitleCase(entityName, false) + "Service {\n" + "rpc Get" + CommonUtils.makeTitleCase(entityName, false) + "List (" +
                CommonUtils.makeTitleCase(entityName, false) + "ListRequest) " +
                "returns (" + CommonUtils.makeTitleCase(entityName, false) + "ListResponse);\n" +
                "rpc Save" + CommonUtils.makeTitleCase(entityName, false) + " (Save" + CommonUtils.makeTitleCase(entityName, false) + "Request) " +
                "returns (Save" + CommonUtils.makeTitleCase(entityName, false) + "Response);\n" +
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

    public String generateFilterInput(List<Entity> data, String entityName){
        StringBuilder outputData = new StringBuilder("message ");
        String inputName = CommonUtils.makeTitleCase(entityName, false) + "ListFilterInput";
        outputData.append(inputName).append(" {\n");
        int i = 1;
        for ( Entity record : data){
            outputData.append(convertDataTypeForFilters(record.getDataType(), record.getCode(), record.getReference())).append(" ");
            outputData.append(CommonUtils.makeTitleCase(record.getCode(), true)).append(" = ").append(i).append(";\n");
            i++;
        }
        outputData.append("\n}");

        return outputData.toString();
    }

    public String generateOrderInput(String entityName){
        StringBuilder outputData = new StringBuilder("message ");
        String inputName = CommonUtils.makeTitleCase(entityName, false) + "ListOrder";
        outputData.append(inputName).append(" {\n").append("E").append(inputName).append("Fields field = 1;\n")
                .append("EOrderDirection direction = 2;\n")
                .append("}\n");

        return outputData.toString();
    }

    private String convertDataType(String dataType, String code, String reference){
        String converted = "";

        if (dataType.contains("("))
            dataType = dataType.substring(0, dataType.indexOf("("));

        switch (dataType.trim()){
            case "id": {
               // if (reference.equals(""))
                    return "string";
                //return makeLinkedEntity(reference);
            }
            case "varchar": return "string";
            case "timestamp":
            case "timestamptz": {
                return "uint64";
            }
            case "enum": return CommonUtils.makeEnumName(code);
        }

        return converted;
    }

    private String convertDataTypeForFilters(String dataType, String code, String reference){
        String converted = "";

        if (dataType.contains("("))
            dataType = dataType.substring(0, dataType.indexOf("("));

        switch (dataType.trim()){
            case "id": return "string";
            case "varchar": return "string";
            case "timestamp":
            case "timestamptz":
                return "uint64";
            case "enum": return CommonUtils.makeEnumName(code);
        }

        return converted;
    }

}
