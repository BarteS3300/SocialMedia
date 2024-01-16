package MAP.repository.paging;

import org.w3c.dom.Entity;

import java.util.stream.Stream;

public class PageImplementation<T> implements Page<T>{

    private Pageable pageable;

    private Stream<T> content;

    public PageImplementation(Pageable pageable, Stream<T> content) {
        this.pageable = pageable;
        this.content = content;
    }

    @Override
    public Pageable getPageable() {
        return null;
    }

    @Override
    public Pageable nextPageable() {
        return null;
    }

    @Override
    public Stream<T> getContent() {
        return null;
    }
}
