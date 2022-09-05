import kotlin.math.roundToInt

var BASE_LINE_CUT = 7
var BASE_POINT_CUT = 10
var BASE_MERGE = 1
var BASE_COLOR = 5
var BASE_SWAP = 3

fun opCost(baseCost: Int, blockArea: Int, totalArea: Int) = (baseCost.toDouble() * totalArea / blockArea).roundToInt()

fun opMerge(id1: String, id2: String) = "merge [$id1] [$id2]"
fun opMerge(id1: Int, id2: Int) = opMerge(id1.toString(), id2.toString())

fun bestMergeCost(x: Int, y:Int, wid: Int, hei: Int) :Int {
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

fun ourMoveCost(x: Int, y: Int, wid: Int, hei: Int): Int {
	val colorCost = opCost(BASE_COLOR, (wid - x) * (hei - y), wid * hei)
	val mergeCost = bestMergeCost(x, y, wid, hei)
	val cutCost = when {
		x != 0 && y != 0 -> BASE_POINT_CUT
		x != 0 || y != 0 -> BASE_LINE_CUT
		else -> 0
	}
	return cutCost + colorCost + mergeCost
}
