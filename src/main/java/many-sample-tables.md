1)Таблица Test

|код поля|Наименование поля|Тип данных|Обязательность|Ссылка|Комментарий|
|---|---|---|---|---|---|
|id|Идентификатор|id|(plus)|||
|caption|	Название	|varchar (30)|	(plus)|||
|long_caption	|Длинное название|	varchar (50)	|(minus)|||
|author_id|	Автор|	id	|(plus)	|author.id||
|status	|Статус	|enum	|(plus)||draft, active, canceled|
|date_begin	|Дата начала актуальности|	timestamp|	(plus)|||
|date_end	|Дата окончания актуальности	|timestamp	|(minus)|||

2.Таблица Author

|код поля|Наименование поля|Тип данных|Обязательность|Ссылка|Комментарий|
|---|---|---|---|---|---|
|id|Идентификатор|id|(plus)|||
|family|	Фамилия	|varchar (30)|	(plus)|||
|name	|имя|	varchar (50)	|(minus)|||
|patronymic|	Отчество|	varchar(80)	||||
|status	|Статус	|enum	|(plus)||working, fired|
|department	|Отдел	|varchar(120)	|(plus)|||
|date_begin	|Дата трудоустройства|	timestamp|	(plus)|||
|date_end	|Дата увольнения	|timestamp	|(minus)|||