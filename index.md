Graphql-generator - простая консольная утилита для получения graphql и protobuf файлов. 
В первой версии доступно:
- создание файлов из файла Markdown(.md) - graphql-generator.jar \<path-to file\>
- создание файлов из таблицы базы данных - graphql-generator.jar --from-database \<host:port:database:user:password\> \<schema\> \<table\>
- использование ключей -a(--all), --from_database, -q(--queries-only), -h(--help), -qn(--no-query)