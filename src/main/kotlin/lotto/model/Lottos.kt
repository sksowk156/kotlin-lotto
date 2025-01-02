package lotto.model

class Lottos private constructor(
    private val autoLottos: List<Lotto>,
    private val manualLottos: List<Lotto>,
) {
    val lottos: List<Lotto> = (autoLottos + manualLottos).toList()
        get() = field.toList()

    fun calculatePurchaseAmount(): Int = lottos.size * PRICE

    fun countMatchingLottoNumbers(winningNumbers: WinningNumbers): LottoMatchStatistic {
        val winningNumberList = winningNumbers.winnigLottoNumbers
        val bonusNumber = winningNumbers.bonusNumber

        val lottoMatchResults =
            lottos
                .map { lotto ->
                    val matchCount = lotto.countMatchingNumbers(winningNumberList)
                    val hasBonus = (matchCount == BONUS_MATCH_COUNT) && lotto.numbers.contains(bonusNumber)
                    LottoPrize.fromMatchCount(matchCount, hasBonus)
                }
                .filter(::isWinningPrize) // 당첨되지 않은 로또는 제외
                .groupingBy { it }
                .eachCount()
                .map { (prize, count) ->
                    LottoMatchResult(matchPrize = prize, count = count)
                }

        return LottoMatchStatistic.from(lottoMatchResults)
    }

    private fun isWinningPrize(it: LottoPrize) = it != LottoPrize.NONE

    companion object {
        private const val PRICE = 1000

        private const val BONUS_MATCH_COUNT = 5

        fun from(
            autoCount: Int = 0,
            manualLottoNumbers: List<List<Int>> = emptyList(),
        ): Lottos =
            Lottos(
                autoLottos = generateLottosInAuto(autoCount),
                manualLottos = generateLottosInManual(manualLottoNumbers),
            )

        private fun generateLottosInAuto(autoCount: Int): List<Lotto> {
            require(autoCount >= 0) { "로또 개수는 양수여야 합니다." }
            return List(autoCount) { Lotto.fromAuto() }
        }

        private fun generateLottosInManual(lottoNumbers: List<List<Int>>): List<Lotto> = lottoNumbers.map { Lotto.from(it) }

        fun calculateAutoLottoCount(
            purchaseAmount: Int,
            manualLottoCount: Int,
        ): Int {
            val totalLottoCount = calculateLottoCount(purchaseAmount)
            val autoLottoCount = totalLottoCount - manualLottoCount

            require(autoLottoCount >= 0) { "구매할 수 있는 로또의 개수를 넘었습니다." }
            return autoLottoCount
        }

        private fun calculateLottoCount(purchaseAmount: Int): Int {
            require(purchaseAmount > 0) { "금액은 양수입니다." }
            return purchaseAmount / PRICE
        }
    }
}
