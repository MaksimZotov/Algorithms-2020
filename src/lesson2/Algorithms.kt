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
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    val list = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
    var count = 0
    var i = -1
    File(inputName).forEachLine { line ->
        require(line.matches(Regex("\\d+")))
        count++
        val price = line.toInt()
        do {
            if (list.isEmpty() || price < list[i].first.first) {
                list.add((price to count) to (price to count))
                i++
            } else if (price > list[i].second.first) {
                list.add((price to count) to (price to count))
                i++
                val iterator = list.iterator()
                var buy = 0
                var buyCount = 0
                var bool = false
                while (iterator.hasNext()) {
                    val element = iterator.next()
                    val dif = element.second.first - element.first.first
                    if (price - element.first.first > dif) {
                        buy = element.first.first
                        buyCount = element.first.second
                        iterator.remove()
                        i--
                        bool = true
                    } else if (price <= element.first.first && dif == 0) {
                        iterator.remove()
                        i--
                    }
                }
                if (bool) {
                    list.add((buy to buyCount) to (price to count))
                    i++
                }
            }
        } while (i != list.lastIndex)
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
fun longestCommonSubstring(first: String, second: String): String {
    val list = mutableListOf<MutableList<Int>>()
    val result = mutableListOf<MutableList<Int>>()
    for (i in first.indices) {
        for (j in second.indices)
            if (first[i] == second[j])
                list.add(mutableListOf(j))
        var j = 1
        var iterator = list.iterator()
        while (i + j < first.length) {
            while (iterator.hasNext()) {
                if (i + j >= first.length) break
                val element = iterator.next()
                if (element[0] + j > second.lastIndex) continue
                if (first[i + j] == second[element[0] + j]) {
                    element.add(element[0] + j)
                    iterator = list.iterator()
                    j++
                } else {
                    result.add(element)
                    iterator.remove()
                }
            }
            j++
        }
    }
    if (result.isEmpty()) return ""
    val res = result.maxBy() { it.size }!!
    return second.substring(res.first(), res.last() + 1)
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