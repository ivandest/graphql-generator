package ru.destinyman.generator;

import org.junit.jupiter.api.Test;
import ru.destinyman.parsers.Entity;
import ru.destinyman.utils.file.MarkdownFileUtils;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GraphqlGeneratorTest {

    GraphqlGenerator graphqlGenerator = new GraphqlGenerator();

    String pathToFile = "src/sample_table.md";
    MarkdownFileUtils markdownFileUtils = new MarkdownFileUtils();
    Map<String, List<Entity>> data = markdownFileUtils.read(Paths.get(pathToFile));
    String[] filePathParts = pathToFile.split("/");
    String fileName = filePathParts[filePathParts.length - 1];


    @Test
    void generate() {
        String etalon = "schema {\nquery: Query\nmutation: Mutation\n}";
        assertEquals(etalon, graphqlGenerator.generate(data, fileName));
    }


    @Test
    void generateQuery() {
        String etalon = "type Query {\ngetRejectionReportFileList(args: GetRejectionReportFileListRequest): GetRejectionReportFileListResponse\n}";

        assertEquals(etalon, graphqlGenerator.generateQuery("rejection_report_file"));
    }

    @Test
    void makeTitleCase() {
        String input = "sample_entity";
        String etalon = "SampleEntity";
        assertEquals(etalon, CommonUtils.makeTitleCase(input, false));
    }

    @Test
    void generateEntityType() {
        graphqlGenerator.generateEntityType(data);

    }
}