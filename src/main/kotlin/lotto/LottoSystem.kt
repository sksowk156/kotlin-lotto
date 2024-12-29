package lotto

import lotto.model.Lotto
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

        val totalLottoCount = Lotto.count(purchaseAmountInput.convertToInt())
        val manualLottoCount = manualLottoCountInput.convertToInt()
        val autoLottoCount = totalLottoCount - manualLottoCount

        if (autoLottoCount < 0) throw RuntimeException("구매할 수 있는 로또의 개수를 넘었습니다.")

        val manualLottoNumbersInput = inputView.getManualLottoNumberInput(count = manualLottoCount)

        val manualLottoNumbers =
            manualLottoNumbersInput?.map { it.convertToInts() } ?: throw RuntimeException("숫자를 입력해야 합니다.")

        val autoLottos = List(autoLottoCount) { Lotto.fromAuto() }

        val manualLottos = manualLottoNumbers.map { Lotto.from(it) }

        resultView.renderPurchaseLottoCountOutput(
            manualLottoCount = manualLottoCount,
            autoLottoCount = autoLottoCount,
        )

        manualLottos.forEach { lotto ->
            resultView.renderPurchaseLottoNumbersOutput(lotto.numbers.map { it.num })
        }

        autoLottos.forEach { lotto ->
            resultView.renderPurchaseLottoNumbersOutput(lotto.numbers.map { it.num })
        }

        val lottos = Lottos.from(autoLottos + manualLottos)
        return LottoPurchaseResult(purchaseAmountInput, lottos)
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
