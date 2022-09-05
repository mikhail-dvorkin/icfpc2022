import kotlin.math.roundToInt

class InnerRectangle(val lx: Int, val ly: Int, val cost: Int)
//data class Rectangle(val lx: Int, val rx: Int, val ly: Int, val ry: Int, val color: Int)

val Double.mergeCost: Int get() = (BASE_MERGE / this).roundToInt()

fun youngSolver(xs: IntArray, ys: IntArray, colorIds: List<IntArray>): Pair<Int, List<Triple<Int, Int, Int>>> {
	val hei = ys.last()
	val wid = xs.last()


	val cache = mutableMapOf<String, Int>()
	require(colorIds.size + 1 == xs.size)
	require(colorIds.all { it.size + 1 == ys.size })
	val r = xs.size - 1
	val c = ys.size - 1
	fun moves(a: IntArray) = buildList {
		require(a.size == r)
		for (i in a.indices) {
			if (a[i] < (a.getOrNull(i - 1) ?: c)) {
				require(i < r)
				require(a[i] < c)
				val cost = ourMoveCost(xs[i], ys[a[i]], wid, hei)
				val na = a.clone().apply { set(i, a[i] + 1) }
				add(na to InnerRectangle(i, a[i], cost))
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
			add(move)
			cur = next
		}
	}
	val theMoves = mutableListOf<Triple<Int, Int, Int>>()
	val field = List(ys.size) { IntArray(xs.size) }
	var theScore = 0
	for (move in moves) {
		val c = colorIds[move.lx][move.ly]
		val colorDist = colorDistance(field[move.ly][move.lx], c)
		if (colorDist == 0.0) continue
//		val colorCost = colorDist * (xs[move.lx + 1] - xs[move.lx]) * (ys[move.ly + 1] - ys[move.ly])
		val colorCost = colorDist * (wid - xs[move.lx]) * (hei - ys[move.ly])
		val ourMoveCost = ourMoveCost(xs[move.lx], ys[move.ly], wid, hei)
//		if (colorCost < ourMoveCost) continue
		for (y in move.ly until ys.size) for (x in move.lx until xs.size) field[y][x] = c
		theScore += ourMoveCost
		theMoves.add(Triple(xs[move.lx], ys[move.ly], c))
	}
	return theScore to theMoves
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
	val wid = xs.last()
	val hei = ys.last()

	val result = List(input.size) { IntArray(input[0].size) }
	for ((lx, ly, color) in solution) {
		for (x in lx until wid) {
			for (y in ly until hei) {
				result[x][y] = color
			}
		}
	}
	write(result, testId, "result")
	val scoreColors = colorScore(result, input)
	val scoreTotal = scoreMoves + scoreColors
	println("$scoreMoves\t+\t$scoreColors\t= $scoreTotal")
	val program = mutableListOf<String>()
	for ((lx, ly, color) in solution) {
		when {
			lx != 0 && ly != 0 -> {
				program.add(opPointCut(id, ly, lx, hei, wid))
				val p0 = lx * ly * 1.0 / wid / hei
				val p1 = (wid - lx) * hei * 1.0 / wid / hei
				val p3 = lx * (hei - ly) * 1.0 / wid / hei
				val p2 = (wid - lx) * (hei - ly) * 1.0 / wid / hei

				val s1 =
					maxOf(p0, p1).mergeCost + maxOf(p2, p3).mergeCost + maxOf(p0 + p1, p2 + p3).mergeCost
				val s2 =
					maxOf(p0, p3).mergeCost + maxOf(p1, p2).mergeCost + maxOf(p0 + p3, p1 + p2).mergeCost
				program.add(opColorPointCutTopRight(id, color))
				program.addAll(opMergePointCut(id, s1 < s2))
				program.add(opMerge(id + 1, id + 2))
				id += 3
			}

			lx != 0 -> {
				program.add(opLineCut(id, true, lx, wid))
				program.add(opColorLineCutTopRight(id, color, true))
				program.add(opMergeLineCut(id))
				id += 1
			}

			ly != 0 -> {
				program.add(opLineCut(id, false, ly, hei))
				program.add(opColorLineCutTopRight(id, color, false))
				program.add(opMergeLineCut(id))
				id += 1
			}

			else -> {
				program.add(opColor(id, color))
			}
		}
	}
	program.add("#$$scoreTotal")
	return program
}
