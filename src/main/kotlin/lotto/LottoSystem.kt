package lotto

import lotto.model.LottoPrize
import lotto.model.LottoPurchaseResult
import lotto.model.Lottos
import lotto.view.InputView
import lotto.view.ResultView

class LottoSystem {
    private val lottoSystemController = LottoSystemController()

    private val inputView = InputView()
    private val resultView = ResultView()

    fun start() {
        val purchaseResult = purchaseLottos()

        processLottoMatch(purchaseResult)
    }

    private fun purchaseLottos(): LottoPurchaseResult {
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

        val lottos = createLottos(manualLottoCount, autoLottoCount)

        lottos.getLottos().forEach { lotto ->
            resultView.renderPurchaseLottoNumbersOutput(lotto.numbers.map { it.num })
        }

        return LottoPurchaseResult(purchaseAmountInput, lottos)
    }

    private fun createLottos(
        manualLottoCount: Int,
        autoLottoCount: Int,
    ): Lottos {
        val manualLottoNumbersInput = inputView.getManualLottoNumberInput(count = manualLottoCount)

        val manualLottos = lottoSystemController.createLottosInManual(manualLottoNumbersInput)

        val autoLottos = lottoSystemController.createLottosInAuto(autoLottoCount)

        return Lottos.from(manualLottos.getLottos() + autoLottos.getLottos())
    }

    private fun processLottoMatch(purchaseResult: LottoPurchaseResult) {
        val winningNumberInput = inputView.getWinningNumberInput()
        val bonusNumberInput = inputView.getBonusNumberInput()

        val matchResults =
            lottoSystemController
                .matchLottoNumbers(
                    winningNumberInput,
                    bonusNumberInput,
                    purchaseResult.lottos,
                )
        val rate =
            lottoSystemController
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

    fun String.convertToInt(): Int = this.toIntOrNull() ?: throw RuntimeException("숫자로 입력하지 않았습니다.")

    fun String.convertToInts(): List<Int> =
        this.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map {
                it.toIntOrNull() ?: throw NumberFormatException("숫자로 입력하지 않았습니다.")
            }
}
