package lotto.view

class InputView {
    fun getPurchaseAmountInput() = getInput(PURCHASE_AMOUNT) ?: throw RuntimeException("금액을 입력되지 않았습니다.")

    fun getManualLottoCountInput() = getInput(MANUAL_LOTTO_COUNT) ?: throw RuntimeException("수동으로 구매할 로또의 개수가 입력되지 않았습니다.")

    fun getManualLottoNumberInput(count: Int) =
        getInputs(message = MANUAL_LOTTO_NUMBER, count = count) ?: throw RuntimeException("숫자를 입력해야 합니다.")

    fun getWinningNumberInput() = getInput(WINNING_NUMBER) ?: throw RuntimeException("우승 번호가 입력되지 않았습니다.")

    fun getBonusNumberInput() = getInput(BONUS_NUMBER) ?: throw RuntimeException("보너스 번호가 입력되지 않았습니다.")

    private fun getInput(message: String): String? {
        println(message)
        val input = readlnOrNull()?.trim()
        if (input.isNullOrEmpty()) return null
        return input
    }

    private fun getInputs(
        message: String,
        count: Int,
    ): List<String>? {
        println(message)
        val inputs = mutableListOf<String>()
        repeat(count) {
            val input = readlnOrNull()?.trim()
            if (input.isNullOrEmpty()) return null
            inputs.add(input)
        }
        return inputs
    }

    private companion object {
        const val PURCHASE_AMOUNT = "구입금액을 입력해 주세요."
        const val MANUAL_LOTTO_COUNT = "수동으로 구매할 로또 수를 입력해 주세요."
        const val MANUAL_LOTTO_NUMBER = "수동으로 구매할 번호를 입력해 주세요."
        const val WINNING_NUMBER = "지난 주 당첨 번호를 입력해 주세요."
        const val BONUS_NUMBER = "보너스 볼을 입력해 주세요."
    }
}
