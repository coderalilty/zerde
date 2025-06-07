package kidd.house.zerde.service;

import kidd.house.zerde.repo.ParentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParentService {
    @Autowired
    private ParentRepo parentRepo;
    public String getChatId(Long parentId) {
        return parentRepo.findByChatIdWhereParentId(parentId).orElse(null);
    }
}
