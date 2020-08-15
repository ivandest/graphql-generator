package ru.destinyman;

import ru.destinyman.generator.GraphqlGenerator;
import ru.destinyman.parsers.Entity;
import ru.destinyman.utils.MarkdownFileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("USAGE: graphql-generator.jar [options] file_path" +
                    "OPTIONS:\n " +
                    "-q, --queries-only - generate only gql-queries\n" +
                    "-h, --help - get current usage info\n" +
                    "-a, --all - generate graphql, protofiles and gql-queries\n" +
                    "-qn, --no-query - generate graphql, protofiles WITHOUT gql-queries\n" +
                    "-f, --filters - with filters on each entity attribute\n" +
                    "-o, --order - with sort block\n" +
                    "--filter-types - with filter type on each entity attribute\n" +
                    "--order-types - with order type on each entity attribute"
            );
            System.exit(0);
        }

        if (args[0].startsWith("-") || args[0].startsWith("--")){

        } else {
            String pathToFile = args[0];
            MarkdownFileUtils markdownFileUtils = new MarkdownFileUtils();
            List<Entity> data = markdownFileUtils.read(Paths.get(pathToFile));
            String[] filePathParts = pathToFile.split("/");
            String fileName = filePathParts[filePathParts.length - 1];
            GraphqlGenerator gg = new GraphqlGenerator();
            try {
                String graphqlFile = fileName.split("\\.")[0] + ".graphql";

                markdownFileUtils.write(gg.generate(data, fileName), Paths.get(graphqlFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
