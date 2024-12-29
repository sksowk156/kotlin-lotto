package lotto.model

class Lottos private constructor(private val lottos: List<Lotto>) {
    fun getLottos() = lottos

    fun calculatePurchaseAmount(): Int = lottos.size * Lotto.PRICE

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
        private const val BONUS_MATCH_COUNT = 5

        fun fromCountInAuto(count: Int): Lottos = from(List(count) { Lotto.fromAuto() })

        fun fromLottoNumbers(lottoNumbers: List<List<Int>>): Lottos = from(lottoNumbers.map { Lotto.from(it) })

        fun from(lottos: List<Lotto>): Lottos = Lottos(lottos)
    }
}
