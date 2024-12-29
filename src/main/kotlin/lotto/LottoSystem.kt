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

        val (manualLottoCount, autoLottoCount) = calculateLottoDistribution(purchaseAmountInput, manualLottoCountInput)

        resultView.renderPurchaseLottoCountOutput(
            manualLottoCount = manualLottoCount,
            autoLottoCount = autoLottoCount,
        )

        val lottos = generateLottos(manualLottoCount, autoLottoCount)

        lottos.getLottos().forEach { lotto ->
            resultView.renderPurchaseLottoNumbersOutput(lotto.numbers.map { it.num })
        }

        return LottoPurchaseResult(purchaseAmountInput, lottos)
    }

    private fun generateLottos(
        manualLottoCount: Int,
        autoLottoCount: Int,
    ): Lottos {
        val manualLottoNumbersInput = inputView.getManualLottoNumberInput(count = manualLottoCount)

        val manualLottos = generatelottos(manualLottoNumbersInput)

        val autoLottos = List(autoLottoCount) { Lotto.fromAuto() }

        return Lottos.from(manualLottos + autoLottos)
    }

    private fun generatelottos(manualLottoNumbersInput: List<String>): List<Lotto> {
        val manualLottoNumbers =
            manualLottoNumbersInput.map { it.convertToInts() }

        val manualLottos = manualLottoNumbers.map { Lotto.from(it) }
        return manualLottos
    }

    private fun calculateLottoDistribution(
        purchaseAmountInput: String,
        manualLottoCountInput: String,
    ): Pair<Int, Int> {
        val totalLottoCount = Lotto.calculateLottoCount(purchaseAmountInput.convertToInt())
        val manualLottoCount = manualLottoCountInput.convertToInt()
        val autoLottoCount = totalLottoCount - manualLottoCount

        if (autoLottoCount < 0) throw RuntimeException("구매할 수 있는 로또의 개수를 넘었습니다.")
        return Pair(manualLottoCount, autoLottoCount)
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
