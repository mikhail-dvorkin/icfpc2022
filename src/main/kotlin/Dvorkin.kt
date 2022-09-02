fun solve(image: List<IntArray>, testCase: Int) {
	val hei = image.size
	val wid = image[0].size
	val field = emptyField(hei, wid)
	val colorScore = colorScore(field, image)
	data class Rectangle(val y1: Int, val x1: Int, val y2: Int, val x2: Int)
	val blockAlive = mutableListOf(true)
	val blockIds = mutableListOf("0")
	val blocks = mutableListOf(Rectangle(0, 0, hei, wid))

	data class Pixel(val y: Int, val x: Int, val color: Int)
	val imagePixels = mutableListOf<Pixel>()
	for (y in 0 until hei) for (x in 0 until wid) imagePixels.add(Pixel(y, x, image[y][x]))
	val groups = imagePixels.groupBy { it.color }
	groups.forEach { t, u ->
		if (u.size > 1000) println("$t ${u.size}")
	}
	println(colorScore / hei / wid)
}
