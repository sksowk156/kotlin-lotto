package lotto.model

class WinningNumbers private constructor(
    winnigLottoNumbers: List<LottoNumber>,
    val bonusNumber: LottoNumber,
) {
    val winnigLottoNumbers: List<LottoNumber> = winnigLottoNumbers.toList()
        get() = field.toList()

    init {
        require(winnigLottoNumbers.size == LOTTO_NUMBER_COUNT) {
            "당첨 로또 번호는 정확히 6개여야 합니다."
        }
        require(winnigLottoNumbers.distinct().size == LOTTO_NUMBER_COUNT) {
            "당첨 로또 번호는 중복될 수 없습니다."
        }
        require(!winnigLottoNumbers.contains(bonusNumber)) {
            "보너스 번호는 당첨 로또 번호와 중복될 수 없습니다."
        }
    }

    companion object {
        private const val LOTTO_NUMBER_COUNT = 6

        fun from(
            winningNumbers: List<LottoNumber>,
            bonusNumber: LottoNumber,
        ): WinningNumbers = WinningNumbers(winningNumbers, bonusNumber)

        fun from(
            winningNumbers: List<Int>,
            bonusNumber: Int,
        ): WinningNumbers =
            from(
                winningNumbers.map { number ->
                    LottoNumber(number)
                },
                LottoNumber(bonusNumber),
            )
    }
}
