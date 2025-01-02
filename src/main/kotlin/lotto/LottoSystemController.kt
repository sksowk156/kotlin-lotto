package lotto

import lotto.model.LottoMatchStatistic
import lotto.model.Lottos
import lotto.model.WinningNumbers

class LottoSystemController {
    fun generateLottos(
        lottosCount: Int,
        lottoNumbersInput: List<String>,
    ): Lottos {
        val lottoNumbers = lottoNumbersInput.map { it.convertToInts() }

        return Lottos.from(
            autoCount = lottosCount,
            manualLottoNumbers = lottoNumbers,
        )
    }

    fun calculateLottoDistribution(
        purchaseAmountInput: String,
        manualLottoCountInput: String,
    ): Pair<Int, Int> {
        val purchaseAmount = purchaseAmountInput.convertToInt()
        val manualLottoCount = manualLottoCountInput.convertToInt()

        val autoLottoCount = Lottos.calculateAutoLottoCount(purchaseAmount, manualLottoCount)
        return manualLottoCount to autoLottoCount
    }

    fun matchLottoNumbers(
        winningNumberInput: String,
        bonusNumberInput: String,
        lottos: Lottos,
    ): LottoMatchStatistic {
        val winningNumbers = winningNumberInput.convertToInts()
        val bonusNumber = bonusNumberInput.convertToInt()
        return lottos.countMatchingLottoNumbers(
            WinningNumbers.from(winningNumbers, bonusNumber),
        )
    }

    fun calculateReturnRate(
        lottoMatchStatistic: LottoMatchStatistic,
        purchaseAmount: Int,
    ): Double = lottoMatchStatistic.calculateReturnRate(purchaseAmount)

    private fun String.convertToInt(): Int = this.toIntOrNull() ?: throw NumberFormatException("숫자로 입력하지 않았습니다.")

    private fun String.convertToInts(): List<Int> =
        this.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map {
                it.toIntOrNull() ?: throw NumberFormatException("숫자로 입력하지 않았습니다.")
            }
}
