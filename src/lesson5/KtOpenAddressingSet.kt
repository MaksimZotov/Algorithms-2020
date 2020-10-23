package lesson5

import java.lang.Exception
import java.lang.IllegalStateException

/**
 * Множество(таблица) с открытой адресацией на 2^bits элементов без возможности роста.
 */
class KtOpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    class Removed()

    init {
        require(bits in 2..31)
    }

    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    /**
     * Индекс в таблице, начиная с которого следует искать данный элемент
     */
    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    override fun contains(element: T): Boolean {
        var index = element.startingIndex()
        var current = storage[index]
        var count = 0 // счётчик нужен на случай вызова contains() для элемента, отсутствующего в полностью заполненной таблице
        while (current != null && count < capacity) {
            if (current == element) return true
            index = (index + 1) % capacity
            current = storage[index]
            count++
        }
        return false
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = element.startingIndex()
        var current = storage[index]
        while (current != null && current !is Removed) {
            if (current == element) return false
            index = (index + 1) % capacity
            if (index == startingIndex) throw IllegalStateException("Table is full")
            current = storage[index]
        }
        storage[index] = element
        size++
        return true
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблица, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     *
     * Средняя
     */

    // Время - O(<= N)
    // Память - O(1)
    override fun remove(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = element.startingIndex()
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                storage[index] = Removed()
                size--
                return true
            } else if (current is Removed) {
                return false
            }
            index = (index + 1) % capacity
            if (index == startingIndex) return false
            current = storage[index]
        }
        throw Exception("We can not be here")
    }


    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */
    override fun iterator(): MutableIterator<T> =
        Iterator()

    // Во всех функциях расходы по памяти равны O(1)
    // По времени суммарная сложность всех функций при проходе от первого до последнего элемента равна O(N)
    inner class Iterator : MutableIterator<T> {
        val maxIndex = storage.indexOfLast { it != null && it !is Removed }
        var currentWasRemoved = true
        var index = -1

        override fun hasNext(): Boolean =
            index != maxIndex

        override fun next(): T {
            currentWasRemoved = false
            for (i in index + 1..storage.lastIndex) {
                if (storage[i] != null && storage[i] !is Removed) {
                    index = i
                    return storage[index] as T
                }
            }
            throw IllegalStateException()
        }

        override fun remove() {
            if (currentWasRemoved) throw IllegalStateException()
            storage[index] = Removed()
            currentWasRemoved = true
            size--
        }
    }
}