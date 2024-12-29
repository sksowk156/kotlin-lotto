package lotto

import lotto.model.LottoPrize
import lotto.model.LottoPurchaseResult
import lotto.view.InputView
import lotto.view.ResultView

class LottoAuto {
    private val lottoAutoController = LottoAutoController()

    private val inputView = InputView()
    private val resultView = ResultView()

    fun start() {
        val purchaseResult = purchaseLottos()

        processLottoMatch(purchaseResult)
    }

    private fun purchaseLottos(): LottoPurchaseResult {
        val purchaseAmountInput = inputView.getPurchaseAmountInput()
        val lottos = lottoAutoController.buyLottos(purchaseAmountInput)
        resultView.renderPurchaseLottoCountOutput(lottos.getLottos().size)
        lottos.getLottos().forEach { lotto ->
            resultView.renderPurchaseLottoNumbersOutput(lotto.numbers.map { it.num })
        }
        return LottoPurchaseResult(purchaseAmountInput, lottos)
    }

    private fun processLottoMatch(purchaseResult: LottoPurchaseResult) {
        val winningNumberInput = inputView.getWinningNumberInput()
        val bonusNumberInput = inputView.getBonusNumberInput()

        val matchResults =
            lottoAutoController
                .matchLottoNumbers(
                    winningNumberInput,
                    bonusNumberInput,
                    purchaseResult.lottos,
                )
        val rate =
            lottoAutoController
                .calculateReturnRate(
                    matchResults,
                    purchaseResult.purchaseAmount,
                )

        resultView.renderResultOutput()
        for (lottoPrize in LottoPrize.getLottoPrizes()) {
            val matchCount = matchResults.findMatchCount(lottoPrize)
            resultView.renderLottoMatchResultOutput(
                lottoPrize.matchCount,
                lottoPrize.prizeAmount,
                matchCount,
                lottoPrize.hasBonus,
            )
        }
        resultView.renderLottoProfit(rate)
    }
}
