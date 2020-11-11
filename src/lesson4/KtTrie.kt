package lesson4

import lesson2.calcPrimesNumber
import java.lang.Exception
import java.lang.StringBuilder
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
        private var count = size
        private var word = StringBuilder()
        private var currentWord = mutableListOf<Char>()
        private var currentNode = root

        private fun getNext(lastChar: Char?): String {
            var lastChar = lastChar
            var lastCharIsFound = false
            while (true) {
                var firstNotNull: Node? = null
                var nextChar: Char? = null
                for (char in currentNode.children.keys) {
                    if (lastChar != null) {
                        if (!lastCharIsFound && char != lastChar) {
                            continue
                        }
                        if (!lastCharIsFound && char == lastChar) {
                            lastCharIsFound = true
                            continue
                        }
                    }
                    if (char == 0.toChar()) {
                        word.clear()
                        for (char in currentWord) {
                            word.append(char)
                        }
                        currentWord.add(0.toChar())
                        return word.toString()
                    }
                    if (firstNotNull == null && currentNode.children[char] != null) {
                        nextChar = char
                        firstNotNull = currentNode.children[char]!!
                        currentNode = firstNotNull
                        currentWord.add(nextChar)
                        break
                    }
                }
                if (nextChar == null) {
                    lastChar = currentWord.removeAt(currentWord.lastIndex)
                    lastCharIsFound = false
                    val wordForParentNode = StringBuilder()
                    for (char in currentWord) {
                        wordForParentNode.append(char)
                    }
                    currentNode = findNode(wordForParentNode.toString())!!
                }
            }
        }

        override fun hasNext(): Boolean =
            count != 0

        var nextWasInvoked = false
        override fun next(): String {
            var res = ""
            if (count == 0) {
                throw IllegalStateException()
            }
            if (!prevWordIsRemoved) {
                if (!nextWasInvoked) {
                    if (root.children.isNotEmpty()) {
                        res = getNext(null)
                    }
                    nextWasInvoked = true
                } else {
                    res = getNext(currentWord.removeAt(currentWord.lastIndex))
                }
            }
            prevWordIsRemoved = false
            count--
            return res
        }

        private var prevWordIsRemoved = false
        override fun remove() {
            if (currentWord.isEmpty() || prevWordIsRemoved) throw IllegalStateException()
            val prevNode = currentNode
            if (hasNext()) getNext(currentWord.removeAt(currentWord.lastIndex))
            if (prevNode.children.remove(0.toChar()) == null) throw IllegalStateException()
            prevWordIsRemoved = true
            size--
        }
    }
}