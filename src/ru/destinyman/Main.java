package ru.destinyman;

import ru.destinyman.generator.GraphqlGenerator;
import ru.destinyman.generator.ProtoGenerator;
import ru.destinyman.parsers.Entity;
import ru.destinyman.utils.database.PostgresConnection;
import ru.destinyman.utils.database.PostgresDbObjects;
import ru.destinyman.utils.file.MarkdownFileUtils;

import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0){
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

        if (args[1].startsWith("-") || args[1].startsWith("--")){
            if (args[1].startsWith("--from-database")) {
                PostgresConnection pgConn = new PostgresConnection();
                Connection connection = pgConn.create();

                Map<String, List<Entity>> entities;
                PostgresDbObjects postgresDbObjects = new PostgresDbObjects();

                GraphqlGenerator gg = new GraphqlGenerator();
                ProtoGenerator pg = new ProtoGenerator();

                String fileName = "rejection-service";
                String graphqlFile = fileName.split("\\.")[0] + ".graphql";
                String protoFile = fileName.split("\\.")[0] + ".proto";
                MarkdownFileUtils markdownFileUtils = new MarkdownFileUtils();

                entities = postgresDbObjects.getTableDescription(connection, "rejection.rejection");
                markdownFileUtils.write(gg.generate(entities.get("rejection.rejection"), fileName), Paths.get(graphqlFile));
                markdownFileUtils.write(pg.generate(entities.get("rejection.rejection"), fileName), Paths.get(protoFile));
            }

        } else {
            String pathToFile = args[0];
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
    }
}
