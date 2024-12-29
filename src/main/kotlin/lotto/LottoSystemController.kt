package lotto

import lotto.model.Lotto
import lotto.model.LottoMatchStatistic
import lotto.model.Lottos
import lotto.model.WinningNumbers

class LottoSystemController {
    fun buyLottos(purchaseAmountInput: String): Lottos {
        val purchasedLottoCount = Lotto.calculateLottoCount(purchaseAmountInput.convertToInt())
        return Lottos.fromCountInAuto(purchasedLottoCount)
    }

    fun generatelottos(lottoNumbersInput: List<String>): Lottos {
        val lottoNumbers = lottoNumbersInput.map { it.convertToInts() }

        return Lottos.fromLottoNumbers(lottoNumbers)
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
        purchaseAmountInput: String,
    ): Double = lottoMatchStatistic.calculateReturnRate(purchaseAmountInput.convertToInt())

    fun String.convertToInt(): Int = this.toIntOrNull() ?: throw RuntimeException("숫자로 입력하지 않았습니다.")

    fun String.convertToInts(): List<Int> =
        this.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map {
                it.toIntOrNull() ?: throw NumberFormatException("숫자로 입력하지 않았습니다.")
            }
}
