package lesson3

import java.lang.IllegalStateException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayDeque
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private class Node<T>(
        val value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private val subSets = mutableListOf<SubSetKtBinarySearchTree>()

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

    // Время - O(Log(N)) при равномерном распределении или O(N) при распределении в виде списка +
    //         + O(f(x1)) + O(f(x2)) + ... + O(f(xm)), где
    //         f(xi) - временная сложность функции addOutside() у i-го subSet'а
    //         m - количество "выпушенных" бинарным деревом подмножеств
    // Память - O(1)
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

        // O(f(x1)) + O(f(x2)) + ... + O(f(xm))
        subSets.forEach { it.addOutside(element) }
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

    // Время - O(Log(N)) при равномерном распределении или O(N) при распределении в виде списка +
    //         + O(f(x1)) + O(f(x2)) + ... + O(f(xm)), где
    //         f(xi) - временная сложность функции removeOutside() у i-го subSet'а
    //         m - количество "выпушенных" бинарным деревом подмножеств
    // Память - O(1)
    override fun remove(element: T): Boolean {
        val root = root ?: return false
        val comparison = element.compareTo(root.value)
        if (comparison == 0) {
            val auxiliary = root
            remove(auxiliary, auxiliary, true)
            this.root = auxiliary.right
            return true
        }

        // O(f(x1)) + O(f(x2)) + ... + O(f(xm))
        subSets.forEach { it.removeOutside(element) }
        return findAndRemove(root, element, comparison > 0)
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
        val childRight = child.right ?: return false
        val min = if (childRight.left == null) {
            child.right = childRight.right
            childRight
        } else {
            findMin(childRight)
        }
        min.left = child.left
        min.right = child.right
        if (childIsRightOf) parent.right = min else parent.left = min
        return true
    }

    private fun findMin(start: Node<T>): Node<T> {
        val left = start.left ?: throw IllegalArgumentException("start.left must not be equal to null")
        return if (left.left == null) {
            start.left = left.right
            left
        } else findMin(left)
    }


    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> =
        BinarySearchTreeIterator()

    // Частично взял решение отсюда: https://medium.com/algorithm-problems/binary-search-tree-iterator-19615ec585a
    inner class BinarySearchTreeIterator internal constructor() : MutableIterator<T> {
        private var stack = Stack<Pair<Node<T>, Node<T>?>>()
        private var currentNode: Node<T>? = null
        private var parentOfCurrentNode: Node<T>? = null

        // Время - O(N)
        // Память - O(2 * N) = O(N)
        init {
            root?.let { addToStack(it, null) }
        }

        private fun addToStack(node: Node<T>, parent: Node<T>?) {
            node.right?.let { addToStack(it, node) }
            stack.push(node to parent)
            node.left?.let { addToStack(it, node) }
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

        // Время - O(1)
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

        // Время - O(1)
        override fun next(): T {
            if (stack.isEmpty()) throw IllegalStateException()
            val childAndParent = stack.pop()
            currentNode = childAndParent.first
            parentOfCurrentNode = childAndParent.second
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

        // Время: либо O(Log(N)), либо O(N), либо O(1), либо O(L), где L - длина левой ветки у правого соседа текущего узла
        override fun remove() {
            val currentNode = currentNode ?: throw IllegalStateException()
            if (parentOfCurrentNode == null) {
                // O(Log(N)) при равномерном распределении или O(N) при распределении в виде списка
                remove(currentNode.value)
            } else {
                val parent = parentOfCurrentNode ?: return
                // O(L) - при наличии обоих потомков, O(1) - в других случаях
                remove(parent, currentNode, currentNode == parent.right)
            }
            this.currentNode = null
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
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> =
        SubSetKtBinarySearchTree(fromElement, toElement)

    private inner class SubSetKtBinarySearchTree(val fromElement: T?, val toElement: T?) : SortedSet<T> {
        private val queue: ArrayDeque<T> = ArrayDeque()
        private var auxiliaryFirst: T? = null
        private var auxiliaryLast: T? = null

        private fun elementMoreThenOrEqualToFrom(element: T): Boolean = if (fromElement == null) true else element >= fromElement
        private fun elementLessThenTo(element: T): Boolean = if (toElement == null) true else element < toElement
        private fun inRange(element: T): Boolean = elementMoreThenOrEqualToFrom(element) && elementLessThenTo(element)

        init {
            val iterator = this@KtBinarySearchTree.iterator()
            while (iterator.hasNext()) {
                val element = iterator.next()
                if (!elementLessThenTo(element)) break
                if (elementMoreThenOrEqualToFrom(element)) queue.addFirst(element)
            }
            subSets.add(this)
        }

        override val size: Int get() = queue.size
        override fun isEmpty(): Boolean = size == 0
        override fun clear() = queue.clear()

        override fun add(element: T): Boolean = if (inRange(element)) this@KtBinarySearchTree.add(element) else throw IllegalArgumentException()
        override fun remove(element: T): Boolean = if (inRange(element)) this@KtBinarySearchTree.remove(element) else throw IllegalArgumentException()
        override fun contains(element: T): Boolean = queue.contains(element)

        fun forAll(elements: Collection<T>, func: (T) -> Boolean): Boolean {
            var res = true
            elements.forEach { if (!func(it)) res = false }
            return res
        }

        override fun addAll(elements: Collection<T>): Boolean = forAll(elements, this::add)
        override fun removeAll(elements: Collection<T>): Boolean = forAll(elements, this::remove)
        override fun containsAll(elements: Collection<T>): Boolean = forAll(elements, this::contains)

        // Время - O(1)
        // Память - O(1)
        fun firstOrLast(func: () -> T): T {
            if (queue.isEmpty()) {
                if (auxiliaryFirst == null) throw NoSuchElementException()
                val res = auxiliaryFirst
                auxiliaryFirst = null
                return res!!
            }
            auxiliaryLast = func()
            return auxiliaryLast!!
        }

        override fun first(): T = firstOrLast { queue.last() }
        override fun last(): T = firstOrLast { queue.first() }

        // Время - O(<= N)   N - количество элементов данном подмножестве
        // Память - O(1)
        fun addOutside(element: T) {
            if (!inRange(element)) return
            val index = queue.indexOf(queue.firstOrNull { it > element })
            queue.add(if (index == -1) 0 else index, element)
        }

        // Время - O(<= N)   N - количество элементов данном подмножестве
        // Память - O(1)
        fun removeOutside(element: T) {
            if (!inRange(element)) return
            queue.remove(element)
        }


        override fun iterator(): MutableIterator<T> {
            TODO("Not yet implemented")
        }

        override fun tailSet(fromElement: T): SortedSet<T> {
            TODO("Not yet implemented")
        }

        override fun headSet(toElement: T): SortedSet<T> {
            TODO("Not yet implemented")
        }

        override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
            TODO("Not yet implemented")
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            TODO("Not yet implemented")
        }

        override fun comparator(): Comparator<in T>? {
            TODO("Not yet implemented")
        }
    }

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
    override fun headSet(toElement: T): SortedSet<T> =
        SubSetKtBinarySearchTree(null, toElement)


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
    override fun tailSet(fromElement: T): SortedSet<T> =
        SubSetKtBinarySearchTree(fromElement, null)

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