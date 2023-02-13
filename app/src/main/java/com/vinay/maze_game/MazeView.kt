package com.vinay.maze_game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.vinay.maze_game.Direction.DOWN
import com.vinay.maze_game.Direction.LEFT
import com.vinay.maze_game.Direction.RIGHT
import com.vinay.maze_game.Direction.UP
import java.util.ArrayList
import java.util.Random
import java.util.Stack

class MazeView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var player: Cell? = null
    private var exit: Cell? = null
    private lateinit var cells: Array<Array<Cell?>>
    private var cellSize = 0f
    private var hMargin = 0f
    private var vMargin = 0f
    private val playerPaint: Paint
    private  val  wallPaint: Paint
    private val exitPaint: Paint
    private var random: Random

    init {
        wallPaint = Paint()
        wallPaint.color = Color.BLACK
        wallPaint.strokeWidth = WALL_THICKNESS
        playerPaint = Paint()
        playerPaint.color = Color.RED
        exitPaint = Paint()
        exitPaint.color = Color.BLUE
        random = Random()
        createMaze()
    }

    private fun createMaze() {
        val stack = Stack<Cell>()
        var current: Cell
        var next: Cell?
        cells = Array(COLS) { arrayOfNulls(ROWS) }
        for (x in 0 until COLS) {
            for (y in 0 until ROWS) {
                cells[x][y] = Cell(x, y)
            }
        }
        player = cells[0][0]
        exit = cells[COLS - 1][ROWS - 1]
        current = cells[0][0]!!
        current.visited = true
        do {
            next = getNeighbour(current)
            if (next != null) {
                removeWall(current, next)
                stack.push(current)
                current = next
                current.visited = true
            } else {
                current = stack.pop()
            }
        } while (!stack.empty())
    }

    private fun movePlayer(direction: Direction) {
        when (direction) {
            UP -> if (player?.topWall?.not()!!) {
                player = cells[player?.col!!][player?.row!! - 1]
            }
            DOWN -> if (player?.bottomWall?.not()!!) {
                player = cells[player?.col!!][player?.row!! + 1]
            }
            LEFT -> if (player?.leftWall?.not()!!) {
                player = cells[player?.col!! - 1][player?.row!!]
            }
            RIGHT -> if (player?.rightWall?.not()!!) {
                player = cells[player?.col!! + 1][player?.row!!]
            }
        }
        invalidate()
    }



    private fun removeWall(current: Cell?, next: Cell?) {
        if (current?.col == next?.col && current?.row == next?.row!! + 1) {
            current.topWall = false
            next.bottomWall = false
        }
        if (current?.col == next?.col && current?.row == next?.row!! - 1) {
            current.bottomWall = false
            next.topWall = false
        }
        if (current?.col == next?.col!! + 1 && current.row == next.row) {
            current.leftWall = false
            next.rightWall = false
        }
        if (current?.col == next.col!! - 1 && current.row == next.row) {
            current.rightWall = false
            next.leftWall = false
        }
    }

    private fun getNeighbour(cell: Cell?): Cell? {
        val neighbours = ArrayList<Cell?>()
        //left neighbour
        if (cell?.col!! > 0) {
            if (!cells[cell.col!! - 1][cell.row!!]!!.visited) {
                neighbours.add(cells[cell.col!! - 1][cell.row!!])
            }
        }

        //right neighbour
        if (cell.col!! < COLS - 1) {
            if (!cells[cell.col!! + 1][cell.row!!]!!.visited) {
                neighbours.add(cells[cell.col!! + 1][cell.row!!])
            }
        }

        //top neighbour
        if (cell.row!! > 0) {
            if (!cells[cell.col!!][cell.row!! - 1]!!.visited) {
                neighbours.add(cells[cell.col!!][cell.row!! - 1])
            }
        }

        //bottom neighbour
        if (cell.row!! < ROWS - 1) {
            if (!cells[cell.col!!][cell.row!! + 1]!!.visited) {
                neighbours.add(cells[cell.col!!][cell.row!! + 1])
            }
        }
        if (neighbours.size > 0) {
            val index = random.nextInt(neighbours.size)
            return neighbours[index]
        }
        return null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.GREEN)
        val width = width
        val height = height
        cellSize = if (width / height < COLS / ROWS) {
            width.toFloat() / (COLS + 1)
        } else {
            height.toFloat() / (ROWS + 1)
        }
        hMargin = (width - COLS * cellSize) / 2
        vMargin = (height - ROWS * cellSize) / 2
        canvas.translate(hMargin, vMargin)
        for (x in 0 until COLS) {
            for (y in 0 until ROWS) {
                if (cells[x][y]!!.topWall) {
                    canvas.drawLine(x * cellSize,
                        y * cellSize,
                        (x + 1) * cellSize,
                        y * cellSize, wallPaint
                    )
                }
                if (cells[x][y]!!.leftWall) {
                    canvas.drawLine(x * cellSize,
                        y * cellSize,
                        x * cellSize,
                        (y + 1) * cellSize, wallPaint
                    )
                }
                if (cells[x][y]!!.bottomWall) {
                    canvas.drawLine(x * cellSize,
                        (y + 1) * cellSize,
                        (x + 1) * cellSize,
                        (y + 1) * cellSize, wallPaint
                    )
                }
                if (cells[x][y]!!.rightWall) {
                    canvas.drawLine((x + 1) * cellSize,
                        y * cellSize,
                        (x + 1) * cellSize,
                        (y + 1) * cellSize, wallPaint
                    )
                }
            }
        }
        val margin = cellSize / 10
        canvas.drawRect(player?.col!! * cellSize + margin,
            player?.row!! * cellSize + margin,
            (player?.col!! + 1) * cellSize - margin,
            (player?.row!! + 1) * cellSize - margin,
            playerPaint)
        canvas.drawRect(exit?.col!! * cellSize + margin,
            exit?.row!! * cellSize + margin,
            (exit?.col!! + 1) * cellSize - margin,
            (exit?.row!! + 1) * cellSize - margin,
            exitPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            return true
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            val x = event.x
            val y = event.y
            val playerCenterX = hMargin + (player?.col!! + 0.5f) * cellSize
            val playerCenterY = vMargin + (player?.row!! + 0.5f) * cellSize
            val dx = x - playerCenterX
            val dy = y - playerCenterY
            val absDx = Math.abs(dx)
            val absDy = Math.abs(dy)
            if (absDx > cellSize || absDy > cellSize) {
                if (absDx > absDy) {
                    //move in x direction
                    if (dx > 0) {
                        //move to right
                        movePlayer(RIGHT)
                    } else {
                        //move to left
                        movePlayer(LEFT)
                    }
                } else {
                    if (dy > 0) {
                        //move down
                        movePlayer(DOWN)
                    } else {
                        // move up
                        movePlayer(UP)
                    }
                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    companion object {
        private const val COLS = 5
        private const val ROWS = 5
        private const val WALL_THICKNESS = 10f
    }
}