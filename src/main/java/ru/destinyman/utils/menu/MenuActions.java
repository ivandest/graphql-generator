package ru.destinyman.utils.menu;

import ru.destinyman.generator.CommonUtils;
import ru.destinyman.generator.GraphqlGenerator;
import ru.destinyman.generator.ProtoGenerator;
import ru.destinyman.parsers.Entity;
import ru.destinyman.utils.ErrorText;
import ru.destinyman.utils.database.CommonDbUtils;
import ru.destinyman.utils.file.MarkdownFileUtils;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MenuActions {

    static public void printHelp(){
        System.out.println("""
                USAGE: graphql-generator.jar [options] file_path
                       OR
                       graphql-generator.jar [options] --from-database host:port:database:login:password [schema | schema table]
                OPTIONS:
                   -q, --queries-only - generate only gql-queries and mutations
                   -h, --help - get current usage info
                   -a, --all - generate graphql, proto files, gql-queries and mutations
                   -qn, --no-query - generate graphql, proto files WITHOUT gql-queries and mutations
                   -f, --filters - with filters on each entity attribute
                   -o, --order - with sort block
                   --filter-types - with filter type on each entity attribute
                   --order-types - with order type on each entity attribute"""
        );
        System.exit(0);
    }

    static public ArrayList<EMenuActions> getActionFromKeys(List<String> args){
        ArrayList<EMenuActions> output = new ArrayList<>();

        if (args.size() == 1 && !args.get(0).startsWith("-")){
            output.add(EMenuActions.ALL);
            return output;
        }
        if (args.contains("--from-database")) {
            output.add(EMenuActions.DATABASE);
            return output;
        }

        args.forEach(arg -> {
            switch (arg) {
                case "-h", "--help" -> output.add(EMenuActions.HELP);
                case "-q", "--queries-only" -> output.add(EMenuActions.QUERIES_ONLY);
                case "-qn", "--no-query" -> output.add(EMenuActions.NO_QUERY);
                case "-f", "--filters" -> output.add(EMenuActions.FILTERS);
                case "-o", "--order" -> output.add(EMenuActions.ORDER);
                case "--filter-types" -> output.add(EMenuActions.FILTER_TYPES);
                case "--order-types" -> output.add(EMenuActions.ORDER_TYPES);
                case "--from-database" -> output.add(EMenuActions.DATABASE);
                case "-a", "--all" -> output.add(EMenuActions.ALL);
            }
        });
        return output;
    }

    public static void executeActions(ArrayList<EMenuActions> menuActions, List<String> args) {
        checkPossibility(menuActions);

        MarkdownFileUtils markdownFileUtils = new MarkdownFileUtils();
        Map<String, List<Entity>> data;
        String fileName;
        if (menuActions.contains(EMenuActions.DATABASE)) {
            data = databaseActions(args).data();
            fileName = databaseActions(args).schema();
        } else {
            data = markdownFileUtils.read(Paths.get(args.get(args.size() - 1)));
            fileName = CommonUtils.getFileName(args);
        }

        String graphqlFile = CommonUtils.makeTitleCase(fileName + "Service.graphql", false);
        String protoFile = CommonUtils.makeTitleCase(fileName + "Service.proto", false);

        StringBuilder gqlOutput = new StringBuilder("schema {\nquery: Query\nmutation: Mutation\n}\n");
        StringBuilder protoOutput = new StringBuilder("syntax = \"proto3\";\n\npackage org.service;\n");

        GraphqlGenerator gg = new GraphqlGenerator();
        ProtoGenerator pg = new ProtoGenerator();

        if (menuActions.contains(EMenuActions.QUERIES_ONLY)){
            gqlOutput.append(gg.generateOnlyQueries(data));
            protoOutput.append(pg.generateOnlyQueries(data));
        }
        if (menuActions.contains(EMenuActions.ALL)){
            gqlOutput.append(gg.generate(data, fileName));
            protoOutput.append(pg.generate(data, fileName));
        }
        if (menuActions.contains(EMenuActions.HELP)){
            printHelp();
        }
        if (menuActions.size() == 1 && menuActions.contains(EMenuActions.DATABASE)){
            gqlOutput.append(gg.generate(data, fileName));
            protoOutput.append(pg.generate(data, fileName));
        }
        if (menuActions.contains(EMenuActions.NO_QUERY)) {
            gqlOutput.append(gg.generateEntityType(data));
            protoOutput.append(pg.generateEntityType(data));
        }

        markdownFileUtils.write(gqlOutput.toString(), Paths.get(graphqlFile));
        markdownFileUtils.write(protoOutput.toString(), Paths.get(protoFile));
    }
    
    private static DataFromDatabase databaseActions(List<String> args) {
        int dbIndex = args.indexOf("--from-database");
        if (dbIndex == -1){
            printHelp();
        }
        Map<String, String> connectionParameters = parseArgsForDbConnection(args);
        String schemaName = connectionParameters.get("schema");
        String fileName = CommonUtils.makeTitleCase(schemaName, false);

        Map<String, List<Entity>> data = CommonDbUtils.getDataFromDb(connectionParameters.get("connectionString"), schemaName,
                getTableNamesFromArgs(connectionParameters.get("tables")));

        return new DataFromDatabase(fileName, data);
    }

    static List<EMenuActions> notPossible = List.of(EMenuActions.NO_QUERY, EMenuActions.QUERIES_ONLY);
    private static void checkPossibility(ArrayList<EMenuActions> menuActions) {
        if (menuActions.size() > 1 && ((menuActions.contains(EMenuActions.ALL) || menuActions.contains(EMenuActions.HELP)))){
            printHelp();
        }
        if (menuActions.containsAll(notPossible)){
            throw new Error(ErrorText.QUERIES_KEY_CONFLICT.getMessage());
        }
    }

    private static String[] getTableNamesFromArgs(String args) {
        if (args == null) return new String[]{};
        return args.split(",");
    }

    private static String getConnectionStringFromArgs(List<String> args) {
        return args.stream().filter(s -> s.matches("\\w+:\\d+:\\w+:\\w+:\\w+")).collect(Collectors.joining());
    }

    private static String getSchemaNameFromArgs(List<String > args) {
        return args.get(2);
    }

    private static Map<String, String> parseArgsForDbConnection(List<String> args) {
        int dbIndex = args.indexOf("--from-database");
        HashMap<String, String> connectionParameters = new HashMap<>();
        List<String> connectionArgs = args.subList(dbIndex, args.size());
        connectionParameters.put("connectionString", getConnectionStringFromArgs(connectionArgs));
        connectionParameters.put("schema", getSchemaNameFromArgs(connectionArgs));
        if (connectionArgs.size() == 3) {
            connectionParameters.put("tables", null);
        }
        if (connectionArgs.size() == 4) {
            connectionParameters.put("tables", connectionArgs.get(connectionArgs.size() - 1));
        }
        return connectionParameters;
    }
}
