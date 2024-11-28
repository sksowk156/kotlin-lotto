package lotto

import org.junit.jupiter.api.DisplayName

class LottoAutoTest {
    @DisplayName(value = "구입 금액은 양수다.")
    fun checkPuchaseAmount() {
    }

    @DisplayName(value = "구매 개수는 1000원 단위로 계산한다.")
    fun countPurchasedLotto() {
    }

    @DisplayName(value = "로또 번호는 각기 다른 숫자로 이루어진 6개의 숫자가 오름차순으로 정렬되어야 한다.")
    fun generateLottoNumbers() {
    }

    @DisplayName(value = "지난 주 당첨 번호는 ','를 기준으로 6개의 숫자를 입력받는다.")
    fun checkWinningNumberInputFormat() {
    }

    @DisplayName(value = "당첨금 계산은 일치하는 번호가 세 개일 때부터다.")
    fun calculatePrizeMoney() {
    }

    @DisplayName(value = "제일 많이 일치하는 경우를 한 번만 계산한다. (6개 일치할 경우 5개는 카운팅하지 않는다.")
    fun verifyLottoNumbers() {
    }

    @DisplayName(value = "수익률은 소수점 둘째 자리까지만 계산한다.")
    fun calculateRate() {
    }
}
