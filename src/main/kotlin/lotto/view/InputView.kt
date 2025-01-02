package lotto.view

class InputView {
    fun getPurchaseAmountInput() = getInput(PURCHASE_AMOUNT)

    fun getManualLottoCountInput() = getInput(MANUAL_LOTTO_COUNT)

    fun getManualLottoNumberInput(count: Int) = getInputs(message = MANUAL_LOTTO_NUMBER, count = count)

    fun getWinningNumberInput() = getInput(WINNING_NUMBER)

    fun getBonusNumberInput() = getInput(BONUS_NUMBER)

    private fun getInput(message: String): String {
        println(message)
        val input = readlnOrNull()?.trim()
        require(!input.isNullOrEmpty()) { "값을 입력해 주세요ㅣ." }
        return input
    }

    private fun getInputs(
        message: String,
        count: Int,
    ): List<String> {
        println(message)
        val inputs = mutableListOf<String>()
        repeat(count) {
            val input = readlnOrNull()?.trim()
            require(!input.isNullOrEmpty()) { "값을 입력해 주세요ㅣ." }
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
