import io.ktor.client.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import java.io.File
import kotlin.math.roundToInt

class InnerRectangle(val lx: Int, val ly: Int, val color:Int, val cost: Int)
data class Rectangle(val lx: Int, val rx: Int, val ly: Int, val ry: Int, val color: Int)

val Double.mergeCost: Int get() = (BASE_MERGE / this).roundToInt()

fun bestMergeCost(x: Int, xl:Int, y:Int, yl:Int) :Int {
    if ((x == 0 || xl == x) && (y == 0 || yl == y)) return 0
    if (x == 0 || xl == x) return maxOf(y * 1.0 / yl, (yl - y) * 1.0 / yl).mergeCost
    if (y == 0 || yl == y) return maxOf(x * 1.0 / xl, (xl - x) * 1.0 / xl).mergeCost
    //    x
    //   0|1
    // y ---
    //   3|2
    val p0 = x * y * 1.0 / xl / yl
    val p1 = (xl - x) * y * 1.0 / xl / yl
    val p2 = (xl - x) * (yl - y) * 1.0 / xl / yl
    val p3 = x * (yl - y) * 1.0 / xl / yl


    return minOf(
        maxOf(p0, p1).mergeCost + maxOf(p3, p2).mergeCost + maxOf(p0 + p1, p3 + p2).mergeCost,
        maxOf(p0, p3).mergeCost + maxOf(p1, p2).mergeCost + maxOf(p0 + p3, p1 + p2).mergeCost,
    )
}

class YoungSolver {
    val cache = mutableMapOf<String, Int>()

    fun solve(xs: IntArray, ys: IntArray, colorIds: List<IntArray>): Pair<Int, List<Rectangle>> {
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
                    val cutCost = when {
                        i != 0 && a[i] != 0 -> BASE_POINT_CUT
                        i != 0 || a[i] != 0 -> BASE_LINE_CUT
                        else -> 0
                    }
                    val colorCost = (BASE_COLOR.toDouble() * gridSize / (xs.last() - xs[i]) / (ys.last() - ys[a[i]])).roundToInt()
                    val mergeCost = bestMergeCost(xs[i], xs.last(), ys[a[i]], ys.last())
                    val cost = cutCost + colorCost + mergeCost



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
        var cur = IntArray(r) { 0 }
        return score to buildList {
            while (!cur.all { it == c }) {
                val (next, move) = moves(cur).first { (nxt, rect) -> go(nxt) + rect.cost == go(cur) }
                add(Rectangle(xs[move.lx], xs.last(), ys[move.ly], ys.last(), move.color))
                cur = next
            }
        }
    }
}

fun runYoungSolver() {
    val client = HttpClient {
        install(Auth) {
            bearer { BearerTokens("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pa2hhaWwuZHZvcmtpbkBnbWFpbC5jb20iLCJleHAiOjE2NjIyMTQwODEsIm9yaWdfaWF0IjoxNjYyMTI3NjgxfQ.euR3KZDg7mnXoe6R-Cj9JYBwfN7bzgEZsSU0r1NQ8vo", "") }
        }
    }

    for (testId in 1 .. 25) {
        print("$testId) ")
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

//        val solution = YoungSolver().solve(xs, ys, data)
        val (ys1, xs1, data1) = raster(input, testId)
        val (scoreMoves, solution) = YoungSolver().solve(xs1, ys1, data1)
        var id = 0
        val result = List(input.size) { IntArray(input[0].size) }
        for ((lx, rx, ly, ry, color) in solution) {
            for (x in lx until rx) {
                for (y in ly until ry) {
                    result[x][y] = color
                }
            }
        }
        val scoreColors = colorScore(result, input)
        val scoreTotal = scoreMoves + scoreColors
        println("$scoreMoves\t+\t$scoreColors\t= $scoreTotal")
        write(result, testId, "temp")
        val outputDir = File("output").also { it.mkdirs() }
        val fileName = testId.toString().padStart(2, '0') + "_" + scoreTotal.toString().padStart(5, '0') + "_" + settingsLabel() + ".txt"
        File(outputDir, fileName).printWriter().use {
            with(it) {
                for ((lx, rx, ly, ry, color) in solution) {
                    when {
                        lx != 0 && ly != 0 -> {
                            println("cut [${id}] [${ly}, ${lx}]")
                            val p0 = lx * ly * 1.0 / rx / ry
                            val p1 = (rx - lx) * ry * 1.0 / rx / ry
                            val p3 = lx * (ry - ly) * 1.0 / rx / ry
                            val p2 = (rx - lx) * (ry - ly) * 1.0 / rx / ry

                            val s1 = maxOf(p0, p1).mergeCost + maxOf(p2, p3).mergeCost + maxOf(p0 + p1, p2 + p3).mergeCost
                            val s2 = maxOf(p0, p3).mergeCost + maxOf(p1, p2).mergeCost + maxOf(p0 + p3, p1 + p2).mergeCost
                            println("color [${id}.2] ${color.toRGBA()}")
                            if (s1 < s2) {
                                println("merge [${id}.2] [${id}.1]")
                                println("merge [${id}.0] [${id}.3]")
                            } else {
                                println("merge [${id}.2] [${id}.3]")
                                println("merge [${id}.0] [${id}.1]")
                            }
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
