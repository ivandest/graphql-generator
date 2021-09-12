# graphql-generator v.0.1
Консольная утилита для создания graphql и proto файлов для дальнейшего 
использования в разработке приложений на основе микросервисной архитектуры.
Основное назначение - сокращение времени на рутинные операции по описанию контрактов
из исходных .md или .csv файлов, а также при реверс-инжиниринге баз данных.

Последнее будет удобным при разбиении монолитного приложения на микросервисы. 

USAGE: graphql-generator.jar [options] file_path

OPTIONS

***-q, --queries-only*** - generate only gql-queries and mutations

***-h, --help*** - get current usage info

***-a, --all*** - generate graphql, proto files, gql-queries and mutations

***-qn, --no-query*** - generate graphql, proto files WITHOUT gql-queries and mutations

***-f, --filters*** - with filters on each entity attribute

***-o, --order*** - with sort block

***--filter-types*** - with filter type on each entity attribute

***--order-types*** - with order type on each entity attribute

***--from-database host:port:database:login:password [schema | schema table]*** - reverse engineering of specified database"""

--------------

Input file example:

|код поля|Наименование поля|Тип данных|Обязательность|Ссылка|Комментарий|
|---|---|---|---|---|---|
|id|Идентификатор|id|(plus)|||
|caption|	Название	|varchar (30)|	(plus)|||
|long_caption	|Длинное название|	varchar (50)	|(minus)|||
|author_id|	Автор|	id	|(plus)	|employee.id||
|status	|Статус	|enum	|(plus)||draft, active, canceled|
|date_begin	|Дата начала актуальности|	timestamp|	(plus)|||
|date_end	|Дата окончания актуальности	|timestamp	|(minus)|||
