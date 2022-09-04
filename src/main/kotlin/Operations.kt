import kotlin.math.roundToInt

const val BASE_LINE_CUT = 7
const val BASE_POINT_CUT = 10
const val BASE_MERGE = 1
const val BASE_COLOR = 5
const val BASE_SWAP = 3

fun opCost(baseCost: Int, blockArea: Int, totalArea: Int) = (baseCost.toDouble() * totalArea / blockArea).roundToInt()

fun opMerge(id1: String, id2: String) = "merge [$id1] [$id2]"
fun opMerge(id1: Int, id2: Int) = opMerge(id1.toString(), id2.toString())
