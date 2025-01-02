package lotto

import lotto.model.LottoPrize
import lotto.model.Lottos
import lotto.view.InputView
import lotto.view.ResultView

class LottoSystem {
    private val lottoSystemController = LottoSystemController()

    private val inputView = InputView()
    private val resultView = ResultView()

    fun run() {
        val (manualLottoCount, autoLottoCount) = calculateLottoCounts()

        val lottos = generateLottos(manualLottoCount, autoLottoCount)

        evaluateLottoResults(lottos)
    }

    private fun calculateLottoCounts(): Pair<Int, Int> {
        val purchaseAmountInput = inputView.getPurchaseAmountInput()

        val manualLottoCountInput = inputView.getManualLottoCountInput()

        val (manualLottoCount, autoLottoCount) =
            lottoSystemController.calculateLottoDistribution(
                purchaseAmountInput,
                manualLottoCountInput,
            )

        resultView.renderPurchaseLottoCountOutput(
            manualLottoCount = manualLottoCount,
            autoLottoCount = autoLottoCount,
        )

        return (manualLottoCount to autoLottoCount)
    }

    private fun generateLottos(
        manualLottoCount: Int,
        autoLottoCount: Int,
    ): Lottos {
        val manualLottoNumbersInput = inputView.getManualLottoNumberInput(count = manualLottoCount)

        val manualLottos = lottoSystemController.generateLottosInManual(manualLottoNumbersInput)

        val autoLottos = lottoSystemController.generateLottosInAuto(autoLottoCount)

        val lottos = Lottos.from(manualLottos.lottos + autoLottos.lottos)

        lottos.lottos.forEach { lotto ->
            resultView.renderPurchaseLottoNumbersOutput(lotto.numbers.map { it.num })
        }

        return lottos
    }

    private fun evaluateLottoResults(purchasedLottos: Lottos) {
        val winningNumberInput = inputView.getWinningNumberInput()
        val bonusNumberInput = inputView.getBonusNumberInput()

        val matchStatistic =
            lottoSystemController
                .matchLottoNumbers(
                    winningNumberInput,
                    bonusNumberInput,
                    purchasedLottos,
                )
        val rate =
            lottoSystemController
                .calculateReturnRate(
                    matchStatistic,
                    purchasedLottos.calculatePurchaseAmount(),
                )

        resultView.renderResultOutput()

        LottoPrize.getLottoPrizes().forEach {
            val matchCount = matchStatistic.findMatchCount(it)
            resultView.renderLottoMatchResultOutput(
                prizeCount = it.matchCount,
                prizeAmount = it.prizeAmount,
                matchingCount = matchCount,
                hasBonus = it.hasBonus,
            )
        }

        resultView.renderLottoProfit(rate)
    }
}
