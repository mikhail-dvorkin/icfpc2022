import java.io.File

fun main() {
	var sumScores = 0
	for (testId in 1..35) {
		print("$testId)\t")
		val program = mutableListOf<String>()
		var blockCount = 1
		if (testId > 25) {
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
