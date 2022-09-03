import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun colorScore(image1: List<IntArray>, image2: List<IntArray>): Int {
	var result = 0.0
	for (y in image1.indices) for (x in image1[y].indices) {
		result += colorDistance(image1[y][x], image2[y][x])
	}
	return result.roundToInt()
}

fun emptyField(hei: Int, wid: Int) = List(hei) { IntArray(wid) }
fun List<IntArray>.transposed() = this[0].indices.map { i -> map { it[i] }.toIntArray() }

const val colorDistanceCoefficient = 0.005

fun Int.toRGBA() = Color(this, true).let {
	listOf(it.red, it.green, it.blue, it.alpha)
}

fun colorDistance(color1: Int, color2: Int): Double {
	val sumSquares = (0..3).sumOf { i ->
		val diff = (color1 ushr (8 * i) and 255) - (color2 ushr (8 * i) and 255)
		diff * diff
	}
	return sqrt(sumSquares.toDouble()) * colorDistanceCoefficient
}

fun read(testCase: Int): List<IntArray> {
	val file = File("input", "$testCase.png")
	val bufferedImage = ImageIO.read(file)
	val q = bufferedImage.getRGB(0, 0)
	val hei = bufferedImage.height
	val wid = bufferedImage.width
	val array = List(hei) { y -> IntArray(wid) { x ->
		val p = bufferedImage.getRGB(x, y)
		require((p ushr 24) == 255)
		p
	}}
	return array.reversed()
}

val picNameMap = mutableMapOf<Int, Int>()

fun write(image: List<IntArray>, testCase: Int, picName: String) {
	picNameMap[testCase] = picNameMap.getOrDefault(testCase, 0) + 1
	val fileName = testCase.toString().padStart(2, '0') + "_" + picNameMap[testCase].toString().padStart(1, '0') + "_" + picName
	val format = "png"
	val picsDir = File("pics").also { it.mkdirs() }
	val file = File(picsDir, "$fileName.$format")
	val hei = image.size
	val wid = image[0].size
	val converted = image.reversed()
	val bufferedImage = BufferedImage(wid, hei, BufferedImage.TYPE_INT_ARGB)
	for (y in 0 until hei) for (x in 0 until wid) bufferedImage.setRGB(x, y, converted[y][x])
	ImageIO.write(bufferedImage, format, file)
}


const val BASE_LINE_CUT = 7
const val BASE_POINT_CUT = 10
const val BASE_MERGE = 1
const val BASE_COLOR = 5
const val BASE_SWAP = 3
