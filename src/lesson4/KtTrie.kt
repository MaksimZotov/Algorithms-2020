package lesson4

import java.util.*

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: MutableMap<Char, Node> = linkedMapOf()
    }

    private var root = Node()

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator(): MutableIterator<String> =
        IteratorKtTrie()

    inner class IteratorKtTrie : MutableIterator<String> {
        private val stack = Stack<String>()
        private var currentWord: String? = null

        // Время - O(N + M * L) в худшем случае
        // Память - O(M * L) в худшем случае
        // N - суммарное количество букв во всех словах, M - количество слов, L - максимальная длина слова
        init {
            if (root.children.isNotEmpty()) {
                findAllBranches(String(), root)
            }
        }

        // Время - O(N + M * L) в худшем случае
        // Память - O(M * L) в худшем случае
        // N - суммарное количество букв во всех словах, M - количество слов, L - максимальная длина слова
        //
        // В ходе рекурсивного обхода создаётся много мусора (word + it.key) но
        // он мало времени находиться в зоне видимости, поэтому его не учитывал
        private fun findAllBranches(word: String, currentNode: Node) {
            if (currentNode.children.isEmpty()) {
                val toPush = if (word.last().toInt() == 0) word.substring(0, word.lastIndex) else word

                // O(L)
                if (toPush !in this@KtTrie) return

                stack.push(toPush)
            }
            currentNode.children.forEach { findAllBranches(word + it.key, it.value) }
        }

        // Время - O(1)
        override fun hasNext(): Boolean =
            stack.isNotEmpty()

        // Время - O(1)
        // Память - уменьшается на единицу количество хранящихся в стеке элементов
        override fun next(): String {
            if (stack.isEmpty()) throw IllegalStateException()
            currentWord = stack.pop()
            return currentWord!!
        }

        // Время - O(N)   N - длина currentWord
        // Память - добавление данных в память не происходит
        override fun remove() {
            if (currentWord == null || !this@KtTrie.remove(currentWord)) throw IllegalStateException()
            currentWord = null
        }
    }
}