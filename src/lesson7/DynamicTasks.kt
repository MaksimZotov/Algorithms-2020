@file:Suppress("UNUSED_PARAMETER")

package lesson7


/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */
// Время - O(N * M)
// Память - O(N * M)
//
// Решения взято отсюда: https://introcs.cs.princeton.edu/java/23recursion/LongestCommonSubsequence.java.html
fun longestCommonSubSequence(first: String, second: String): String {
    val array = Array(first.length + 1) { IntArray(second.length + 1) }
    for (i in first.length - 1 downTo 0)
        for (j in second.length - 1 downTo 0) {
            if (first[i] == second[j]) array[i][j] = array[i + 1][j + 1] + 1
            else array[i][j] = Math.max(array[i + 1][j], array[i][j + 1])
        }
    val result = StringBuilder()
    var i = 0
    var j = 0
    while (i < first.length && j < second.length) {
        if (first[i] == second[j]) {
            result.append(first[i])
            i++
            j++
        } else if (array[i + 1][j] >= array[i][j + 1]) i++ else j++
    }
    return result.toString()
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */

// Время - O(<= N^2)
// Память - O(N)
//
// Решения взято отсюда: https://stackoverflow.com/questions/54885750/first-longest-increasing-subsequence
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    val data = Array(list.size) { IntArray(2) }
    var maxLength = 0

    // O(N)
    for (i in list.indices) {
        data[i][0] = -1
        data[i][1] = 1
        // O(i)   i меняется так: 1 -> 2 -> 4 -> 5 -> 6 -> ...
        for (j in i - 1 downTo 0) {
            if (list[i] > list[j]) {
                if (data[i][1] <= data[j][1] + 1) {
                    data[i][1] = data[j][1] + 1
                    data[i][0] = j
                }
            }
        }
        maxLength = Math.max(maxLength, data[i][1])
    }
    val result = mutableListOf<Int>()

    // O(N)
    for (i in list.indices) {
        if (data[i][1] == maxLength) {
            var cur = i
            // O(?)
            while (cur != -1) {
                result.add(list[cur])
                cur = data[cur][0]
            }
            break
        }
    }
    result.reverse()
    return result
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
fun shortestPathOnField(inputName: String): Int {
    TODO()
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5