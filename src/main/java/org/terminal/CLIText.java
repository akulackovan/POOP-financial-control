package org.terminal;

import java.util.Map;

/**
 * Содержит текстовую справку для CLI-приложения управления финансами
 */
public class CLIText {

  /** Справка по командам авторизации */
  public static final String AUTH_HELP = """
      --- Список команд авторизации ---
      login [-u <логин> -p <пароль>]           - вход в систему
      registration [-u <логин> -p <пароль>]    - регистрация нового пользователя
      help [<команда>]                         - показать справку
      exit | q                                 - выход из программы
      """;

  /** Общая справка по основным командам */
  public static final String MAIN_HELP = """
      --- Список команд управления финансами ---
      status                                   - показать статус счета и бюджета
      actions list [параметры]                 - показать транзакции с фильтрацией
      actions remove [-i <id>]                 - удалить транзакцию по ID
      add_income [-c <категория> -a <сумма>]   - добавить доход
      add_outcome [-c <категория> -a <сумма>]  - добавить фактический расход
      set_budget [-c <категория> -a <сумма>]   - установить бюджет (планируемый расход)
      category list [-t <тип>]                 - показать категории
      category remove [-c <категория> -t <тип>] - удалить категорию
      category add [-c <категория> -t <тип>]   - добавить категорию
      category edit [-c <категория> -t <тип> -n <новое_имя>] - переименовать категорию
      export [-f <файл>]                       - экспорт данных
      import [-f <файл>]                       - импорт данных
      logout                                   - выход из профиля
      help [<команда>]                         - показать справку
      exit | q                                 - выход из программы
      """;

  /** Справка по работе с категориями */
  public static final String CATEGORY_HELP = """
      --- Управление категориями ---
      category list [-t <тип>]                 - показать категории (income/outcome)
      category add  [-c <категория> -t <тип>]  - добавть категорию
      category remove [-c <категория> -t <тип>] - удалить категорию и все связанные транзакции
      category edit [-c <категория> -t <тип> -n <новое_имя>] - переименовать категорию

      Параметры:
        -t <тип>      - тип категории: income (доходы) или outcome (расходы)
        -c <категория> - название категории
        -n <новое_имя> - новое название категории
      """;

  /** Справка по добавлению дохода */
  public static final String ADD_INCOME_HELP = """
      --- Добавление дохода ---
      add_income [-c <категория> -a <сумма>]

      Добавляет запись о доходе в указанную категорию.

      Параметры:
        -c <категория> - категория дохода (если не указана - выбор из списка)
        -a <сумма>     - сумма дохода в рублях (например: 1500.50)

      Примеры:
        add_income -c "Зарплата" -a 50000
        add_income
      """;

  /** Справка по добавлению расхода */
  public static final String ADD_OUTCOME_HELP = """
      --- Добавление расхода ---
      add_outcome [-c <категория> -a <сумма>]

      Добавляет запись о фактическом расходе в указанную категорию.

      Параметры:
        -c <категория> - категория расхода (если не указана - выбор из списка)
        -a <сумма>     - сумма расхода в рублях (например: 1500.50)

      Примеры:
        add_outcome -c "Продукты" -a 2500.75
        add_outcome
      """;

  /** Справка по установке бюджета */
  public static final String SET_BUDGET_HELP = """
      --- Установка бюджета ---
      set_budget [-c <категория> -a <сумма>]

      Устанавливает планируемый бюджет расходов для категории.

      Параметры:
        -c <категория> - категория расхода (если не указана - выбор из списка)
        -a <сумма>     - сумма бюджета в рублях (например: 10000)

      Примеры:
        set_budget -c "Развлечения" -a 5000
        set_budget
      """;

  /** Справка по просмотру транзакций */
  public static final String ACTIONS_LIST_HELP = """
      --- Просмотр транзакций ---
      actions list [-c <категория> -t <тип> -n <количество> -from <дата> -to <дата>]

      Показывает список транзакций с возможностью фильтрации.

      Параметры:
        -c <категория>   - фильтр по категории
        -t <тип>         - фильтр по типу: income или outcome
        -n <количество>  - показать последние N транзакций
        -from <дата>     - начало периода (формат: dd.MM.yyyy HH:mm)
        -to <дата>       - конец периода (формат: dd.MM.yyyy HH:mm)

      Примеры:
        actions list -c "Продукты" -t outcome
        actions list -n 10 -from "01.01.2024" -to "31.01.2024"
      """;

