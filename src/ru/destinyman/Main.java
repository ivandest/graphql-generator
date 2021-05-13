package ru.destinyman;

import ru.destinyman.generator.CommonUtils;
import ru.destinyman.generator.GraphqlGenerator;
import ru.destinyman.generator.ProtoGenerator;
import ru.destinyman.parsers.Entity;
import ru.destinyman.utils.database.PostgresConnection;
import ru.destinyman.utils.database.PostgresDbObjects;
import ru.destinyman.utils.file.MarkdownFileUtils;
import ru.destinyman.utils.menu.EMenuActions;
import ru.destinyman.utils.menu.MenuActions;

import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    static boolean checkHelpKey(String arg){
       return arg.equals("-h") || arg.equals("--help");
    }

    public static void main(String[] args) {

        switch (args.length) {
            case 0: {
                MenuActions.printHelp();
            }
            case 1: {
                if (args[0].startsWith("-") && !(checkHelpKey(args[0]))){
                    throw new Error("Check that filepath was passed.");
                }
                if (checkHelpKey(args[0])){
                    MenuActions.printHelp();
                }
                MenuActions.generateDefault(args[0]);
                break;
            }
            default: {
                if (args[args.length - 1].startsWith("-")){
                    MenuActions.printHelp();
                }
                if (!args[0].startsWith("-")) {
                    MenuActions.printHelp();
                }

                ArrayList<EMenuActions> menuActions = MenuActions.getActionFromKeys(args);

                MenuActions.executeActions(menuActions, args);

                if (args[1].startsWith("-")){
                    if (args[1].startsWith("--from-database")) {
                        PostgresConnection pgConn = new PostgresConnection();
                        Connection connection = pgConn.create();

                        Map<String, List<Entity>> entities;
                        PostgresDbObjects postgresDbObjects = new PostgresDbObjects();

                        GraphqlGenerator gg = new GraphqlGenerator();
                        ProtoGenerator pg = new ProtoGenerator();

                        String fileName = CommonUtils.makeTitleCase(args[2], false) + "Service";
                        String graphqlFile = fileName.split("\\.")[0] + ".graphql";
                        String protoFile = fileName.split("\\.")[0] + ".proto";
                        MarkdownFileUtils markdownFileUtils = new MarkdownFileUtils();

                        String tableName = args[2] + "." + args[3];
                        entities = postgresDbObjects.getTableDescription(connection, tableName);
                        markdownFileUtils.write(gg.generate(entities.get(tableName), fileName), Paths.get(graphqlFile));
                        markdownFileUtils.write(pg.generate(entities.get(tableName), fileName), Paths.get(protoFile));
                    }
                }
                break;
            }
        }
    }
}
