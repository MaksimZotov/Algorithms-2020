package lesson4

import java.lang.StringBuilder

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

        // Время - если M > L
        //      O(<= L * K + (L + L-1 + L-2 + L-3 + ... + 1) + (M - L)) = O(<= L * K + (1 + L)/2 * L + M - L) = O(<= L * K + L^2 + M - L)
        //         иначе
        //      O(M + 1)
        //
        // Память - O(1)
        //
        // M - длина пути - 1 от текущего слова до следющего, выраженная в количестве узлов
        // L - длина ветки - 1 для текущего слова, выраженная в количестве узлов
        // K - максимальная длина мапы из всех мап из узлов, составляющих путь от текущего слова до следующего
        // Ki - длина мапы из i-го узла
        //
        // Пример:
        //
        //                 а
        //           б       е
        //      в          ж з и к л
        //   г     д
        //
        // Если допустить, что имеются слова "абвд" и "аеж", то при переходе с текущего слова "абвд" на "аеж" вначале
        //
        // мы через findNode() перейдем из "абвд" в "абв" за 3 хода (L), где пройдём по 'г', 'д' (Ki = 2). Затем снова через
        // findNode() из "абв" в "аб" - за 2 хода (L - 1) (тут проходим только по 'в'; Ki = 1),
        // из "аб" в "а" - за 1 ход (L - 2).
        //
        // После этого перейдём из "а" в "ае" за 1 ход (тут проходим по 'б', 'е'; Ki = 2)
        // и из "ае" в "аеж" тоже за 1 ход (тут проходим только по 'ж', хоть в "ае" есть и другие ключи; Ki = 1).
        //
        // Таким образом, в данном случае M = 5 (в - б - а - е - ж), K = 2, L = 3
        //
        // Другой пример:
        //
        //             а
        //         б       в
        //      г     д
        //    е ж з
        //
        // При переходе из "абг" в "абд" сложность будет O(1)
        //
        // При переходе из "аб" в "абг" мы пройдемся только по 'г' (Ki = 1). Перейдём в "абг".
        // Далее пройдемся только по 0.toChar() (Ki = 1), после чего получим "абг"
        //
        // Таким образом, M = 1 (г), K = 1, L = 0
        //
        // Важные замечания: если текущее и следующее слово лежат в одном узле, то сложность всегда O(0)
        //
        //                   если текущее слово находится в узле, являющимся родителем для следующего слова, то
        //                   сложность всегда O((M + 1) * K) = O(M + 1), то есть L = 0, так как не будет вызовов findNode(), а
        //                   Ki всегда равен 1.

        private fun getNext(lastChar: Char?): String {
            var lastChar = lastChar
            var lastCharIsFound = false
            while (true) {
                var firstNotNull: Node? = null
                var nextChar: Char? = null

                // O(Ki)
                for (char in currentNode.children.keys) {
                    if (lastChar != null && !lastCharIsFound) {
                        if (char == lastChar) {
                            lastCharIsFound = true
                        }
                        continue
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

                    // O(Li)
                    currentNode = findNode(wordForParentNode.toString())!!
                }
            }
        }

        // Время - O(1)
        // Память - O(1)
        override fun hasNext(): Boolean =
            count != 0

        // Сложность такая же, как у getNext()
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

        // Время - O(1)
        // Память - O(1)
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