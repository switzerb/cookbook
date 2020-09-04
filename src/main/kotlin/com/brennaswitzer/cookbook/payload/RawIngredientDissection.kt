package com.brennaswitzer.cookbook.payload

import java.util.*

class RawIngredientDissection(var raw: String?) {
    var quantity: Section? = null
    var units: Section? = null
    var name: Section? = null
    private var prep: String? = null

    val quantityText: String?
        get() = if (quantity == null) null else quantity!!.text
    val unitsText: String?
        get() = if (units == null) null else units!!.text
    val nameText: String?
        get() = if (name == null) null else name!!.text

    fun getPrep(): String? {
        return prep
    }

    fun setPrep(prep: String?) {
        this.prep = if (prep.isNullOrBlank()) null else prep
    }

    class Section(var text: String?, var start: Int, var end: Int) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Section) return false
            return start == other.start &&
                    end == other.end &&
                    text == other.text
        }

        override fun hashCode(): Int {
            return Objects.hash(start, end, text)
        }

        override fun toString(): String {
            val sb = StringBuilder("Section{")
            sb.append("start=").append(start)
            sb.append(", end=").append(end)
            sb.append(", text='").append(text).append('\'')
            sb.append('}')
            return sb.toString()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RawIngredientDissection) return false
        return raw == other.raw &&
                quantity == other.quantity &&
                units == other.units &&
                name == other.name &&
                prep == other.prep
    }

    override fun hashCode(): Int {
        return Objects.hash(raw, quantity, units, name, prep)
    }

    override fun toString(): String {
        val sb = StringBuilder("RawIngredientDissection{")
        sb.append("raw='").append(raw).append('\'')
        sb.append(", quantity=").append(quantity)
        sb.append(", units=").append(units)
        sb.append(", name=").append(name)
        sb.append(", prep='").append(prep).append('\'')
        sb.append('}')
        return sb.toString()
    }
}