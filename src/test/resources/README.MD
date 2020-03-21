Чтобы запустить тесты необходимо в настройках IDEA выставить (для TestNG)
-
- Test Kind: Suite
- Указать путь к файлу testing.xml
- Выставить "In single module"
- Use classpath or module: com.anyservice
___

Чтобы проходил FileTest необходимо
- Запустить **up-several.sh** через консоль _(./up-several.sh)_. Если тест все равно не проходит, то
    - В консоли ввести команду **docker-machine ip**
    - Выведенное значение необходимо вставить в файл **bootstrap-alone.yml** в блоке **s3:** , везде где упоминается url адрес.

