@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
import kotlin.math.min
import kotlin.String

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortTimes(inputName: String, outputName: String) {
    fun compare(first: String, second: String): Boolean {
        val firstValue = first.split(Regex(":| "))
        val secondValue = second.split(Regex(":| "))

        val comparison = { first: List<String>, second: List<String> ->
            when {
                (first[3] == "PM" && second[3] == "AM") || (first[0] == "12" && first[3] == "PM" && second[3] == "AM") -> 1
                (first[0] == "12" && second[0] != "12") && ((first[3] == "AM") || (first[3] == "PM")) -> -1
                else -> 0
            }
        }

        var sign = comparison(firstValue, secondValue)
        if (sign > 0) return true else if (sign < 0) return false

        sign = -comparison(secondValue, firstValue)
        if (sign > 0) return true else if (sign < 0) return false

        for ((first, second) in listOf(
            firstValue[0].toInt() to secondValue[0].toInt(),
            firstValue[1].toInt() to secondValue[1].toInt(),
            firstValue[2].toInt() to secondValue[2].toInt()
        )) {
            if (first > second) return true
            else if (first < second) return false
        }
        return true
    }

    val list = mutableListOf<String>()

    File(inputName).forEachLine { line ->
        run {
            require(line.matches(Regex("(0|1)\\d:[0-5]\\d:[0-5]\\d (P|A)M")))
            if (list.isEmpty()) {
                list.add(line)
                return@run
            }
            if (compare(line, list.last()))
                list.add(line)
            else {
                val index = list.indexOfLast { compare(line, it) } + 1
                list.add(if (index > list.lastIndex) list.lastIndex else index, line)
            }
        }
    }

    File(outputName).bufferedWriter().use { writer ->
        list.forEach {
            writer.write(it)
            writer.newLine()
        }
    }
}

/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun sortAddresses(inputName: String, outputName: String) {
    fun comparison(current: String, other: String): Int {
        val curLength = current.length
        val otherLength = other.length
        for (i in 0 until min(curLength, otherLength)) {
            val result = current[i].toLowerCase() - other[i].toLowerCase()
            if (result != 0) return result
        }
        if (curLength != otherLength) return curLength - otherLength
        return 0
    }

    fun comparison(current: Int, other: Int): Int = current - other

    fun MutableList<Pair<String, String>>.addWithSort(element: Pair<String, String>) {
        for (i in lastIndex downTo 0) {
            val comparison = comparison(element.first, get(i).first)
            if (comparison > 0 || (comparison == 0 && comparison(element.second, get(i).second) > 0)) {
                add(i + 1, element)
                return
            }
            if (i == 0) add(0, element)
        }
    }

    fun MutableList<Pair<Pair<String, Int>, MutableList<Pair<String, String>>>>.addWithSort(
        element: Pair<Pair<String, Int>, MutableList<Pair<String, String>>>
    ) {
        for (i in lastIndex downTo 0) {
            val comparison = comparison(element.first.first, get(i).first.first)
            if (comparison > 0 || (comparison == 0 && comparison(element.first.second, get(i).first.second) > 0)) {
                add(i + 1, element)
                return
            }
            if (i == 0) add(0, element)
        }
    }

    val list = mutableListOf<Pair<Pair<String, Int>, MutableList<Pair<String, String>>>>()

    File(inputName).forEachLine { line ->
        run {
            require(line.matches(Regex("[a-zA-Zа-яА-Я-ёЁ]+ [a-zA-Zа-яА-Я-ёЁ]+ - [a-zA-Zа-яА-Я-ёЁ]+ \\d+")))
            val nameDashAddress = line.split(Regex(" - | "))
            if (list.isEmpty()) {
                list.add((nameDashAddress[2] to nameDashAddress[3].toInt()) to mutableListOf(nameDashAddress[0] to nameDashAddress[1]))
                return@run
            }
            for (i in 0..list.lastIndex) {
                if (list[i].first.first == nameDashAddress[2] && list[i].first.second == nameDashAddress[3].toInt()) {
                    list[i].second.addWithSort(nameDashAddress[0] to nameDashAddress[1])
                    break
                }
                if (i == list.lastIndex)
                    list.addWithSort((nameDashAddress[2] to nameDashAddress[3].toInt()) to mutableListOf(nameDashAddress[0] to nameDashAddress[1]))
            }
        }
    }

    File(outputName).bufferedWriter().use { writer ->
        list.forEachIndexed() { index, it ->
            run {
                writer.write(
                    "${it.first.first} ${it.first.second} - " +
                            (it.second.foldIndexed("") { i, acc, pair ->
                                acc + "${pair.first} ${pair.second}" +
                                        if (i != list[index].second.lastIndex) ", " else ""
                            })
                )
                writer.newLine()
            }
        }
    }
}


/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
fun sortTemperatures(inputName: String, outputName: String) {
    TODO()
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    TODO()
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    TODO()
}

