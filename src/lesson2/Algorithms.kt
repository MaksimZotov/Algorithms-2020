@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File
import java.lang.Exception
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

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
// Память - O(K)
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
// Таким образом, временная сложность: O(< N * 5)
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

// Время - O(N * M * K)   K <= M
// Память - O(1)
//
// Трудно придумать худший вариант. При first = second на i = 0 и j = 0 итерации K достигнет M, однако из-за проверки
// "if (second.length - j <= result.second) break" после этого мы никогда вновь в цикл while не попадём, так как
// second.length станет равен result.second.
//
// В то же время, чем меньше размеры совпадающих последовательностий символов из first и second, тем меньше значение K
// в силу наличия в while условия "first[i + length] == second[j + length]"
//
// Из-за такой "неопределенности" я написал функцию testLongestCommonSubstring(), расположенную чуть ниже и
// сравнивающую время работы и количество общих итераций между моим решением и стандартным (под таковым я имею в виду
// решение с двумерным массивом - тут этот массив оптимизирован по памяти: O(M) вместо O(N * M)).
//
// В ходе сравнения обнаружилось, что данный алгоритм имеет асимптотику, сильно схожую на асимптотику у стандартного алгоритма.
// Результаты тестирования приведены снизу, а под ними расположена сама testLongestCommonSubstring()
//
// По результатам видно, что иногда данное решение выигрывает по числу итераций у стандартного, но при этом проигрывает
// по времени - предполагаю, причина кроется в наличии арифметичекских вычислений в моей реализации.
fun longestCommonSubstring(first: String, second: String): String {
    var result = -1 to -1
    // O(N)
    for (i in first.indices) {
        if (first.length - i <= result.second) break
        // O(M)
        for (j in second.indices) {
            if (second.length - j <= result.second) break
            if (first[i] == second[j]) {
                var length = 1
                // O(K)
                while (i + length < first.length && j + length < second.length && first[i + length] == second[j + length]) length++
                if (length > result.second) result = j to length
            }
        }
    }
    return if (result.first < 0) "" else second.substring(result.first, result.first + result.second)
}

// Результаты testLongestCommonSubstring()
// Count - количество итераций
// Time - время выполнения
/*
    Length of the first word: 20768
    Length of the second word: 22680
       The standard solution:
           Count = 471018240
           Time = 2061
       The custom solution:
           Count = 411495309
           Time = 2357
    CountCustom / CountStandard = 0.8736292441668501
    TimeCustom / TimeStandard = 1.143619602134886
    Substring length = 2734

    Length of the first word: 26607
    Length of the second word: 27206
       The standard solution:
           Count = 723870042
           Time = 3207
       The custom solution:
           Count = 730500974
           Time = 3367
    CountCustom / CountStandard = 1.0091603901463848
    TimeCustom / TimeStandard = 1.0498908637355784
    Substring length = 159

    Length of the first word: 53903
    Length of the second word: 23002
       The standard solution:
           Count = 1239876806
           Time = 5259
       The custom solution:
           Count = 925581272
           Time = 4992
    CountCustom / CountStandard = 0.7465106755130316
    TimeCustom / TimeStandard = 0.9492298916143753
    Substring length = 7198

    Length of the first word: 369
    Length of the second word: 363
       The standard solution:
           Count = 133947
           Time = 6
       The custom solution:
           Count = 131404
           Time = 1
    CountCustom / CountStandard = 0.9810148790193136
    TimeCustom / TimeStandard = 0.16666666666666666
    Substring length = 5

    Length of the first word: 7374
    Length of the second word: 6520
       The standard solution:
           Count = 48078480
           Time = 270
       The custom solution:
           Count = 47251461
           Time = 200
    CountCustom / CountStandard = 0.982798561851373
    TimeCustom / TimeStandard = 0.7407407407407407
    Substring length = 93

    Length of the first word: 4
    Length of the second word: 4
       The standard solution:
           Count = 16
           Time = 0
       The custom solution:
           Count = 16
           Time = 0
    CountCustom / CountStandard = 1.0
    TimeCustom / TimeStandard = NaN
    Substring length = 0

    Length of the first word: 4
    Length of the second word: 4
       The standard solution:
           Count = 16
           Time = 0
       The custom solution:
           Count = 16
           Time = 0
    CountCustom / CountStandard = 1.0
    TimeCustom / TimeStandard = NaN
    Substring length = 1

    Length of the first word: 5
    Length of the second word: 5
       The standard solution:
           Count = 25
           Time = 1
       The custom solution:
           Count = 7
           Time = 0
    CountCustom / CountStandard = 0.28
    TimeCustom / TimeStandard = 0.0
    Substring length = 5

    Length of the first word: 10
    Length of the second word: 9
       The standard solution:
           Count = 90
           Time = 0
       The custom solution:
           Count = 74
           Time = 0
    CountCustom / CountStandard = 0.8222222222222222
    TimeCustom / TimeStandard = NaN
    Substring length = 3

    Length of the first word: 25
    Length of the second word: 29
       The standard solution:
           Count = 725
           Time = 0
       The custom solution:
           Count = 503
           Time = 0
    CountCustom / CountStandard = 0.6937931034482758
    TimeCustom / TimeStandard = NaN
    Substring length = 7

    Length of the first word: 7
    Length of the second word: 1
       The standard solution:
           Count = 7
           Time = 0
       The custom solution:
           Count = 7
           Time = 0
    CountCustom / CountStandard = 1.0
    TimeCustom / TimeStandard = NaN
    Substring length = 0

    Length of the first word: 14
    Length of the second word: 8
       The standard solution:
           Count = 112
           Time = 0
       The custom solution:
           Count = 86
           Time = 0
    CountCustom / CountStandard = 0.7678571428571429
    TimeCustom / TimeStandard = NaN
    Substring length = 2

    Length of the first word: 12
    Length of the second word: 12
       The standard solution:
           Count = 144
           Time = 0
       The custom solution:
           Count = 42
           Time = 0
    CountCustom / CountStandard = 0.2916666666666667
    TimeCustom / TimeStandard = NaN
    Substring length = 8
 */

