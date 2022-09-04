fun raster(image: List<IntArray>, testCase: Int): Triple<IntArray, IntArray, List<IntArray>> {
	write(image, testCase, "original")
	val xs = xs(image)
	val ys = xs(image.transposed())
	fun median(yFrom: Int, xFrom: Int, yTo: Int, xTo: Int): Int {
		val colors = (yFrom until yTo).flatMap { image[it].slice(xFrom until xTo) }
		return (0..3).sumOf { i ->
			colors.map { it ushr (8 * i) and 255 }.sorted()[colors.size / 2] shl (8 * i)
		}
//		return colors.groupBy { it }.maxByOrNull { it.value.size }!!.key
	}
	val medians = List(ys.size - 1) { i -> IntArray(xs.size - 1) { j -> median(ys[i], xs[j], ys[i + 1], xs[j + 1])} }
	val medianImage = List(ys.last()) { IntArray(xs.last()) }
	for (i in 0..ys.size - 2) for (j in 0..xs.size - 2) {
		for (y in ys[i] until ys[i + 1]) for (x in xs[j] until xs[j + 1]) {
			medianImage[y][x] = medians[i][j]
		}
	}
	write(medianImage, testCase, "medians")
	return Triple(xs.toIntArray(), ys.toIntArray(), medians)
}

fun xs(image: List<IntArray>): List<Int> {
	val inf = 1e99
	val hei = image.size
	val wid = image[0].size
	val magic = rasterCount
	val minGap = rasterMinSide
	val diffs = DoubleArray(wid - 1)
	for (y in 0 until hei) {
		for (x in 1 until wid) {
			diffs[x - 1] += colorDistance(image[y][x - 1], image[y][x])
		}
	}
	val dp = List(wid + 1) { DoubleArray(magic + 1) { -inf } }
	val dpHow = List(wid + 1) { IntArray(magic + 1) { -1 } }
	dp[0][0] = 0.0
	for (x in 0 until wid) {
		for (k in 0 until magic) {
			if (dp[x][k] == -inf) continue
			for (xx in x + minGap..wid) {
				val newCost = dp[x][k] + diffs.getOrElse(x) { 0.0 }
				if (newCost > dp[xx][k + 1]) {
					dp[xx][k + 1] = newCost
					dpHow[xx][k + 1] = x
				}
			}
		}
	}
	fun way(x: Int, k: Int): List<Int> {
		if (k == 0) return listOf(x)
		val xx = dpHow[x][k]
		return way(xx, k - 1) + x
	}
//	println("$minGap ${way(wid, magic)}")
	return way(wid, magic)
}
