import java.io.File
import kotlin.math.roundToInt

class InnerRectangle(val lx: Int, val ly: Int, val color:Int, val cost: Int)
data class Rectangle(val lx: Int, val rx: Int, val ly: Int, val ry: Int, val color: Int)

val Double.mergeCost: Int get() = (BASE_MERGE / this).roundToInt()

fun bestMergeCost(x: Int, xl:Int, y:Int, yl:Int) :Int {
    //    x
    //   0|1
    // y ---
    //   2|3
    val p0 = x * y * 1.0 / xl / yl
    val p1 = (xl - x) * y * 1.0 / xl / yl
    val p2 = x * (yl - y) * 1.0 / xl / yl
    val p3 = (xl - x) * (yl - y) * 1.0 / xl / yl


    return minOf(
        maxOf(p0, p1).mergeCost + maxOf(p2, p3).mergeCost + maxOf(p0 + p1, p2 + p3).mergeCost,
        maxOf(p0, p2).mergeCost + maxOf(p1, p3).mergeCost + maxOf(p0 + p2, p1 + p3).mergeCost,
    )
}

class YoungSolver {
    val cache = mutableMapOf<String, Int>()

    fun solve(xs: IntArray, ys: IntArray, colorIds: List<IntArray>): List<Rectangle> {
        require(colorIds.size + 1 == xs.size)
        require(colorIds.all { it.size + 1 == ys.size })
        val r = xs.size - 1
        val c = ys.size - 1
        val gridSize = (xs.last() - xs.first()) * (ys.last() - ys.first())
        fun moves(a: IntArray) = buildList {
            require(a.size == r)
            for (i in a.indices) {
                if (a[i] < (a.getOrNull(i - 1) ?: c)) {
                    require(i < r)
                    require(a[i] < c)
                    val cost =
                        BASE_POINT_CUT * 1 + // cut(xs[i], ys[a[i]])
                        (BASE_COLOR.toDouble() * gridSize / (xs.last() - xs[i]) / (ys.last() - ys[i])).roundToInt() + // color(UP_RIGHT)
                        bestMergeCost(xs[i], xs.last(), ys[i], ys.last())

                    val na = a.clone().apply { set(i, a[i] + 1) }
                    add(na to InnerRectangle(i, a[i], colorIds[i][a[i]], cost))
                }
            }
        }.take(1)
        fun go(a: IntArray) : Int = cache.getOrPut(a.contentToString()) {
            if (a.all { it == c })
                0
            else {
                moves(a).minOf { (to, rect) ->
                    go(to) + rect.cost
                }
            }
        }
        val score = go(IntArray(r) { 0 })
        println(score)
        var cur = IntArray(r) { 0 }
        return buildList {
            while (!cur.all { it == c }) {
                val (next, move) = moves(cur).first { (nxt, rect) -> go(nxt) + rect.cost == go(cur) }
                add(Rectangle(xs[move.lx], xs.last(), ys[move.ly], ys.last(), move.color))
                cur = next
            }
        }
    }
}

fun main() {
    for (testId in 1 .. 10) {
        val input = read(testId)
        val BLOCK = 40
        val xs = (0..400 step BLOCK).toList().toIntArray()
        val ys = (0..400 step BLOCK).toList().toIntArray()

        val data = xs.asSequence().zipWithNext { xl, xr ->
            ys.asSequence().zipWithNext { yl, yr ->
                (xl until xr).flatMap { x -> (yl until yr).map { y -> input[x][y] } }.groupingBy { it }
                    .eachCount()
                    .entries
                    .maxBy { it.value }
                    .key
            }
        }.map { it.toList().toIntArray() }.toList()


        val solution = YoungSolver().solve(xs, ys, data)
        var id = 0
        val result = List(input.size) { IntArray(input[0].size) }
        for ((lx, rx, ly, ry, color) in solution) {
            for (x in lx until rx) {
                for (y in ly until ry) {
                    result[x][y] = color
                }
            }
        }

        val fixJuryBug = 1
        write(result, testId, "temp")
        File("output", "$testId.out").printWriter().use {
            with(it) {
                for ((lx, rx, ly, ry, color) in solution) {
                    when {
                        lx != 0 && ly != 0 -> {
                            println("cut [${id}] [${ly + lx * fixJuryBug / 40}, ${lx + ly * fixJuryBug / 40}]")
                            println("color [${id}.2] ${color.toRGBA()}")
                            println("merge [${id}.2] [${id}.1]")
                            println("merge [${id}.0] [${id}.3]")
                            println("merge [${id + 1}] [${id + 2}]")
                            id += 3
                        }

                        lx != 0 -> {
                            println("cut [${id}] [y] [$lx]")
                            println("color [${id}.1] ${color.toRGBA()}")
                            println("merge [${id}.0] [${id}.1]")
                            id += 1
                        }

                        ly != 0 -> {
                            println("cut [${id}] [x] [$ly]")
                            println("color [${id}.1] ${color.toRGBA()}")
                            println("merge [${id}.0] [${id}.1]")
                            id += 1
                        }

                        else -> {
                            println("color [${id}] ${color.toRGBA()}")
                        }
                    }
                }
            }
        }
    }
}