fun testLongestCommonSubstring(first: String, second: String): String {
    println("Length of the first word: ${first.length}\nLength of the second word: ${second.length}")


    // The standard solution
    var resultStandard = ""
    val countStandard = first.length * second.length
    val timeStandard = measureTimeMillis {
        if (first == "" || second == "") resultStandard = ""
        val array = Array(2) { IntArray(second.length) }
        var length = 0
        var endIndex = 0
        for (i in first.indices) {
            for (j in second.indices) {
                if (first[i] == second[j]) {
                    array[1][j] = if (i != 0 && j != 0) array[0][j - 1] + 1 else 1
                    if (array[1][j] > length) {
                        length = array[1][j]
                        endIndex = i
                    }
                }
            }
            array[0] = array[1]
            array[1] = IntArray(second.length)
        }
        resultStandard = first.substring(endIndex - length + 1, endIndex + 1)
    }


    println(
        "   The standard solution:\n" +
                "       Count = $countStandard\n" +
                "       Time = $timeStandard"
    )


    // The custom solution
    var resultCustom = ""
    var countCustom = 0
    val timeCustom = measureTimeMillis {
        var result = -1 to -1
        for (i in first.indices) {
            if (first.length - i <= result.second) {
                countCustom++
                break
            }
            for (j in second.indices) {
                if (second.length - j <= result.second) {
                    countCustom++
                    break
                }
                if (first[i] == second[j]) {
                    var length = 1
                    while (i + length < first.length && j + length < second.length && first[i + length] == second[j + length]) length++
                    if (length > result.second) result = j to length
                    countCustom += length
                } else countCustom++
            }
        }
        resultCustom = if (result.first < 0) "" else second.substring(result.first, result.first + result.second)
    }


    println(
        "   The custom solution:\n" +
                "       Count = $countCustom\n" +
                "       Time = $timeCustom"
    )

    println("CountCustom / CountStandard = ${countCustom.toDouble() / countStandard}")
    println("TimeCustom / TimeStandard = ${timeCustom.toDouble() / timeStandard}")
    println("Substring length = ${resultCustom.length}\n")

    return if (resultCustom == resultStandard) resultCustom else throw Exception()
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