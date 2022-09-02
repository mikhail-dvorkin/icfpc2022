fun main(args: Array<String>) {
	val testCases = 1..10
	for (testCase in testCases) {
		val image = read(testCase)
		write(image, testCase, "0_original")
		raster(image, testCase)
	}
}
