package lotto

import lotto.model.Lotto
import lotto.model.LottoMatchStatistic
import lotto.model.Lottos
import lotto.model.WinningNumbers

class LottoAutoController {
    fun buyLottos(purchaseAmountInput: String): Lottos {
        val purchasedLottoCount = countPurchasedLotto(purchaseAmountInput)
        return generateLottos(purchasedLottoCount)
    }

    private fun countPurchasedLotto(purchaseAmountInput: String): Int {
        val purchaseAmount = purchaseAmountInput.convertToInt()
        val lottoCount = Lotto.count(purchaseAmount)
        return lottoCount
    }

    private fun generateLottos(lottoCount: Int): Lottos = Lottos.fromCountInAuto(lottoCount)

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
        purchaseAmountInput: String,
    ): Double = lottoMatchStatistic.calculateReturnRate(purchaseAmountInput.convertToInt())

    private fun String.convertToInt(): Int = this.toIntOrNull() ?: throw RuntimeException("숫자로 입력하지 않았습니다.")

    private fun String.convertToInts(): List<Int> =
        this.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map {
                it.toIntOrNull() ?: throw NumberFormatException("숫자로 입력하지 않았습니다.")
            }
}
