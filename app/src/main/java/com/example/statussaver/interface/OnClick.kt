package com.example.statussaver.`interface`

import java.io.Serializable

interface OnClick : Serializable {
    fun position(position: Int, type: String?)
}
