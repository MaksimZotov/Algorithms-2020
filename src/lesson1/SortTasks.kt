@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
import java.lang.StringBuilder
import kotlin.math.min
import kotlin.String

fun <T : Comparable<T>> MutableList<T>.copyOfRange(begin: Int, end: Int): MutableList<T> {
    val result = mutableListOf<T>()
    for (i in begin until end)
        result.add(get(i))
    return result
}

fun <T : Comparable<T>> merge(elements: MutableList<T>, begin: Int, middle: Int, end: Int) {
    val left = elements.copyOfRange(begin, middle)
    val right = elements.copyOfRange(middle, end)
    var li = 0
    var ri = 0
    for (i in begin until end) {
        if (li < left.size && (ri == right.size || left[li] <= right[ri]))
            elements[i] = left[li++]
        else
            elements[i] = right[ri++]
    }
}

fun <T : Comparable<T>> mergeSort(elements: MutableList<T>, begin: Int, end: Int) {
    if (end - begin <= 1) return
    val middle = (begin + end) / 2
    mergeSort(elements, begin, middle)
    mergeSort(elements, middle, end)
    merge(elements, begin, middle, end)
}

fun <T : Comparable<T>> mergeSort(elements: MutableList<T>) {
    mergeSort(elements, 0, elements.size)
}

inline fun <T> fill(
    elements: MutableList<T>,
    inputName: String,
    requiredFormat: String,
    crossinline constructor: (String) -> T
) {
    File(inputName).forEachLine { line ->
        run {
            require(line.matches(Regex(requiredFormat)))
            elements.add(constructor(line))
        }
    }
}

fun <T> write(elements: MutableList<T>, outputName: String) {
    File(outputName).bufferedWriter().use { writer ->
        elements.forEach {
            writer.write(it.toString())
            writer.newLine()
        }
    }
}

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
    class Time(val name: String) : Comparable<Time> {
        private val time: List<String> = name.split(Regex(":| "))

        override operator fun compareTo(other: Time): Int =
            (60 * ((time[0].toInt() % 12) * 60 + time[1].toInt()) + time[2].toInt() + (if (time[3] == "AM") 0 else 43200)) -
                    (60 * ((other.time[0].toInt() % 12) * 60 + other.time[1].toInt()) + other.time[2].toInt() + (if (other.time[3] == "AM") 0 else 43200))

        override fun toString(): String = name
    }

    val list = mutableListOf<Time>()
    fill(list, inputName, "(0|1)\\d:[0-5]\\d:[0-5]\\d (P|A)M", ::Time)
    mergeSort(list)
    write(list, outputName)
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
    fun compare(name: String, otherName: String): Int {
        for (i in 0 until min(name.length, otherName.length)) {
            val result = name[i].toLowerCase() - otherName[i].toLowerCase()
            if (result != 0) return result
        }
        if (name.length != otherName.length) return name.length - otherName.length
        return 0
    }

    class FirstSecondName(val name: String) : Comparable<FirstSecondName> {
        override operator fun compareTo(other: FirstSecondName): Int = compare(name, other.name)

        override fun equals(other: Any?): Boolean = if (other !is FirstSecondName) false else name == other.name
    }

    class Address(val name: String) : Comparable<Address> {
        val listOfNames = mutableListOf<FirstSecondName>()

        override operator fun compareTo(other: Address): Int {
            val thisStreetAndNumber = name.split(" ")
            val otherStreetAndNumber = other.name.split(" ")
            val result = compare(thisStreetAndNumber[0], otherStreetAndNumber[0])
            return if (result == 0) thisStreetAndNumber[1].toInt() - otherStreetAndNumber[1].toInt() else result
        }

        override fun equals(other: Any?): Boolean = if (other !is Address) false else name == other.name

        override fun toString(): String {
            val stringBuilder = StringBuilder("$name - ")
            listOfNames.forEachIndexed { index, it ->
                if (index != listOfNames.lastIndex) stringBuilder.append("${it.name}, ")
                else stringBuilder.append(it.name)
            }
            return stringBuilder.toString()
        }
    }

    class FirstSecondNameDashAddress(name: String) {
        val firstSecondNameDashAddress = name.split(Regex(" - "))
        val firstSecondName = FirstSecondName(firstSecondNameDashAddress[0])
        val address = Address(firstSecondNameDashAddress[1])
    }

    val sourceList = mutableListOf<FirstSecondNameDashAddress>()
    fill(sourceList, inputName, "[a-zA-Zа-яА-Я-ёЁ]+ [a-zA-Zа-яА-Я-ёЁ]+ - [a-zA-Zа-яА-Я-ёЁ]+ \\d+", ::FirstSecondNameDashAddress)

    val resultList = mutableListOf<Address>()
    for (item in sourceList) {
        if (resultList.contains(item.address))
            resultList[resultList.indexOf(item.address)].listOfNames.add(item.firstSecondName)
        else {
            item.address.listOfNames.add(item.firstSecondName)
            resultList.add(item.address)
        }
    }

    mergeSort(resultList)
    for (item in resultList)
        mergeSort(item.listOfNames)
    write(resultList, outputName)
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

