import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.sqrt

fun colorScore(image1: List<IntArray>, image2: List<IntArray>): Double {
	var result = 0.0
	for (y in image1.indices) for (x in image1[y].indices) {
		result += colorDistance(image1[y][x], image2[y][x])
	}
	return result
}

fun emptyField(hei: Int, wid: Int) = List(hei) { IntArray(wid) }

const val colorDistanceCoefficient = 0.05

fun colorDistance(color1: Int, color2: Int): Double {
	val sumSquares = (0..3).sumOf { i ->
		val diff = (color1 shr (8 * i) and 255) - (color2 shr (8 * i) and 255)
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
		assert((p shr 24) == 255)
		p
	}}
	return array
}

val picNameMap = mutableMapOf<Int, Int>()

fun write(image: List<IntArray>, testCase: Int, picName: String = "") {
	val fileName = picName.ifEmpty {
		picNameMap[testCase] = picNameMap.getOrDefault(testCase, 0) + 1
		picNameMap[testCase]
	}
	val format = "png"
	val picsDir = File("pics").also { it.mkdirs() }
	val file = File(picsDir, "${testCase}_${fileName}.$format")
	val hei = image.size
	val wid = image[0].size
	val bufferedImage = BufferedImage(wid, hei, BufferedImage.TYPE_INT_ARGB)
	for (y in 0 until hei) for (x in 0 until wid) bufferedImage.setRGB(x, y, image[y][x])
	ImageIO.write(bufferedImage, format, file)
}
