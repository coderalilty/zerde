package kidd.house.zerde.service;

import kidd.house.zerde.repo.ParentRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
class ParentServiceTest {
    @InjectMocks
    private ParentService parentService;
    @Mock
    private ParentRepo parentRepo;

    @Test
    void getChatId() {
        parentService.getChatId(1L);
        Mockito.verify(parentRepo, Mockito.times(1)).findByChatIdWhereParentId(1L);
    }
}