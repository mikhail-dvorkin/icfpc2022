import kotlin.math.roundToInt

class InnerRectangle(val lx: Int, val ly: Int, val color:Int, val cost: Int)
data class Rectangle(val lx: Int, val rx: Int, val ly: Int, val ry: Int, val color: Int)

val Double.mergeCost: Int get() = (BASE_MERGE / this).roundToInt()

fun youngSolver(xs: IntArray, ys: IntArray, colorIds: List<IntArray>): Pair<Int, List<Rectangle>> {
	val hei = ys.last()
	val wid = xs.last()

	fun bestMergeCost(x: Int, y:Int) :Int {
		if ((x == 0 || wid == x) && (y == 0 || hei == y)) return 0
		if (x == 0 || wid == x) return maxOf(y * 1.0 / hei, (hei - y) * 1.0 / hei).mergeCost
		if (y == 0 || hei == y) return maxOf(x * 1.0 / wid, (wid - x) * 1.0 / wid).mergeCost
		//    x
		//   0|1
		// y ---
		//   3|2
		val p0 = x * y * 1.0 / wid / hei
		val p1 = (wid - x) * y * 1.0 / wid / hei
		val p2 = (wid - x) * (hei - y) * 1.0 / wid / hei
		val p3 = x * (hei - y) * 1.0 / wid / hei
		return minOf(
			maxOf(p0, p1).mergeCost + maxOf(p3, p2).mergeCost + maxOf(p0 + p1, p3 + p2).mergeCost,
			maxOf(p0, p3).mergeCost + maxOf(p1, p2).mergeCost + maxOf(p0 + p3, p1 + p2).mergeCost,
		)
	}

	fun ourMoveCost(x: Int, y: Int) {

	}

	val cache = mutableMapOf<String, Int>()
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
				val mergeCost = bestMergeCost(xs[i], ys[a[i]])
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
	val moves = buildList {
		while (!cur.all { it == c }) {
			val (next, move) = moves(cur).first { (nxt, rect) -> go(nxt) + rect.cost == go(cur) }
			add(Rectangle(xs[move.lx], xs.last(), ys[move.ly], ys.last(), move.color))
			cur = next
		}
	}
	return score to moves
}

fun runYoungSolver(testId: Int, blockCount: Int): List<String> {
	/*
	val client = HttpClient {
		install(Auth) {
			bearer { BearerTokens("token", "") }
		}
	}
	 */
	val input = read(testId)
	/*
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
	 */

//		val solution = YoungSolver().solve(xs, ys, data)
	val (ys, xs, data) = raster(input, testId)
	val (scoreMoves, solution) = youngSolver(xs, ys, data)
	var id = blockCount - 1

	val result = List(input.size) { IntArray(input[0].size) }
	for ((lx, rx, ly, ry, color) in solution) {
		for (x in lx until rx) {
			for (y in ly until ry) {
				result[x][y] = color
			}
		}
	}
//	write(result, testId, "temp")
	val scoreColors = colorScore(result, input)
	val scoreTotal = scoreMoves + scoreColors
	println("$scoreMoves\t+\t$scoreColors\t= $scoreTotal")
	val program = mutableListOf<String>()
	for ((lx, rx, ly, ry, color) in solution) {
		when {
			lx != 0 && ly != 0 -> {
				program.add("cut [${id}] [${ly}, ${lx}]")
				val p0 = lx * ly * 1.0 / rx / ry
				val p1 = (rx - lx) * ry * 1.0 / rx / ry
				val p3 = lx * (ry - ly) * 1.0 / rx / ry
				val p2 = (rx - lx) * (ry - ly) * 1.0 / rx / ry

				val s1 =
					maxOf(p0, p1).mergeCost + maxOf(p2, p3).mergeCost + maxOf(p0 + p1, p2 + p3).mergeCost
				val s2 =
					maxOf(p0, p3).mergeCost + maxOf(p1, p2).mergeCost + maxOf(p0 + p3, p1 + p2).mergeCost
				program.add("color [${id}.2] ${color.toRGBA()}")
				if (s1 < s2) {
					program.add("merge [${id}.2] [${id}.1]")
					program.add("merge [${id}.0] [${id}.3]")
				} else {
					program.add("merge [${id}.2] [${id}.3]")
					program.add("merge [${id}.0] [${id}.1]")
				}
				program.add("merge [${id + 1}] [${id + 2}]")
				id += 3
			}

			lx != 0 -> {
				program.add("cut [${id}] [y] [$lx]")
				program.add("color [${id}.1] ${color.toRGBA()}")
				program.add("merge [${id}.0] [${id}.1]")
				id += 1
			}

			ly != 0 -> {
				program.add("cut [${id}] [x] [$ly]")
				program.add("color [${id}.1] ${color.toRGBA()}")
				program.add("merge [${id}.0] [${id}.1]")
				id += 1
			}

			else -> {
				program.add("color [${id}] ${color.toRGBA()}")
			}
		}
	}
	program.add("#$$scoreTotal")
	return program
}
