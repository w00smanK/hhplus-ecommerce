//package kr.hhplus.ecommerce.application.balance;
//
//import kr.hhplus.be.ecommerce.domain.balance.BalanceCommand;
//import kr.hhplus.be.ecommerce.domain.balance.BalanceService;
//import kr.hhplus.be.ecommerce.domain.balance.BalanceTransactionService;
//import kr.hhplus.be.ecommerce.domain.unit.Amount;
//import kr.hhplus.be.ecommerce.domain.user.UserService;
//import kr.hhplus.be.ecommerce.support.MockTestSupport;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InOrder;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.inOrder;
//
//class BalanceFacadeTest extends MockTestSupport {
//
//    @InjectMocks
//    private BalanceFacade balanceFacade;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private BalanceService balanceService;
//
//    @Mock
//    private BalanceTransactionService balanceTransactionService;
//
//    @DisplayName("잔고를 조회한다.")
//    @Test
//    void getBalance() {
//        // given
//        Long userId = 1L;
//
//        // when
//        balanceFacade.getBalance(userId);
//
//        // then
//        InOrder inOrder = inOrder(userService, balanceService);
//        inOrder.verify(userService).getUser(anyLong());
//        inOrder.verify(balanceService).getBalance(any());
//    }
//
//    @DisplayName("잔고를 충전한다.")
//    @Test
//    void chargeBalance() {
//        // given
//        BalanceCommand.Charge command = BalanceCommand.Charge.of(1L, Amount.of(1_000L));
//
//        // when
//        balanceFacade.chargeBalance(command);
//
//        // then
//        InOrder inOrder = inOrder(userService, balanceService, balanceTransactionService);
//        inOrder.verify(userService).getUser(anyLong());
//        inOrder.verify(balanceService).charge(any());
//        inOrder.verify(balanceTransactionService).create(any());
//    }
//}