  /** Справка по удалению транзакций */
  public static final String ACTIONS_REMOVE_HELP = """
      --- Удаление транзакции ---
      actions remove [-i <id>]

      Удаляет транзакцию по указанному ID.

      Параметры:
        -i <id> - ID транзакции для удаления

      Пример:
        actions remove -i 5
      """;

  /** Справка по статусу */
  public static final String STATUS_HELP = """
      --- Статус счета ---
      status

      Показывает общую информацию о финансах:
        -  Общий доход, расходы и остаток
        -  Доходы по категориям
        -  Бюджет по категориям с остатками
        -  Предупреждения о превышении бюджета
      """;

  /** Справка по экспорту */
  public static final String EXPORT_HELP = """
      --- Экспорт данных ---
      export [-f <файл>]

      Экспортирует данные пользователя в JSON файл.

      Параметры:
        -f <файл> - имя файла для экспорта (если не указан - автоматическое имя)

      Пример:
        export -f "my_finances.json"
      """;

  /** Справка по импорту */
  public static final String IMPORT_HELP = """
      --- Импорт данных ---
      import [-f <файл>]

      Импортирует данные пользователя из JSON файла.

      Параметры:
        -f <файл> - имя файла для импорта

      Пример:
        import -f "my_finances.json"
      """;

  /** Справка по входу в систему */
  public static final String LOGIN_HELP = """
      --- Вход в систему ---
      login [-u <логин> -p <пароль>]

      Вход под существующим пользователем.

      Параметры:
        -u <логин> - имя пользователя
        -p <пароль> - пароль

      Пример:
        login -u ivan -p 12345
      """;

  /** Справка по регистрации */
  public static final String REGISTRATION_HELP = """
      --- Регистрация ---
      registration [-u <логин> -p <пароль>]

      Регистрация нового пользователя.

      Параметры:
        -u <логин> - имя пользователя
        -p <пароль> - пароль

      Пример:
        registration -u ivan -p 12345
      """;

  /** Справка по выходу из профиля */
  public static final String LOGOUT_HELP = """
      --- Выход из профиля ---
      logout

      Завершает текущую сессию пользователя и возвращает в меню авторизации.
      """;

  /** Справка по выходу из программы */
  public static final String EXIT_HELP = """
      --- Выход из программы ---
      exit
      q

      Завершает работу приложения.
      """;

  /** Соответствие команды с описанием */
  private static final Map<String, String> HELP_MAP = Map.ofEntries(
      Map.entry("login", LOGIN_HELP),
      Map.entry("registration", REGISTRATION_HELP),
      Map.entry("status", STATUS_HELP),
      Map.entry("actions", ACTIONS_LIST_HELP),
      Map.entry("actions list", ACTIONS_LIST_HELP),
      Map.entry("actions remove", ACTIONS_REMOVE_HELP),
      Map.entry("add_income", ADD_INCOME_HELP),
      Map.entry("add_outcome", ADD_OUTCOME_HELP),
      Map.entry("set_budget", SET_BUDGET_HELP),
      Map.entry("category", CATEGORY_HELP),
      Map.entry("category list", CATEGORY_HELP),
      Map.entry("category remove", CATEGORY_HELP),
      Map.entry("category edit", CATEGORY_HELP),
      Map.entry("export", EXPORT_HELP),
      Map.entry("import", IMPORT_HELP),
      Map.entry("logout", LOGOUT_HELP),
      Map.entry("exit", EXIT_HELP),
      Map.entry("q", EXIT_HELP));

  /**
   * Вывод справки по команде
   * @param args массив аргументов команды
   */
  public static void showHelp(String[] args) {
    if (args.length != 2) {
      System.out.println("Неправильный синтаксис команды help");
      System.out.println("Используйте: help <команда>");
      return;
    }

    String key = args[1].toLowerCase();
    String helpText = HELP_MAP.get(key);

    if (helpText != null) {
      System.out.println(helpText);
    } else {
      System.out.println("Нет справки для команды: " + key);
      System.out.println("Доступные команды для справки:");
      HELP_MAP.keySet().stream()
          .filter(k -> !k.contains(" "))
          .forEach(k -> System.out.println("  " + k));
    }
  }
}