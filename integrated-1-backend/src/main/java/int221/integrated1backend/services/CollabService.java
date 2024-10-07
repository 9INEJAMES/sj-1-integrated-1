package int221.integrated1backend.services;

import int221.integrated1backend.entities.in.Board;
import int221.integrated1backend.entities.in.Collab;
import int221.integrated1backend.repositories.in.CollabRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CollabService {
    @Autowired
    private CollabRepository repository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional("firstTransactionManager")
    public Collab createNewCollab(Collab collab) {
        return repository.save(collab);
    }
}
