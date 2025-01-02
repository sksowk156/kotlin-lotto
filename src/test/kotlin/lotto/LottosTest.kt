package lotto

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import lotto.model.LottoMatchResult
import lotto.model.LottoMatchStatistic
import lotto.model.LottoPrize
import lotto.model.Lottos
import lotto.model.WinningNumbers
import org.junit.jupiter.api.Test

class LottosTest {
    @Test
    fun `당첨 번호와 비교하여 계산한다`() {
        val winningLotto = WinningNumbers.from(listOf(1, 2, 3, 4, 5, 6), 7)

        val lotto1 = listOf(1, 2, 3, 4, 5, 6) // 6개 일치
        val lotto2 = listOf(1, 2, 3, 4, 5, 7) // 5개 일치, 보너스 일치
        val lotto2bonus = listOf(1, 2, 3, 4, 5, 8) // 5개 일치
        val lotto3 = listOf(1, 2, 3, 4, 8, 9) // 4개 일치
        val lotto4 = listOf(1, 2, 3, 10, 11, 12) // 3개 일치
        val lotto5 = listOf(1, 2, 13, 14, 15, 16) // 2개 일치

        val lottos = Lottos.from(0, listOf(lotto1, lotto2, lotto2bonus, lotto3, lotto4, lotto5))

        val expectedResults =
            LottoMatchStatistic.from(
                listOf(
                    LottoMatchResult(matchPrize = LottoPrize.THREE, count = 1),
                    LottoMatchResult(matchPrize = LottoPrize.FOUR, count = 1),
                    LottoMatchResult(matchPrize = LottoPrize.FIVE, count = 1),
                    LottoMatchResult(matchPrize = LottoPrize.FIVE_BONUS, count = 1),
                    LottoMatchResult(matchPrize = LottoPrize.SIX, count = 1),
                ),
            )

        lottos.countMatchingLottoNumbers(winningLotto).totalPrizeAmount shouldBe expectedResults.totalPrizeAmount
    }

    @Test
    fun `from 메소드는 올바른 자동 및 수동 로또 개수로 Lottos를 생성해야 한다`() {
        val autoCount = 3
        val manualNumbers =
            listOf(
                listOf(1, 2, 3, 4, 5, 6),
                listOf(7, 8, 9, 10, 11, 12),
            )

        val lottos = Lottos.from(autoCount, manualNumbers)

        lottos.lottos.size shouldBe autoCount + manualNumbers.size
    }

    @Test
    fun `calculatePurchaseAmount 메소드는 올바른 금액을 반환해야 한다`() {
        val autoCount = 2
        val manualNumbers =
            listOf(
                listOf(5, 12, 23, 34, 45, 1),
            )
        val lottos = Lottos.from(autoCount, manualNumbers)
        val expectedAmount = (autoCount + manualNumbers.size) * 1000

        lottos.calculatePurchaseAmount() shouldBe expectedAmount
    }

    @Test
    fun `from 메소드는 autoCount가 음수일 때 예외를 던져야 한다`() {
        val exception =
            shouldThrow<IllegalArgumentException> {
                Lottos.from(autoCount = -1, manualLottoNumbers = emptyList())
            }
        exception.message shouldBe "로또 개수는 양수여야 합니다."
    }

    @Test
    fun `calculateAutoLottoCount 메소드는 수동 로또 개수가 총 로또 개수를 초과할 때 예외를 던져야 한다`() {
        val purchaseAmount = 2000
        val manualLottoCount = 3

        val exception =
            shouldThrow<IllegalArgumentException> {
                Lottos.calculateAutoLottoCount(purchaseAmount, manualLottoCount)
            }
        exception.message shouldBe "구매할 수 있는 로또의 개수를 넘었습니다."
    }
}
