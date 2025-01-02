package lotto.view

class ResultView {
    fun renderPurchaseLottoCountOutput(
        manualLottoCount: Int,
        autoLottoCount: Int,
    ) = render(
        """
        $PURCHASE_MANUAL ${manualLottoCount}장, $PURCHASE_AUTO $autoLottoCount$PURCHASE_LOTTO_COUNT,
        """.trimIndent(),
    )

    fun renderPurchaseLottoNumbersOutput(lottoNumbers: List<Int>) = render(lottoNumbers.toString())

    fun renderResultOutput() = render(RESULT)

    fun renderLottoMatchResultOutput(
        prizeCount: Int,
        prizeAmount: Int,
        matchingCount: Int,
        hasBonus: Boolean,
    ) {
        render(
            "${prizeCount}개 일치${
                if (hasBonus) {
                    ", 보너스 볼 일치"
                } else {
                    " "
                }
            }(${prizeAmount}원)- ${matchingCount}개",
        )
    }

    fun renderLottoProfit(rate: Double) {
        render("총 수익률은 ${rate}입니다.")
    }

    private fun render(message: String) {
        println(message)
    }

    private companion object {
        const val PURCHASE_MANUAL = "수동으로"
        const val PURCHASE_AUTO = "자동으로"
        const val PURCHASE_LOTTO_COUNT = "개를 구매했습니다."
        const val RESULT = "당첨 통계\n" + "--------"
    }
}
