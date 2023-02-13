package com.vinay.maze_game

data class Cell(var col: Int?, var row: Int?) {
    @JvmField
    var topWall = true
    @JvmField
    var leftWall = true
    @JvmField
    var bottomWall = true
    @JvmField
    var rightWall = true
    @JvmField
    var visited = false
}