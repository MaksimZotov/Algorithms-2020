package lesson3

import java.lang.IllegalStateException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private class Node<T>(
        val value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private var root: Node<T>? = null

    override var size = 0
        private set

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: [java.util.Set.add] (Ctrl+Click по add)
     *
     * Пример
     */
    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     * (в Котлине тип параметера изменён с Object на тип хранимых в дереве данных)
     *
     * Средняя
     */

    // Время - O(Log(N)) при равномерном распределении или O(N) при распределении в виде списка
    override fun remove(element: T): Boolean {
        if (root == null) return false
        val comparison = element.compareTo(root!!.value)
        if (comparison == 0) {
            val auxiliary = root!!
            remove(auxiliary, auxiliary, true)
            root = auxiliary.right
            return true
        }
        return findAndRemove(root!!, element, comparison > 0)
    }

    private fun findAndRemove(start: Node<T>, value: T, childIsRightOf: Boolean): Boolean {
        val next = if (childIsRightOf) start.right else start.left
        val comparison = next?.let { value.compareTo(it.value) } ?: return false
        return when {
            comparison != 0 -> findAndRemove(next, value, comparison > 0)
            else -> remove(start, next, childIsRightOf)
        }
    }

    private fun remove(parent: Node<T>, child: Node<T>, childIsRightOf: Boolean): Boolean {
        size--
        if (child.left == null || child.right == null) {
            val child = when {
                child.left == null && child.right == null -> null
                child.left == null -> child.right
                else -> child.left
            }
            if (childIsRightOf) parent.right = child else parent.left = child
            return true
        }
        val min = if (child.right!!.left == null) {
            val res = child.right!!
            child.right = child.right!!.right
            res
        } else {
            findMin(child.right!!)
        }
        min.left = child.left
        min.right = child.right
        if (childIsRightOf) parent.right = min else parent.left = min
        return true
    }

    private fun findMin(start: Node<T>): Node<T> =
        if (start.left!!.left == null) {
            val res = start.left!!
            start.left = start.left!!.right
            res
        } else findMin(start.left!!)


    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> =
        BinarySearchTreeIterator()

    inner class BinarySearchTreeIterator internal constructor() : MutableIterator<T> {
        private var stack: Stack<Node<T>> = Stack<Node<T>>()
        private var currentNode: Node<T>? = null

        init {
            pushToLeft(root)
        }

        private fun pushToLeft(node: Node<T>?) {
            if (node != null) {
                stack.push(node)
                pushToLeft(node.left)
            }
        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: [java.util.Iterator.hasNext] (Ctrl+Click по hasNext)
         *
         * Средняя
         */


        override fun hasNext(): Boolean =
            stack.isNotEmpty()

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: [java.util.Iterator.next] (Ctrl+Click по next)
         *
         * Средняя
         */

        override fun next(): T {
            if (stack.isEmpty()) throw IllegalStateException()
            currentNode = stack.pop()
            pushToLeft(currentNode!!.right)
            return currentNode!!.value
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: [java.util.Iterator.remove] (Ctrl+Click по remove)
         *
         * Сложная
         */

        override fun remove() {
            if (currentNode == null) throw IllegalStateException()
            remove(currentNode!!.value)
            currentNode = null
        }
    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.subSet] (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO() //return SubSetKtBinarySearchTree(fromElement, toElement)
    }

    /*

    private inner class SubSetKtBinarySearchTree(val fromElement: T, val toElement: T) : SortedSet<T> {
        val set = sortedSetOf<T>()

        init {
            update()
            val t = 0
        }

        private fun update() {
            set.clear()
            val iterator = this@KtBinarySearchTree.iterator()
            while (iterator.hasNext()) {
                val element = iterator.next()
                println(element)
                val moreOrEqual = element.compareTo(fromElement) >= 0
                val less = element.compareTo(toElement) < 0
                //if (!less) break
                if (moreOrEqual) set.add(element)
            }
        }

        override fun add(element: T): Boolean {
            if (!this@KtBinarySearchTree.add(element)) return false
            update()
            return true
        }

        override fun addAll(elements: Collection<T>): Boolean {
            TODO("Not yet implemented")
        }

        override fun clear() {
            TODO("Not yet implemented")
        }

        override fun iterator(): MutableIterator<T> {
            TODO("Not yet implemented")
        }

        override fun removeAll(elements: Collection<T>): Boolean {
            TODO("Not yet implemented")
        }

        override fun contains(element: T): Boolean {
            return set.contains(element)
        }

        override fun tailSet(fromElement: T): SortedSet<T> {
            TODO("Not yet implemented")
        }

        override fun first(): T {
            return set.first()
        }

        override fun headSet(toElement: T): SortedSet<T> {
            TODO("Not yet implemented")
        }

        override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
            TODO("Not yet implemented")
        }

        override fun remove(element: T): Boolean {
            if (element.compareTo(fromElement) < 0 || element.compareTo(toElement) >= 0) throw IllegalArgumentException()
            if (!this@KtBinarySearchTree.remove(element) || !set.remove(element)) return false
            update()
            return true
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            TODO("Not yet implemented")
        }

        override val size: Int
            get() = set.size

        override fun containsAll(elements: Collection<T>): Boolean {
            TODO("Not yet implemented")
        }

        override fun isEmpty(): Boolean {
            return size == 0
        }

        override fun comparator(): Comparator<in T>? {
            TODO("Not yet implemented")
        }

        override fun last(): T {
            return set.last()
        }

    }
    */

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.headSet] (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.tailSet] (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }

    override fun height(): Int =
        height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

}