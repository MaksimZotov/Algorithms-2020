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
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    TODO()
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

// Время - O(N * (N + K))   K - переменная, меняющаяся в ходе работы программы (K < N)
// Память - O(N)
fun longestCommonSubstring(first: String, second: String): String {
    val list = mutableListOf<MutableList<Int>>()
    var result = mutableListOf<Int>()

    // O(N)
    for (i in first.indices) {
        // O(N)
        for (j in second.indices)
            if (first[i] == second[j]) {
                list.add(mutableListOf(j))
                if (result.isEmpty())
                    result = list.last()
            }
        var j = 1
        var iterator = list.iterator()

        // O(K)
        while (iterator.hasNext() && i + j < first.length) {
            val element = iterator.next()
            if (element[0] + j > second.lastIndex) continue
            if (first[i + j] == second[element[0] + j]) {
                element.add(element[0] + j)
                iterator = list.iterator()
                j++
            } else {
                if (element.size > result.size) result = element
                iterator.remove()
            }
        }
    }
    if (result.isEmpty()) return ""
    return second.substring(result.first(), result.last() + 1)
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
// Память - O(N)
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