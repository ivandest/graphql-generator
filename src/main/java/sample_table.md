 Таблица Reference
|код поля|Наименование поля|Тип данных|Обязательность|Ссылка|Комментарий|
|---|---|---|---|---|---|
|id|Идентификатор|id|(plus)|||
|caption|	Название	|varchar (30)|	(plus)|||
|long_caption	|Длинное название|	varchar (50)	|(minus)|||
|author_id|	Автор|	id	|(plus)	|employee.id||
|status	|Статус	|enum	|(plus)||draft, active, canceled|
|date_begin	|Дата начала актуальности|	timestamp|	(plus)|||
|date_end	|Дата окончания актуальности	|timestamp	|(minus)|||