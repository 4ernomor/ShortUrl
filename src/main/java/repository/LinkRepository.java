package repository;

import entity.Link;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkRepository {
    Optional<Link> save(Link link);
    Optional<Link> get(UUID id);
    Optional<Link> getByShortLink(String shortlink);
    List<Link> getAll();
    void delete(UUID id);
    Optional<Link> update(Link link);
}
