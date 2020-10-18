@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
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

// Время - O(N*Log(N))
// Память - O(N)
fun sortTimes(inputName: String, outputName: String) {
    class Time(val name: String) : Comparable<Time> {
        val time: List<String> = name.split(Regex(":| "))
        val value = (60 * ((time[0].toInt() % 12) * 60 + time[1].toInt()) + time[2].toInt() + (if (time[3] == "AM") 0 else 43200))

        override operator fun compareTo(other: Time): Int = value - other.value
        override fun toString(): String = name
    }

    val list = mutableListOf<Time>()
    File(inputName).forEachLine { line ->
        require(line.matches(Regex("(0|1)\\d:[0-5]\\d:[0-5]\\d (P|A)M")))
        list.add(Time(line))
    }
    File(outputName).bufferedWriter().use { writer -> list.sorted().forEach { writer.write("${it}\n") } }
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

// Время - O(N*Log(N))
// Память - O(N)
fun sortAddresses(inputName: String, outputName: String) {
    val comparator = Comparator<String> { address, otherAddress ->
        val streetAndNumber = address.split(" ")
        val street = streetAndNumber[0]
        val number = streetAndNumber[1]

        val otherStreetAndNumber = otherAddress.split(" ")
        val otherStreet = otherStreetAndNumber[0]
        val otherNumber = otherStreetAndNumber[1]

        val result = street.compareTo(otherStreet)
        return@Comparator if (result == 0) {
            val dif = number.length - otherNumber.length
            if (dif != 0) dif else number.compareTo(otherNumber)
        } else result
    }
    val addressAndNames = sortedMapOf<String, MutableList<String>>(comparator)
    File(inputName).forEachLine { line ->
        require(line.matches(Regex("[a-zA-Zа-яА-Я-ёЁ]+ [a-zA-Zа-яА-Я-ёЁ]+ - [a-zA-Zа-яА-Я-ёЁ]+ \\d+")))
        val nameAndAddress = line.split(" - ")
        val name = nameAndAddress[0]
        val address = nameAndAddress[1]
        addressAndNames.getOrPut(address) { mutableListOf() }.add(name)
    }
    File(outputName).bufferedWriter().use { writer ->
        addressAndNames.forEach {
            writer.write("${it.key} - ")
            val sortedNames = it.value.sorted()
            sortedNames.forEachIndexed { i, s -> if (i == sortedNames.lastIndex) writer.write(s) else writer.write("$s, ") }
            writer.newLine()
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

// Время - O(N)
// Память - O(N)
fun sortTemperatures(inputName: String, outputName: String) {
    val array = Array<Pair<Int, String?>>(7731) { 0 to null }
    File(inputName).forEachLine { line ->
        val i = line.replace(".", "").toInt() + 2730
        array[i] = array[i].first + 1 to line
    }
    File(outputName).bufferedWriter().use { writer -> array.forEach { for (i in 1..it.first) writer.write("${it.second}\n") } }
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

// Время - O(N)
// Память - O(N)
fun sortSequence(inputName: String, outputName: String) {
    val sourceList = mutableListOf<Int>()
    var max = Int.MIN_VALUE

    val map = hashMapOf<Int, Int>()
    // O(N)
    File(inputName).forEachLine { line ->
        val key = line.toInt()
        // O(1) - так как hashMap
        val value = map[key]
        if (value != null) {
            val newValue = value + 1
            // O(1)
            map[key] = newValue
            if (newValue > max)
                max = newValue
            // O(1)
        } else map[key] = 1
        // O(1)
        sourceList.add(key)
    }

    // O(<= N)
    val updatedMap = map.filter { it.value == max }
    // O(<= N)
    val result = (updatedMap.minByOrNull { it.key } ?: return).key

    File(outputName).bufferedWriter().use { writer ->
        var count = 0
        // O(N)
        sourceList.forEach {
            if (it == result) count++
            else writer.write("$it\n")
        }
        for (i in 1..count)
            writer.write("$result\n")
    }
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

// Время - O(N)
// Память - O(N)
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    val right = second.copyOfRange(first.size, second.size)
    var li = 0
    var ri = 0
    for (i in second.indices)
        if (li < first.size && (ri == right.size || first[li] <= right[ri]!!)) second[i] = first[li++]
        else second[i] = right[ri++]
}

