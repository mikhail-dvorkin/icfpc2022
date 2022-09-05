import java.io.File

fun main() {
	var sumScores = 0
	for (testId in 1..40) {
		print("$testId)\t")
		if (testId >= 36) {
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
