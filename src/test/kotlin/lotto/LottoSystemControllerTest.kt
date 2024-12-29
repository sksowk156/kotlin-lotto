package lotto

import lotto.model.Lotto
import lotto.model.LottoMatchResult
import lotto.model.LottoMatchStatistic
import lotto.model.LottoPrize
import lotto.model.Lottos
import lotto.model.WinningNumbers
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class LottoSystemControllerTest {
    private lateinit var lottoSystemController: LottoSystemController

    @BeforeEach
    fun setUp() {
        lottoSystemController = LottoSystemController()
    }

    @DisplayName("구입 금액은 양수여야 한다.")
    @ParameterizedTest(name = "{index} => input: ''{0}''")
    @CsvSource(
        "1000, 1",
        "2000, 2",
        "5000, 5",
        "10000, 10",
    )
    fun checkPurchaseAmount1(
        input: String,
        expectedCount: Int,
    ) {
        val lottos = lottoSystemController.buyLottos(input)
        assertThat(lottos.getLottos().size).isEqualTo(expectedCount)
    }

    @DisplayName("구입 금액이 0이거나 음수면 예외가 발생한다.")
    @ParameterizedTest(name = "{index} => input: ''{0}''")
    @ValueSource(strings = ["0", "-1000"])
    fun checkPurchaseAmount2(input: String) {
        assertThatThrownBy { lottoSystemController.buyLottos(input) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("금액은 양수입니다.")
    }

    @DisplayName("구입 금액이 숫자가 아니면 예외가 발생한다.")
    @ParameterizedTest(name = "{index} => input: ''{0}''")
    @ValueSource(strings = ["abc", "-1dd"])
    fun checkPurchaseAmount3(input: String) {
        assertThatThrownBy { lottoSystemController.buyLottos(input) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("숫자로 입력하지 않았습니다.")
    }

    @DisplayName(value = "구매 개수는 1000원 단위로 계산한다.")
    @ParameterizedTest(name = "{index} => input: ''{0}'' expected: ''{1}''")
    @CsvSource(
        "1600, 1",
        "29100, 29",
        "11111, 11",
    )
    fun countPurchasedLotto(
        input: String,
        expected: Int,
    ) {
        val lottos = lottoSystemController.buyLottos(input)
        assertThat(lottos.getLottos().size).isEqualTo(expected)
    }

    @DisplayName(value = "로또 번호는 각기 다른 숫자로 이루어진 6개의 숫자가 오름차순으로 정렬되어야 한다.")
    @Test
    fun generateLottoNumbers() {
        val lotto = lottoSystemController.buyLottos("1000").getLottos().first()

        assertThat(lotto.numbers.size).isEqualTo(6)
        assertThat(lotto.numbers.map { it.num }).isSorted
    }

    @DisplayName("지난 주 당첨 번호는 6개의 숫자로 이루어져 있지 않다면 예외가 발생한다.")
    @ParameterizedTest(name = "{index} => winningNumbersInput=''{0}'', purchasedLottoCount=''{1}''")
    @ValueSource(
        strings = [
            "1,2,3,4,5,6,7",
            "10,20,30,40,41,42,1,2,3",
            "7,14,21,28,35",
        ],
    )
    fun validWinningNumbers(winningNumbersInput: String) {
        assertThatThrownBy {
            lottoSystemController.matchLottoNumbers(
                winningNumbersInput,
                "8",
                Lottos.fromCountInAuto(1),
            )
        }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("당첨 로또 번호는 정확히 6개여야 합니다.")
    }

    @Test
    @DisplayName("당첨금 계산은 일치하는 번호가 3개일 때부터다.")
    fun testMatchLottoNumbers() {
        val input = listOf(1, 2, 3, 4, 5, 6) to 7
        val lottos =
            Lottos.from(
                listOf(
                    // 3개 일치
                    Lotto.from(listOf(1, 2, 3, 7, 8, 9)),
                    // 3개 일치
                    Lotto.from(listOf(4, 5, 6, 10, 11, 12)),
                    // 2개 일치
                    Lotto.from(listOf(1, 2, 7, 8, 9, 10)),
                    // 0개 일치
                    Lotto.from(listOf(13, 14, 15, 16, 17, 18)),
                ),
            )

        val matchResult = lottos.countMatchingLottoNumbers(WinningNumbers.from(input.first, input.second))

        assertEquals(2, matchResult.findMatchCount(LottoPrize.THREE), "3개 일치하는 로또는 2개여야 합니다.")
    }

    @Test
    @DisplayName("수익률은 소수점 둘째 자리까지만 계산한다.")
    fun calculateRate() {
        val purchaseAmount = 3000 // 구매 금액
        val prizeAmount =
            listOf(LottoMatchResult(LottoPrize.THREE, 1), LottoMatchResult(LottoPrize.FOUR, 1), LottoMatchResult(LottoPrize.FIVE, 1))

        val returnRate = LottoMatchStatistic.from(prizeAmount).calculateReturnRate(purchaseAmount)

        assertEquals(68.33, returnRate, "수익률은 소수점 둘째 자리까지 계산되어야 합니다.")
    }

    @DisplayName("유효한 로또 번호 입력 시 Lottos가 올바르게 생성된다.")
    @Test
    fun `createLottosInManual with valid input`() {
        val lottoNumbersInput = listOf("1,2,3,4,5,6", "7,8,9,10,11,12")

        val lottos = lottoSystemController.generateLottosInManual(lottoNumbersInput)

        assertThat(lottos.getLottos()).hasSize(2)
        assertThat(lottos.getLottos().map { it.numbers.map { num -> num.num } })
            .containsExactly(
                listOf(1, 2, 3, 4, 5, 6),
                listOf(7, 8, 9, 10, 11, 12),
            )
    }

    @DisplayName("로또 번호에 중복이 있는 경우 예외가 발생한다.")
    @ParameterizedTest(name = "{index} => input: ''{0}''")
    @ValueSource(
        strings = [
            "1,1,3,4,5,6", // 중복 번호
            "1,2,3,4,5", // 번호 개수 부족
            "1,2,3,4,5,6,7", // 번호 개수 초과
        ],
    )
    fun `createLottosInManual with invalid input1`(input: String) {
        val lottoNumbersInput = listOf(input)

        assertThatThrownBy { lottoSystemController.generateLottosInManual(lottoNumbersInput) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("중복되지 않는 로또 번호 6개를 입력해주세요")
    }

    @DisplayName("로또 번호가 범위를 벗어난 경우 예외가 발생한다.")
    @ParameterizedTest(name = "{index} => input: ''{0}''")
    @ValueSource(
        strings = [
            "1,2,3,4,5,46", // 번호 범위 초과 (예: 45를 초과)
        ],
    )
    fun `createLottosInManual with invalid input2`(input: String) {
        val lottoNumbersInput = listOf(input)

        assertThatThrownBy { lottoSystemController.generateLottosInManual(lottoNumbersInput) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("유효하지 않은 로또 번호 입니다.")
    }

    @DisplayName("유효한 로또 개수 입력 시 Lottos가 올바르게 생성된다.")
    @ParameterizedTest(name = "{index} => count: {0}")
    @ValueSource(ints = [1, 5, 10])
    fun `createLottosInAuto with valid count`(count: Int) {
        val lottos = lottoSystemController.generateLottosInAuto(count)

        assertThat(lottos.getLottos()).hasSize(count)
        lottos.getLottos().forEach { lotto ->
            assertThat(lotto.numbers).hasSize(6)
            assertThat(lotto.numbers.map { it.num }).allMatch { it in 1..45 }
        }
    }

    @DisplayName("로또 개수가 0 이하일 때 예외가 발생한다.")
    @ParameterizedTest(name = "{index} => count: {0}")
    @ValueSource(ints = [0, -1, -5])
    fun `createLottosInAuto with invalid count`(count: Int) {
        assertThatThrownBy { lottoSystemController.generateLottosInAuto(count) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("로또 개수는 양수여야 합니다.")
    }

    @DisplayName("수동 로또 개수가 전체 로또 개수를 초과할 때 예외가 발생한다.")
    @ParameterizedTest(name = "{index} => purchaseAmount: {0}, manualCount: {1}")
    @CsvSource(
        "1000, 2",
        "2000, 3",
        "5000, 6",
        "10000, 11",
    )
    fun `calculateAutoLottoCount with manualCount exceeding total`(
        purchaseAmountInput: String,
        manualLottoCountInput: String,
    ) {
        assertThatThrownBy {
            lottoSystemController.calculateAutoLottoCount(
                purchaseAmountInput,
                manualLottoCountInput,
            )
        }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessageContaining("구매할 수 있는 로또의 개수를 넘었습니다.")
    }

    @DisplayName("구매 금액이나 수동 로또 개수가 숫자가 아닐 때 예외가 발생한다.")
    @ParameterizedTest(name = "{index} => purchaseAmount: {0}, manualCount: {1}")
    @CsvSource(
        "abc, 1",
        "1000, xyz",
        "one thousand, two",
    )
    fun `calculateAutoLottoCount with non-numeric inputs`(
        purchaseAmountInput: String,
        manualLottoCountInput: String,
    ) {
        assertThatThrownBy {
            lottoSystemController.calculateAutoLottoCount(
                purchaseAmountInput,
                manualLottoCountInput,
            )
        }
            .isInstanceOf(NumberFormatException::class.java)
            .hasMessageContaining("숫자로 입력하지 않았습니다.")
    }
}
