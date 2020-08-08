package ru.destinyman.utils;

import org.junit.jupiter.api.Test;
import ru.destinyman.parsers.Entity;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownFileUtilsTest {

    @Test
    void read() {
        
        String pathToFile = "src/sample_table.md";


        Entity headerEntity = new Entity("код поля",
                "Наименование поля",
                "Тип данных",
                "Обязательность",
                "Ссылка",
                "Комментарий"
        );

        Entity delimiterEntity = new Entity("---",
                "---",
                "---",
                "---",
                "---",
                "---");

        Entity firstEntity = new Entity("id", "Идентификатор", "id", "(plus)", "", "");

        Entity secondEntity = new Entity("caption", "Название", "varchar (30)", "(plus)", "", "");
        
        Entity thirdEntity = new Entity("long_caption", "Длинное название", "varchar (50)", "(minus)", "", "");
        
        Entity forthEntity = new Entity("author_id", "Автор", "id", "(plus)", "employee.id", "");
        
        Entity fifthEntity = new Entity("status", "Статус", "enum", "(plus)", "", "Черновик, Активен, Отменен");

        Entity sixthEntity = new Entity("date_begin", "Дата начала актуальности", "timestamp", "(plus)", "", "");

        Entity seventhEntity = new Entity("date_end", "Дата окончания актуальности", "timestamp", "(minus)", "", "");

        List<Entity> entities = new ArrayList<>();
        entities.add(headerEntity);
        entities.add(delimiterEntity);
        entities.add(firstEntity);
        entities.add(secondEntity);
        entities.add(thirdEntity);
        entities.add(forthEntity);
        entities.add(fifthEntity);
        entities.add(sixthEntity);
        entities.add(seventhEntity);

        MarkdownFileUtils markdownFileUtils = new MarkdownFileUtils();
        List<Entity> toCompare = markdownFileUtils.read(Paths.get(pathToFile));


        assertEquals(entities, toCompare);
        
    }
}