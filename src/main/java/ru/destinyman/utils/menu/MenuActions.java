package ru.destinyman.utils.menu;

import ru.destinyman.generator.CommonUtils;
import ru.destinyman.generator.GraphqlGenerator;
import ru.destinyman.generator.ProtoGenerator;
import ru.destinyman.parsers.Entity;
import ru.destinyman.utils.ErrorText;
import ru.destinyman.utils.database.CommonDbUtils;
import ru.destinyman.utils.file.MarkdownFileUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MenuActions {

    static public void printHelp(){
        System.out.println("""
                USAGE: graphql-generator.jar [options] file_path
                OPTIONS:
                   -q, --queries-only - generate only gql-queries and mutations
                   -h, --help - get current usage info
                   -a, --all - generate graphql, proto files, gql-queries and mutations
                   -qn, --no-query - generate graphql, proto files WITHOUT gql-queries and mutations
                   -f, --filters - with filters on each entity attribute
                   -o, --order - with sort block
                   --filter-types - with filter type on each entity attribute
                   --order-types - with order type on each entity attribute
                   --from-database host:port:database:login:password [schema | schema table] - reverse engineering of specified database"""
        );
        System.exit(0);
    }

    static public ArrayList<EMenuActions> getActionFromKeys(String[] args){
        List<String> result = Arrays.asList(args);

        ArrayList<EMenuActions> output = new ArrayList<>();

        if (args.length == 1 && !args[0].startsWith("-")){
            output.add(EMenuActions.ALL);
            return output;
        }
        if (args.length == 3 && args[0].equals("--from-database") && !args[1].startsWith("-") && !args[2].startsWith("-")) {
            output.add(EMenuActions.ALL);
            return output;
        }

        result.forEach(arg -> {
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

    public static void executeActions(ArrayList<EMenuActions> menuActions, String[] args) {
        checkPossibility(menuActions);

        String fileName = menuActions.contains(EMenuActions.DATABASE) ?
                CommonUtils.makeTitleCase(args[2], false)
        :
                CommonUtils.getFileName(args);

        String graphqlFile = fileName.split("\\.")[0] + "Service.graphql";
        String protoFile = fileName.split("\\.")[0] + "Service.proto";

        MarkdownFileUtils markdownFileUtils = new MarkdownFileUtils();

        Map<String, List<Entity>> data = menuActions.contains(EMenuActions.DATABASE) ?
                CommonDbUtils.getDataFromDb(args[args.length - 3], args[args.length - 2] + "." + args[args.length - 1])
                :
                markdownFileUtils.read(Paths.get(args[args.length - 1]));
        StringBuilder gqlOutput = new StringBuilder("schema {\nquery: Query\nmutation: Mutation\n}\n");
        StringBuilder protoOutput = new StringBuilder("syntax = \"proto3\";\n\npackage org.service;\n");

        GraphqlGenerator gg = new GraphqlGenerator();
        ProtoGenerator pg = new ProtoGenerator();

        String entityName = menuActions.contains(EMenuActions.DATABASE) ? fileName : CommonUtils.getFileNameWithoutExtension(args[args.length - 1]);

        if (menuActions.contains(EMenuActions.QUERIES_ONLY)){
            gqlOutput.append(gg.generateOnlyQueries(data));
            protoOutput.append(pg.generateOnlyQueries(data));
        }
        if (menuActions.contains(EMenuActions.ALL)){
            gqlOutput.append(gg.generate(data, entityName));
            protoOutput.append(pg.generate(data, entityName));
        }
        if (menuActions.contains(EMenuActions.HELP)){
            printHelp();
        }
        if (menuActions.size() == 1 && menuActions.contains(EMenuActions.DATABASE)){
            gqlOutput.append(gg.generate(data, entityName));
            protoOutput.append(pg.generate(data, entityName));
        }
        if (menuActions.contains(EMenuActions.NO_QUERY)) {
            gqlOutput.append(gg.generateEntityType(data));
            protoOutput.append(pg.generateEntityType(data));
        }

        markdownFileUtils.write(gqlOutput.toString(), Paths.get(graphqlFile));
        markdownFileUtils.write(protoOutput.toString(), Paths.get(protoFile));
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
}
