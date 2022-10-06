import java.awt.Color
import java.awt.Component
import java.awt.Graphics

import java.awt.Insets

import javax.swing.border.Border

class TreeNode<T>(val name: String, val value: T){
    private val nodes: MutableMap<String, TreeNode<T>> = mutableMapOf()

    var parent: TreeNode<T>? = null
    val count: Int get() = nodes.size
    val names: Set<String> = nodes.keys
    val children: Collection<TreeNode<T>> = nodes.values

    fun addChild(node: TreeNode<T>) {
        nodes[node.name] = node
        node.parent = this
    }

    fun addChild(name: String, value: T) = addChild(TreeNode(name, value))

    fun addChild(pair: Pair<String, T>) = addChild(pair.first, pair.second)

    fun addChildren(nodes: Map<String, T>) = nodes.forEach { (name, value) -> addChild(name, value) }

    fun addChildren(vararg nodes: Pair<String, T>) = addChildren(nodes.toMap())

    operator fun plusAssign(node: TreeNode<T>) = addChild(node)

    operator fun plusAssign(nodes: Map<String, T>) = nodes.forEach{ addChild(it.toPair()) }

    operator fun contains(node: TreeNode<T>): Boolean = node in nodes.values

    fun clearChildren() = nodes.clear()

    fun removeChild(name: String) = nodes.remove(name)

    operator fun get(name: String): TreeNode<T>? = nodes[name]

    operator fun set(name: String, node: TreeNode<T>?) {
        nodes.remove(name)
        if (node != null) {
            nodes[name] = node
            node.parent = this
        }
    }

    operator fun set(name: String, value: T) = set(name, TreeNode(name, value))

    inline fun forEach(it: (TreeNode<T>) -> Unit) = children.forEach { node -> it(node) }

    override fun toString(): String {
        var output = name
        if (count != 0)
        {
            output += " -> "
            val temp = arrayListOf<String>()
            forEach { temp.add(it.toString()) }
            output += "[ ${temp.joinToString(", ")} ]"
        }
        return output
    }
}

class ColorPalette() {
    companion object {
        val BACKGROUND =        Color(0xF2F1F6)
        val BACKGROUND_ALT =    Color(0xDEE9E7)
        val FOREGROUND =        Color(0x3F3F3F)
        val FOREGROUND_ALT =    Color(0x62717E)
        val ACCENT_LOW =        Color(0xE3F2F7)
        val ACCENT_NORMAL =     Color(0xB5CBCC)
        val ACCENT_HIGH =       Color(0xFFCAAF)
        val DISABLE =           Color(0xD1D1CF)
        val TRANSPARENT =       Color(0x00000000, true)
    }
    // Палитра - https://www.pinterest.com/pin/801781539899322636/
}
class RoundedBorder (private val radius: Int, private val background: Color) : Border {
    override fun getBorderInsets(c: Component?): Insets = Insets(radius / 5, radius / 5, radius / 5, radius / 5)

    override fun isBorderOpaque(): Boolean = true

    override fun paintBorder(c: Component?, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        g.color = background
        g.fillRoundRect(x, y, width - 1, height - 1, radius, radius)
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius)
    }
}