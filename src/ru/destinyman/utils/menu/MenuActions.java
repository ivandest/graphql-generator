package ru.destinyman.utils.menu;

import ru.destinyman.generator.GraphqlGenerator;
import ru.destinyman.generator.ProtoGenerator;
import ru.destinyman.parsers.Entity;
import ru.destinyman.utils.file.MarkdownFileUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuActions {



    static public void printHelp(){
        System.out.println("USAGE: graphql-generator.jar [options] file_path\n" +
                "OPTIONS:\n" +
                "   -q, --queries-only - generate only gql-queries\n" +
                "   -h, --help - get current usage info\n" +
                "   -a, --all - generate graphql, protofiles and gql-queries\n" +
                "   -qn, --no-query - generate graphql, protofiles WITHOUT gql-queries\n" +
                "   -f, --filters - with filters on each entity attribute\n" +
                "   -o, --order - with sort block\n" +
                "   --filter-types - with filter type on each entity attribute\n" +
                "   --order-types - with order type on each entity attribute\n" +
                "   --from-database - reverse engineering of specified database"
        );
        System.exit(0);
    }

    static public void generateDefault(String pathToFile){
        MarkdownFileUtils markdownFileUtils = new MarkdownFileUtils();
        List<Entity> data = markdownFileUtils.read(Paths.get(pathToFile));
        String[] filePathParts = pathToFile.split("/");
        String fileName = filePathParts[filePathParts.length - 1];
        GraphqlGenerator gg = new GraphqlGenerator();
        ProtoGenerator pg = new ProtoGenerator();
        String graphqlFile = fileName.split("\\.")[0] + ".graphql";
        String protoFile = fileName.split("\\.")[0] + ".proto";
        markdownFileUtils.write(gg.generate(data, fileName), Paths.get(graphqlFile));
        markdownFileUtils.write(pg.generate(data, fileName), Paths.get(protoFile));
    }

    static public ArrayList<EMenuActions> getActionFromKeys(String[] args){
        List<String> result = Arrays.asList(args);

        ArrayList<EMenuActions> output = new ArrayList<>();
        result.forEach(arg -> {
            switch (arg) {
                case "-q":
                case "--queries-only": {
                    output.add(EMenuActions.QUERIES_ONLY);
                    break;
                }
                case "-a":
                case "--all": {
                    output.add(EMenuActions.ALL);
                    break;
                }
                case "-qn":
                case "--no-query":{
                    output.add(EMenuActions.NO_QUERY);
                    break;
                }
                case "-f":
                case "--filters": {
                    output.add(EMenuActions.FILTERS);
                    break;
                }
                case "-o":
                case "--order":{
                    output.add(EMenuActions.ORDER);
                    break;
                }
                case "--filter-types": {
                    output.add(EMenuActions.FILTER_TYPES);
                    break;
                }
                case "--order-types":{
                    output.add(EMenuActions.ORDER_TYPES);
                    break;
                }
                case "--from-database": {
                    output.add(EMenuActions.DATABASE);
                    break;
                }
            }
        });
        return output;
    }

    public static void executeActions(ArrayList<EMenuActions> menuActions, String[] args) {
        checkPossibility(menuActions);
    }

    static List<EMenuActions> notPossible = List.of(EMenuActions.NO_QUERY, EMenuActions.QUERIES_ONLY);
    private static void checkPossibility(ArrayList<EMenuActions> menuActions) {
        if (menuActions.size() > 1 && ((menuActions.contains(EMenuActions.ALL) || menuActions.contains(EMenuActions.HELP)))){
            printHelp();
        }
        if (menuActions.containsAll(notPossible)){
            throw new Error("NO_QUERIES conflict with QUERIES_ONLY");
        }
    }
}
