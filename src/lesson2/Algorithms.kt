@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File
import kotlin.math.sqrt

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */

// Время - O(N * K)   K - переменная, меняющаяся в ходе работы программы и зависящая от содержимого входной последовательности
// Память - O(N)
//
// K <= N/2 + 1
// Пример худшего варианта входных данных (N = 20) (buysell_custom_in4.txt): 10, 11, 9, 10, 8, 9, 7, 8, 6, 7, 5, 6, 4, 5, 3, 4, 2, 3, 1, 2
// В этом случае K на каждой второй итерации (ещё на самой первой) увеличивается на 1, и на последней итерации K = 11 = N/2 + 1
// Кроме того, в блок while (iterator.hasNext()), который имеет сложность O(K), мы попадаем только на каждой второй итерации.
// Получается, что даже в худшем случае затраты по времени существенно меньше, чем O(N/2 * Kmax) = O(N/2 * (N/2 + 1)).
// Есть ещё другой пример:
//
// buysell_custom_in5.txt - файл с количеством строк (цен) N = 1_000. Цены сгенерированы случайным образом.
// В данном случае максимальное значение, которое в ходе выполнения программы принимает K, равно 5.
// Таким образом, временная сложность: O(<= N * 5)
// Если нагенерировать N = 10_000 или N = 1 млн. случайных чисел, то макс. значение K будет тоже сильно меньше самого N
//
// Все тексты лежат в: корневая папка проекта/texts
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    // список <Пара<Цена покупки, Дата покупки>, <Цена продажи, Дата продажи>>
    val list = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
    var date = 0

    // Индекс текущей пары buy-sell
    var i = -1

    // O(N)
    File(inputName).forEachLine { line ->
        require(line.matches(Regex("\\d+")))
        date++
        //считанная цена
        val price = line.toInt()

        // если считанная цена ниже текущей цены покупки
        if (list.isEmpty() || price < list[i].first.first) {
            list.add((price to date) to (price to date))
            i++

            // если считанная цена выше текущей цены продажи
        } else if (price > list[i].second.first) {
            list.add((price to date) to (price to date))
            i++

            val iterator = list.iterator()
            var buy = 0
            var buyCount = 0
            var bool = false

            // пройдем по всем некогда добавленным в список парам buy-sell, чтобы убрать однозначно плохие варианты
            // O(K)   K - количество элементов в list
            while (iterator.hasNext()) {
                // пара but-sell
                val element = iterator.next()

                // профит от пары <цена покупки, цена продажи>
                val profit = element.second.first - element.first.first

                // если считанная цена даёт больший профит, чем цена продажи из element
                if (price - element.first.first > profit) {
                    // Указываем цену покупки из element
                    buy = element.first.first
                    // Указываем дату покупки из element
                    buyCount = element.first.second
                    // удаляем element, так как нашли более подходящую пару but-sell а именно: <element.first.first, price>
                    iterator.remove()
                    i--
                    bool = true

                    // если текущая цена <= цена покупки у element && цена покупки и цена продажи у element совпадают
                } else if (price <= element.first.first && profit == 0) {
                    iterator.remove()
                    i--
                }
                // как видно, при варианте "текущая цена < цена покупки из element && профит от element > 0"
                // element не удаляется, так как в диапазоне [дата покупки из element; текущая дата]
                // может иметься такая цена продажи sell, что любая из следующих считанных цен будет < sell
            }
            if (bool) {
                list.add((buy to buyCount) to (price to date))
                i++
            }
        }
    }
    require(list.isNotEmpty())
    val result = list.maxBy { it.second.first - it.first.first }!!
    return result.first.second to result.second.second
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */

// Время - O(N)
// Память - O(1)
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    var result = 1
    val decrementedChoiceInterval = choiceInterval - 1
    for (i in 2..menNumber) result = (result + decrementedChoiceInterval) % i + 1
    return result
}


/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */

// Время - O(N * (N + K))   K - переменная, меняющаяся в ходе работы программы (K <= (1 + N)/2 * N)
// Память - O(<= N)
//
// Доказать, что это решение не явлется наивным, можно на примере:
// Есть готовый тест с двумя текстами: ruslan_ludmila_1.txt и ruslan_ludmila_2.txt
// На решение этого теста на моём ноутбуке тратится меньше секунды,
// в то время как при лобовом решении я не могу даже дождаться окончания исполнения программы.
// Кроме того, эталонное решение с двумерным массивом тратит на этот тест всего лишь в 2 раза меньше времени по сравнению с данным, но
// зато здесь задействуется меньше памяти.
//
// В худшем варианте, когда first полностью равен second, макс. значение K = (1 + N)/2 * N
// Однако даже в таком случае K достигает (1 + N)/2 * N лишь на первой итерации основного цикла. На каждой последующей итерации
// K будет иметь меньшее значение по сравнению со значением на предыдущей итерации.
// Например, при first = second и N = 5 K будет меняться следующим образом: 15 -> 14 -> 12 -> 9 -> 5
// То есть мы имеем следующую динамику изменения K в худшем случае: Kmax -> Kmax - 1 -> (Kmax - 1) - 2 -> ((Kmax - 1) - 2) - 3 -> ...
fun longestCommonSubstring(first: String, second: String): String {
    // Лист пар <Начальный индекс подстроки в second, Длина подстроки>
    val list = mutableListOf<Pair<Int, Int>>()
    var result = -1 to -1

    // O(N)
    for (i in first.indices) {
        // O(N)
        for (j in second.indices) {
            if (first[i] == second[j]) {
                list.add(j to 1)
                if (result.second < 0)
                    result = list.last()
            }
        }
        val iterator = list.iterator()
        if (iterator.hasNext()) {
            var startIndexAndLength = iterator.next()

            // O(K)
            while (true) {
                if (startIndexAndLength.second > result.second) {
                    result = startIndexAndLength
                }
                if (i + startIndexAndLength.second > first.lastIndex || startIndexAndLength.first + startIndexAndLength.second > second.lastIndex) {
                    iterator.remove()
                    if (iterator.hasNext()) {
                        startIndexAndLength = iterator.next()
                        continue
                    } else
                        break
                }
                if (first[i + startIndexAndLength.second] == second[startIndexAndLength.first + startIndexAndLength.second]) {
                    startIndexAndLength = startIndexAndLength.first to startIndexAndLength.second + 1
                } else {
                    if (startIndexAndLength.second > result.second) {
                        result = startIndexAndLength
                    }
                    iterator.remove()
                    if (iterator.hasNext()) {
                        startIndexAndLength = iterator.next()
                        continue
                    } else
                        break
                }
            }
        }
    }
    if (result.first < 0) return ""
    return second.substring(result.first, result.first + result.second)
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */

// Время - O(N^(3/2))
// Память - O(1)
fun calcPrimesNumber(limit: Int): Int {
    if (limit <= 1) return 0
    var count = 1
    for (i in 3..limit) {
        val root = sqrt(i.toFloat()).toInt() + 1
        for (j in 2..root)
            if (j == root) count++
            else if (i % j == 0) break
    }
    return count
}