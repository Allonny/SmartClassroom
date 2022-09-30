class TreeNode<T>(var name: String, var value: T){
    var parent: TreeNode<T>? = null
    private var children: MutableMap<String, TreeNode<T>> = mutableMapOf()

    fun addChild(name: String, value: T) {
        val node = TreeNode(name, value)
        children[node.name] = node
        node.parent = this
    }

    fun clearChildren() {
        children.clear()
    }

    fun removeChild(name: String) {
        children.remove(name)
    }

    fun getChildren(): List<String> {
        return children.keys.toList()
    }

    fun childrenCount(): Int {
        return children.size
    }

    operator fun get(name: String): TreeNode<T>? {
        return children[name]
    }

    operator fun set(name: String, node: TreeNode<T>?) {
        if (node == null) children.remove(name)
        else children[name] = node
        node!!.parent = this
    }

    override fun toString(): String {
        var output = name
        if (childrenCount() != 0)
        {
            output += " -> "
            val temp = arrayListOf<String>()
            getChildren().forEach { temp.add(this[it].toString()) }

            output += "[ ${temp.joinToString(", ")} ]"
        }

        return output
    }
}