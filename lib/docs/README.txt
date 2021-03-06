Термины и понятия

*) Торговая сессия - период отслеживания данных. Не обязательно совпадает с
торговой сессией биржи. Торговая сессия определяет период в течение которого
данные, получаемые с биржи отслеживаются и трансформируются в аналитику
(индикаторы ТА). 
*) Торговый спринт - период спекуляций. Торговая сессия может содержать один или
несколько спринтов.

Логическая схема спекулянта

1. Инициализация работы
2. Ожидание постоянных объектов
3. Выбор контракта и определение периода торговой сессии
4. Загрузка исторических данных и старт трекинга данных в рамках сессии
5. Внеторговое время, ожидание начала спринта
6. Поиск сигнала на открытие позиции
7. Открытие позиции
8. Определение точек выхода и трекинг состояния позиции
9. Нормальное закрытие позиции
10. Освобождение ресурсов по завершении периода трекинга данных
11. Завершение работы
12. Экстренное закрытие позиции
13. Критическая ситуация

1. Инициализация работы

На данном этапе выполняется загрузка конфигурации робота или его предварительная
настройка.

2. Ожидание постоянных объектов

Доступность терминалов, аккаунтов и т.п.

3. Выбор контракта и определение периода торговой сессии

Выбор контракта осуществляется при старте и каждый раз после завершения торговой
сессии. При смене контракта осуществляется запрос данных нового контракта и
отписка от предыдущего. Основные выходы из состояния:
*) Инструмент доступен
*) Время торговой сессии закончилось, а инструмент все еще недоступен
*) Какой то был еще?

4. Загрузка исторических данных и старт трекинга данных в рамках сессии

Выделено в отдельное состояние, так как набор данных индивидуален для
стратегии. По факту может занимать продолжительное время в связи с чем
рекомендуется обеспечить достаточный временной зазор между стартом сессии
и первым спринтом.

5. Внеторговое время, ожидание начала спринта

При входе в состояние осуществляется определение периода очередного спринта
и ожидание его наступления.   

6. Поиск сигнала на открытие позиции

В течение спринта отслеживать состояние рынка с целью поиска сигналов на вход
в короткую или длинную позицию. Алгоритм генерации сигналов специфичен для
каждого спекулянта. Здесь тоже может быть рассчет параметров выхода, на которые
завязаны параметры входа. 

7. Открытие позиции

Открытие позиции индивидуально в зависимости от стратегии и выделено в отдельное
состояние. Параметры входа указываются в атрибутах сигнала. Открытие позиции
может длиться продолжительное время. Условием выхода из состояния может быть
полное или частичное открытие позиции, либо завершение спринта. Здесь может
возникнуть ситуация, при которой спринт завершается при частично открытой
позиции. Подобный кейс следует рассматривать в частном порядке: возможно
принудительное закрытие перед выходом или переход в отдельное состояние
принудительного закрытия.

8. Определение точек выхода и трекинг состояния позиции 

Параметры выхода из спекуляции зависят от параметров фактического входа и
должны рассчитываться на данном этапе. Кроме этого, параметры выхода могут
меняться с течением времени или в зависимости от изменений атрибутов инструмента
(например, изменения стоимости шага цены). Здесь определяется необходимость
актуализации параметров выхода, их перерасчет и отслеживание условий выхода.
Например, это может быть отслеживание последней цены, отслеживание факта
исполнения заявки на взятие цели или стоп-лосса, отслеживание завершения спринта
и так далее. 

9. Нормальное закрытие позиции

Этап закрытия позиции при нормальных условиях. Данный этап символизирует процесс
закрытия позиции при нормальных условиях. Нормальные условия это такие, при
которых возможно ожидание наступления благоприятной ситуации, при которой
позиция будет закрыта с большей выгодой. Например, это может быть выставление
лимитных заявок с небольшим проскальзыванием в надежде на то, что цена достигнет
расчетного уровня. Возможные выходы: позиция закрыта и таймаут (или окончание
спринта). Из данного состояния обычно осуществляется переход к поиску сигналов
на вход.

10. Освобождение ресурсов по завершении периода трекинга данных

Данная фаза наступает в момент завершения торговой сессии. При этом
останавливаются сервисы отслеживания и поставки данных технического анализа,
освобождаются занятые ресурсы.

11. Завершение работы

Данный этап наступает при завершении стратегии. Обычно это принудительный
останов робота, возникающий в момент останова системы. Однако возможен условный
переход из какого либо другого состояния. На данном этапе могут выполняются
действия, характерные для фазы завершения работы. Например, вывод в журнал общей
статистики о результатах работы, отправка отчета, и т.п.

12. Экстренное закрытие позиции

Экстренное закрытие позиции это такое закрытие, при котором риск неисполнения
заявки снижается за счет повышенния потенциального убытка (например, увеличенние
резерва проскальзывания). Подобный подход применяется в тех случаях, когда в
скором времени по незакрытой позиции возникает риск возникновения убытков и он
выше чем потенциальный убыток от максимально быстрой ликвидации позиции. По
аналогии с нормальным закрытием, здесь выходы: позиция закрыта и таймаут. При
этом, вариант таймаута имеет больший приоритет внимания. Варианты использования
этого обработчика: завершение спринта, всплеск волатильности против позиции,
неустойчивое соединение с удаленной системой, и т.п.

13. Критическая ситуация

Данное состояние сигнализирует о невозможности решения проблем средствами
заложенных алгоритмов и необходимости срочного вмешательства оператора. Основная
функция данного обработчика это постановка оператора в известность всеми
возможными способами.

--------------------------------------------------------------------------------

Структура спекуляции

Спекуляция - это последовательность сделок с целью извлечения прибыли от разницы
цен открытия и закрытия позиции. Критерием оценки эффективности спекуляции
фвляется ее финансовый результат. Финансовый результат выражается в базовой
валюте (обычно совпадает с валютой стоимости шага цены инструмента). Кроме
этого, спекуляция характеризуется временем начала и завершения. Таким образом,
в целях максимального упрощения, базовая модель спекуляции ограничивается
следующими характеристиками:
1. направление BUY или SELL
2. время начала спекуляции
3. финансовый результат (положительный или отрицательный)
4. время завершения спекуляции

