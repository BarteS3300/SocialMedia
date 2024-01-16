package MAP.repository.paging;

import MAP.domain.Entity;
import MAP.repository.Repository;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
        Page<E> findAll(Pageable pageable);
}
