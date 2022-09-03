import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

fun solveSwaps(testCase: Int) {
	print("$testCase) ")
	val image = read(testCase)
	val hei = image.size
	val wid = image[0].size
	val imageOriginal = read(testCase, true)
	val picture = readJson(testCase)
	require(picture.width == wid)
	require(picture.height == hei)
	val blocks = picture.blocks
	val e = List(blocks.size) { i -> DoubleArray(blocks.size) { j ->
		var score = 0.0
		for (y in blocks[j].bottomLeft[1] until blocks[j].topRight[1]) for (x in blocks[j].bottomLeft[0] until blocks[j].topRight[0]) {
			score += colorDistance(blocks[i].color(), image[y][x])
			val c1 = blocks[i].color()
			val c2 = imageOriginal[blocks[i].bottomLeft[1] + 2][blocks[i].bottomLeft[0] + 2]
			require(c1 == c2) { "${c1.toRGBA()} ${c2.toRGBA()} $i" }
		}
		score
	} }
	println(e[0].contentToString())
	print(colorScore(image, imageOriginal))
	print(" == ")
	println(e.indices.map { e[it][it] }.sum())
	println(e.indices.map { e[it][it] }.sum() / blocks.size)
	val area = (blocks[0].topRight[0] - blocks[0].bottomLeft[0]) * (blocks[0].topRight[1] - blocks[0].bottomLeft[1])
	println(area)
	println((BASE_SWAP.toDouble() * hei * wid / area).roundToInt())
	println((BASE_COLOR.toDouble() * hei * wid / area).roundToInt())
}

@OptIn(ExperimentalSerializationApi::class)
fun readJson(testCase: Int) = Json.decodeFromStream<Picture>(File("input", "$testCase.initial.json").inputStream())

@Serializable
data class Block(val blockId: Int, val bottomLeft: List<Int>, val topRight: List<Int>, val color: List<Int>)
private fun Block.color() = color.toColor()
@Serializable
data class Picture(val width: Int, val height: Int, val blocks: List<Block>)

fun solveStub(image: List<IntArray>, testCase: Int) {
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
