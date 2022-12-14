import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun mergeToOne(testCase: Int): List<String> {
	val image = read(testCase)
	val hei = image.size
	val wid = image[0].size
	val totalArea = hei * wid
//	val imageOriginal = read(testCase, true)
	val picture = readJson(testCase)
	require(picture.width == wid)
	require(picture.height == hei)
	val blocks = picture.blocks
	val blockArea = blocks[0].size()
	/*
	val e = List(blocks.size) { i ->
		require(blocks[i].width() == blocks[0].width())
		require(blocks[i].height() == blocks[0].height())
		DoubleArray(blocks.size) { j ->
			var score = 0.0
			for (y in blocks[j].bottomLeft[1] until blocks[j].topRight[1]) for (x in blocks[j].bottomLeft[0] until blocks[j].topRight[0]) {
				score += colorDistance(blocks[i].color(), image[y][x])
				val c1 = blocks[i].color()
				val c2 = imageOriginal[blocks[i].bottomLeft[1] + 2][blocks[i].bottomLeft[0] + 2]
				require(c1 == c2) { "${c1.toRGBA()} ${c2.toRGBA()} $i" }
			}
			score
		}
	}
	print("colorScore per block: " + e.indices.map { e[it][it] }.sum() / blocks.size)
	val area = (blocks[0].topRight[0] - blocks[0].bottomLeft[0]) * (blocks[0].topRight[1] - blocks[0].bottomLeft[1])
	print("\tswap per block: " + opCost(BASE_SWAP, blockArea, totalArea))
	print("\tall swaps: " + opCost(BASE_SWAP, blockArea, totalArea) * blocks.size)
	print("\tcolor per block: " + (BASE_COLOR.toDouble() * hei * wid / area).roundToInt())
	print("\tmerge per block: " + opCost(BASE_MERGE, blockArea, totalArea))
	 */
	val m = sqrt(blocks.size.toDouble()).roundToInt()
	var totalMergeCost = 0
	for (i in 1 until m) {
		totalMergeCost += opCost(BASE_MERGE, blockArea * i, totalArea) * m
		totalMergeCost += opCost(BASE_MERGE, blockArea * i * m, totalArea)
	}
	//print("\ttotalMerge: $totalMergeCost\t")
	val program = mutableListOf<String>()
	var blockCount = blocks.size
	val rowBlocks = IntArray(m)
	for (i in 0 until m) {
		program.add(opMerge(i * m, i * m + 1))
		blockCount++
		for (j in 2 until m) {
			program.add(opMerge(blockCount - 1, i * m + j))
			blockCount++
		}
		rowBlocks[i] = blockCount - 1
	}
	for (i in m - 2 downTo 0) {
		program.add(opMerge(blockCount - 1, rowBlocks[i]))
		blockCount++
	}
	program.add("#$$totalMergeCost")
	program.add("#@$blockCount")
	return program
}

@OptIn(ExperimentalSerializationApi::class)
fun readJson(testCase: Int) = Json.decodeFromStream<Picture>(File("input", "$testCase.initial.json").inputStream())

@Serializable
data class Block(val blockId: Int, val bottomLeft: List<Int>, val topRight: List<Int>, val color: List<Int>)
private fun Block.color() = color.toColor()
private fun Block.width() = topRight[0] - bottomLeft[0]
private fun Block.height() = topRight[1] - bottomLeft[1]
private fun Block.size() = width() * height()
@Serializable
data class Picture(val width: Int, val height: Int, val blocks: List<Block>)
