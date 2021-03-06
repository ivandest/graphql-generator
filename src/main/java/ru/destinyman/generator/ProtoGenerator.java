package ru.destinyman.generator;

import ru.destinyman.parsers.Entity;
import java.util.List;
import java.util.Map;

public class ProtoGenerator implements IGenerator {
    @Override
    public String generate(Map<String, List<Entity>> data, String fileName) {
        String[] orderDirection = {"ASC", "DESC"};

        String output = """
                service %sService {
                %s
                }
                
                // Entities
                %s
                
                // Inputs
                %s
                
                // Enums
                %s
                """;

        StringBuilder service = new StringBuilder();
        StringBuilder enums = new StringBuilder();
        StringBuilder types = new StringBuilder();
        StringBuilder inputs = new StringBuilder();
        for (String key : data.keySet()) {
            service.append(generateService(key));
            List<Entity> items = data.get(key);
            inputs.append(generateListRequest(key))
                    .append("\n")
                    .append(generateListResponse(key))
                    .append("\n")
                    .append(generateOrderInput(key))
                    .append("\n")
                    .append(generateFilterInput(items, key))
                    .append("\n")
                    .append(generateSaveResponse(key))
                    .append("\n")
                    .append(generateRemoveRequest(key))
                    .append("\n")
                    .append(generateRemoveResponse(key))
                    .append("\n");

            enums.append(CommonUtils.generateProtoEnum(key + "ListOrderFields", CommonUtils.getFieldCodes(items)))
                    .append("\n")
                    .append(CommonUtils.generateProtoEnumFromComment(items, key))
                    .append("\n");
        }

        enums.append(CommonUtils.generateProtoEnum("order_direction", orderDirection))
                .append("\n");
        types.append(generateEntityType(data)).append("\n");
        inputs.append("\n").append(generateSaveRequest(data));

        return String.format(output, CommonUtils.makeTitleCase(fileName, false), service, types, inputs, enums);
    }

    @Override
    public String generateEntityType(Map<String, List<Entity>> data) {
        StringBuilder result = new StringBuilder();
        for (String key : data.keySet()) {
            StringBuilder entityType = new StringBuilder("message ");
            entityType.append(CommonUtils.makeTitleCase(key, false)).append(" {\n");
            generateFieldsWithTypesForEntity(key, data.get(key), entityType);
            result.append(entityType).append("\n");
        }

        return result.toString();
    }

    @Override
    public String generateOnlyQueries(Map<String, List<Entity>> data) {
        StringBuilder result = new StringBuilder();
        for (String key : data.keySet()) {
            result.append(generateService(key)).append("\n")
                    .append(generateListRequest(key)).append("\n")
                    .append(generateListRequest(key)).append("\n")
                    .append(generateSaveRequest(data)).append("\n")
                    .append(generateSaveResponse(key)).append("\n")
                    .append(generateRemoveRequest(key)).append("\n")
                    .append(generateRemoveRequest(key));
        }
        return result.toString();
    }

    private void generateFieldsWithTypesForEntity(String entityName, List<Entity> data, StringBuilder entityType){
        int i = 1;
        for (Entity record : data){
            entityType.append(convertDataType(record.getDataType(), record.getCode(), entityName)).append(" ");
            entityType.append(CommonUtils.makeSnakeCase(record.getCode(), true)).append(" = ").append(i).append(";\n");
            i++;
        }
        entityType.append("}");

    }

    public String generateService(String entityName) {
        return "rpc Get" + CommonUtils.makeTitleCase(entityName, false) + "List (" +
                CommonUtils.makeTitleCase(entityName, false) + "ListRequest) " +
                "returns (" + CommonUtils.makeTitleCase(entityName, false) + "ListResponse);\n" +
                "rpc Save" + CommonUtils.makeTitleCase(entityName, false) + " (Save" + CommonUtils.makeTitleCase(entityName, false) + "Request) " +
                "returns (Save" + CommonUtils.makeTitleCase(entityName, false) + "Response);\n" +
                "rpc RemoveMany" + CommonUtils.makeTitleCase(entityName, false) + "s (RemoveMany" + CommonUtils.makeTitleCase(entityName, false) + "Request) " +
                "returns (RemoveMany" + CommonUtils.makeTitleCase(entityName, false) + "Response);\n";
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

    public String generateSaveRequest(Map<String, List<Entity>> data){
        StringBuilder result = new StringBuilder();
        for (String key : data.keySet()){
            StringBuilder entityType = new StringBuilder("message Save");
            entityType.append(CommonUtils.makeTitleCase(key, false)).append("Request {\n");
            generateFieldsWithTypesForEntity(key, data.get(key), entityType);
            result.append(entityType);
        }

        return result.toString();
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
        generateFieldsWithTypesForEntity(entityName, data, outputData);

        return outputData.toString();
    }

    public String generateOrderInput(String entityName){
        StringBuilder outputData = new StringBuilder("message ");
        String inputName = CommonUtils.makeTitleCase(entityName, false) + "ListOrder";
        outputData.append(inputName).append(" {\n").append("E").append(inputName).append("Fields field = 1;\n")
                .append("EOrderDirection direction = 2;\n}\n");

        return outputData.toString();
    }

    private String convertDataType(String dataType, String code, String entityName){
        String converted = "";

        if (dataType.contains("("))
            dataType = dataType.substring(0, dataType.indexOf("("));

        switch (dataType.trim()){
            case "id":
            case "varchar":
            case "uuid":
            case "jsonb":
                return "string";
            case "timestamp":
            case "timestamptz": {
                return "uint64";
            }
            case "enum": return CommonUtils.makeEnumName(entityName + "_" + code);
        }

        return converted;
    }

}
