fun raster(image: List<IntArray>, testCase: Int): Triple<IntArray, IntArray, List<IntArray>> {
	write(image, testCase, "original")
	val hei = image.size
	val wid = image[0].size
	val xs = xs(image)
	val ys = xs(image.transposed())
	fun median(yFrom: Int, xFrom: Int, yTo: Int, xTo: Int): Int {
		val colors = (yFrom until yTo).flatMap { image[it].slice(xFrom until xTo) }
		return medianColor(colors)
//		return colors.groupBy { it }.maxByOrNull { it.value.size }!!.key
	}
	val medians = List(ys.size - 1) { i -> IntArray(xs.size - 1) { j -> median(ys[i], xs[j], ys[i + 1], xs[j + 1])} }
	val medianImage = List(ys.last()) { IntArray(xs.last()) }
	fun doMedianImage(name: String) {
		for (i in 0..ys.size - 2) for (j in 0..xs.size - 2) {
			for (y in ys[i] until ys[i + 1]) for (x in xs[j] until xs[j + 1]) {
				medianImage[y][x] = medians[i][j]
			}
		}
		write(medianImage, testCase, name)
	}
	doMedianImage("medians")

	val dfsed = List(ys.size - 1) { BooleanArray(xs.size - 1) }
	val were = List(ys.size - 1) { IntArray(xs.size - 1) }
	var wereTime = 0
	var componentSize = 0
	val ourMoveCostBase = ourMoveCost(wid / 2, hei / 2, wid, hei)
	fun dfs(y: Int, x: Int) {
		were[y][x] = wereTime
		componentSize++
		for (d in 0 until 4) {
			val yy = y + DY[d]; val xx = x + DX[d]
			if (xx !in 0..xs.size - 2 || yy !in 0..ys.size - 2) continue
			if (dfsed[yy][xx] || were[yy][xx] == wereTime) continue
			val ourMoveCost = maxOf(1.0, ourMoveCost(xs[maxOf(x, xx)], ys[maxOf(y, yy)], wid, hei).toDouble() / ourMoveCostBase)
			val colorDistance = colorDistance(medians[y][x], medians[yy][xx]) / ourMoveCost
			if (colorDistance > joinColors * colorDistanceCoefficient) continue
			dfs(yy, xx)
		}
	}
	while (true) {
		var bestSize = -1
		var bestY = -1
		var bestX = -1
		for (initY in 0..ys.size - 2) for (initX in 0..xs.size - 2) {
			if (dfsed[initY][initX]) continue
			wereTime++
			componentSize = 0
			dfs(initY, initX)
			if (componentSize > bestSize) {
				bestSize = componentSize
				bestY = initY
				bestX = initX
			}
		}
		if (bestSize == -1) break
		wereTime++
		dfs(bestY, bestX)
		val colors = mutableListOf<Int>()
		for (y in 0..ys.size - 2) for (x in 0..xs.size - 2) {
			if (were[y][x] != wereTime) continue
			dfsed[y][x] = true
			colors.addAll((ys[y] until ys[y + 1]).flatMap { image[it].slice(xs[x] until xs[x + 1]) })
		}
		val medianColor = medianColor(colors)
		for (y in 0..ys.size - 2) for (x in 0..xs.size - 2) {
			if (were[y][x] != wereTime) continue
			medians[y][x] = medianColor
		}
	}
//	doMedianImage("dfsed")
	return Triple(xs.toIntArray(), ys.toIntArray(), medians)
}

fun xs(image: List<IntArray>): List<Int> {
	val inf = 1e99
	val hei = image.size
	val wid = image[0].size
	val magic = rasterCount
	val minGap = rasterMinSide
	val diffs = DoubleArray(wid)
	for (x in 1 until wid) {
		for (y in 0 until hei) {
			diffs[x] += colorDistance(image[y][x - 1], image[y][x])
		}
//		diffs[x] /= ourMoveCost(x, hei / 2, wid, hei).toDouble()
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
