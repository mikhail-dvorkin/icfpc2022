import java.io.File

fun main() {
	for (a in listOf(8, 9, 40, 60, 16, 10, 20, 30)) {
		val b = 300 / a
		for (c in listOf(3, 9, 16, 32)) {
			for (d in listOf(0, 2, 4, 6)) {
				rasterCount = a
				rasterMinSide = b
				joinColors = c
				rotate = d
				println("== ${settingsLabel()} ==")
				runAll()
			}
		}
	}
}

fun runAll() {
	var sumScores = 0
	for (testId in 1..40) {
		print("$testId)\t")
		if (testId <= 35) {
			BASE_LINE_CUT = 7
			BASE_POINT_CUT = 10
			BASE_MERGE = 1
			BASE_COLOR = 5
			BASE_SWAP = 3
		} else {
			BASE_LINE_CUT = 2
			BASE_POINT_CUT = 3
			BASE_MERGE = 1
			BASE_COLOR = 5
			BASE_SWAP = 3
		}
		val program = mutableListOf<String>()
		var blockCount = 1
		if (testId in 26..35) {
			program.addAll(mergeToOne(testId))
			blockCount = program.last().drop(2).toInt()
		}
		program.addAll(runYoungSolver(testId, blockCount))
		sumScores += solved(program, testId)
	}
	println("sum: \t$sumScores")
	val bestScores = File("output", "best.txt").readLines().map { it.split("_")[1].toInt() }
	println("best:\t${bestScores.sum()}")
}
