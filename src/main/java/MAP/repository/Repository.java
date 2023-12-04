package MAP.repository;

import MAP.domain.Entity;
import MAP.domain.Friendship;

import java.util.Optional;

public interface Repository<ID, E extends Entity<ID>> {

    Optional<E> findOne(ID id);

    Iterable<E> getAll();

    Optional<E> save (E entity);

    boolean delete(ID id);

    Optional<E> update(E entity);

}
