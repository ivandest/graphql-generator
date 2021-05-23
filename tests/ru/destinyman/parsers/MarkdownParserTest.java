package ru.destinyman.parsers;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownParserTest {

    @Test
    void parse() {

        Entity etalon = new Entity();
        etalon.setCode("id");
        etalon.setCaption("Идентификатор");
        etalon.setDataType("id");
        etalon.setIsNullable("(plus)");
        etalon.setReference("");
        etalon.setComment("");

        MarkdownParser markdownParser = new MarkdownParser();
        Entity toCompare = markdownParser.parse("id|Идентификатор|id|(plus)|||");

        assertEquals(etalon, toCompare);
        assertEquals(etalon, toCompare);
    }
}