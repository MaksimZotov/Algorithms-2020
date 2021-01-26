package lesson6.impl

import lesson6.Graph.Edge
import lesson6.Graph
import lesson6.Graph.Vertex

class GraphBuilder {

    data class VertexImpl(private val nameField: String) : Vertex {
        private var mark: Boolean = false

        override fun getName() = nameField

        override fun toString() = name

        override fun isMarked(): Boolean = mark

        override fun isMarked(mark: Boolean) { this.mark = mark }
    }

    data class EdgeImpl(
        private val weightField: Int,
        private val _begin: Vertex,
        private val _end: Vertex,
    ) : Edge {
        private var mark: Boolean = false

        override fun getBegin() = _begin

        override fun getEnd() = _end

        override fun isMarked(): Boolean = mark

        override fun isMarked(mark: Boolean) { this.mark = mark }

        override fun getWeight() = weightField
    }

    private val vertices = mutableMapOf<String, Vertex>()

    private val connections = mutableMapOf<Vertex, Set<EdgeImpl>>()

    private fun addVertex(v: Vertex) {
        vertices[v.name] = v
    }

    fun addVertex(name: String): Vertex {
        return VertexImpl(name).apply {
            addVertex(this)
        }
    }

    fun addConnection(begin: Vertex, end: Vertex, weight: Int = 1) {
        val edge = EdgeImpl(weight, begin, end)
        connections[begin] = connections[begin]?.let { it + edge } ?: setOf(edge)
        connections[end] = connections[end]?.let { it + edge } ?: setOf(edge)
    }

    fun build(): Graph = object : Graph {

        override fun get(name: String): Vertex? = this@GraphBuilder.vertices[name]

        override fun getVertices(): Set<Vertex> = this@GraphBuilder.vertices.values.toSet()

        override fun getEdges(): Set<Edge> {
            return connections.values.flatten().toSet()
        }

        override fun getConnections(v: Vertex): Map<Vertex, Edge> {
            val edges = connections[v] ?: emptySet()
            val result = mutableMapOf<Vertex, Edge>()
            for (edge in edges) {
                when (v) {
                    edge.begin -> result[edge.end] = edge
                    edge.end -> result[edge.begin] = edge
                }
            }
            return result
        }
    }
}