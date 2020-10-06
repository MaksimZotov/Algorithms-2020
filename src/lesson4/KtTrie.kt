package lesson4

import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.LinkedHashSet

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

        init {
            if (root.children.isNotEmpty()) {
                findAllBranches(String(), root)
            }
        }

        private fun findAllBranches(word: String, currentNode: Node) {
            if (currentNode.children.isEmpty()) {
                stack.push(if (word.last().toInt() == 0) word.substring(0, word.lastIndex) else word)
            }
            currentNode.children.forEach { findAllBranches(word + it.key, it.value) }
        }

        override fun hasNext(): Boolean =
            stack.isNotEmpty()

        override fun next(): String {
            if (stack.isEmpty()) throw IllegalStateException()
            currentWord = stack.pop()
            return currentWord!!
        }

        override fun remove() {
            if (stack.isEmpty() || currentWord == null || !this@KtTrie.remove(currentWord)) throw IllegalStateException()
            currentWord = null
        }
    }
}