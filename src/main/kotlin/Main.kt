fun main() {
	for (testId in 1..35) {
		print("$testId) ")
		val program = mutableListOf<String>()
		var blockCount = 1
		if (testId > 25) {
			program.addAll(solveSwaps(testId))
			blockCount = program.last().drop(2).toInt()
		}
		program.addAll(runYoungSolver(testId, blockCount))
		solved(program, testId)
	}
}
