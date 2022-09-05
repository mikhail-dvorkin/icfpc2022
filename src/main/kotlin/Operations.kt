import kotlin.math.roundToInt

var BASE_LINE_CUT = -1
var BASE_POINT_CUT = -1
var BASE_MERGE = -1
var BASE_COLOR = -1
var BASE_SWAP = -1

fun opCost(baseCost: Int, blockArea: Int, totalArea: Int) = (baseCost.also { require(it > 0) }.toDouble() * totalArea / blockArea).roundToInt()

private fun opMerge(id1: String, id2: String) = "merge [$id1] [$id2]"
fun opMerge(id1: Int, id2: Int) = opMerge(id1.toString(), id2.toString())
fun opMergeLineCut(id: Int) = opMerge("${id}.0", "${id}.1")
fun opMergePointCut(id: Int, horizontal: Boolean) = if (horizontal xor flipXY()) {
	listOf(opMerge("${id}.2", "${id}.1"), opMerge("${id}.0", "${id}.3"))
} else {
	listOf(opMerge("${id}.2", "${id}.3"), opMerge("${id}.0", "${id}.1"))
}
private fun opColor(id: String, color: Int) = "color [${id}] ${color.toRGBA()}"
fun opColor(id: Int, color: Int) = opColor(id.toString(), color)
fun opColorLineCutTopRight(id: Int, color: Int, horizontal: Boolean): String {
	return opColor("${id}.${if (flipCoord() xor ((horizontal xor flipXY()) and flipY())) 0 else 1}", color)
}
fun opColorPointCutTopRight(id: Int, color: Int): String {
	return opColor("${id}.${(if (flipCoord()) 0 else 2) xor (if (flipY()) 3 else 0)}", color)
}
fun opLineCut(id: Int, horizontal: Boolean, coord: Int, maxCoord: Int): String {
	return "cut [${id}] [${if (horizontal xor flipXY()) "y" else "x"}] [${if (flipCoord() xor ((horizontal xor flipXY()) and flipY())) (maxCoord - coord) else coord}]"
}
fun opPointCut(id: Int, x: Int, y: Int, maxX: Int, maxY: Int): String {
	var (xx, yy) = if (flipXY()) y to x else x to y
	if (flipCoord()) { xx = maxX - xx; yy = maxY - yy }
	if (flipY()) { yy = maxY - yy }
	return "cut [${id}] [${xx}, ${yy}]"
}

fun flipXY() = (rotate % 2 != 0).also { require(!it) }
fun flipCoord() = (rotate % 4) >= 2
fun flipY() = rotate >= 4

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
