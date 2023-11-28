package MAP.repository;

import MAP.domain.Entity;
import MAP.domain.Friendship;
import MAP.validators.Validator;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E>{

    private Validator<E> validator;

    private ArrayList<Friendship> friendships = new ArrayList<>();

    Map<ID, E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        this.entities = new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id){
        if(id == null)
            throw new IllegalArgumentException("Id must not be null");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> getAll(){
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity){
        if(entity == null)
            throw new IllegalArgumentException("Entity must not be null");
        validator.validate(entity);
        if(entities.putIfAbsent(entity.getId(), entity) == null){
            return Optional.empty();
        }
        else{
            return Optional.of(entity);
        }
    }

    public Optional<E> delete(ID id){
        if (id == null)
            throw new IllegalArgumentException("id must not be null");
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity){
        if(entity == null)
            throw new IllegalArgumentException("Entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null){
            entities.put(entity.getId(), entity);
            return Optional.empty();
            }
        return Optional.of(entity);
    }

    public void addFriendship(Friendship f){
        friendships.add(f);
    }

    public void removeFriendship(Friendship f){
        friendships.remove(f);
    }

    public ArrayList<Friendship> friendships(){
        return friendships;
    }


}
