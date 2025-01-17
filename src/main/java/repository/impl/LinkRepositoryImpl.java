package repository.impl;

import entity.Link;
import lombok.Builder;
import lombok.Data;
import repository.LinkRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class LinkRepositoryImpl implements LinkRepository {

    private final Map<UUID, Link> links = new HashMap<>();
    private final Map<String, UUID> shortLinkIndex = new HashMap<>();
    @Override
    public Optional<Link> save(Link link) {
        cleanExpiredLinks();
        links.put(link.getId(), link);
        shortLinkIndex.put(link.getShortlink(),link.getId());
        return Optional.of(link);
    }
    @Override
    public Optional<Link> get(UUID id) {
        cleanExpiredLinks();
        return Optional.ofNullable(links.get(id));
    }

    @Override
    public Optional<Link> getByShortLink(String shortlink) {
        cleanExpiredLinks();
        UUID id = shortLinkIndex.get(shortlink);
        if (id != null){
            return Optional.ofNullable(links.get(id));
        }
        return Optional.empty();
    }
    @Override
    public List<Link> getAll() {
        return links.values().stream().collect(Collectors.toList());
    }
    @Override
    public void delete(UUID id) {
        cleanExpiredLinks();
        links.remove(id);
        shortLinkIndex.entrySet().removeIf(entry -> id.equals(entry.getValue()));
    }
    @Override
    public Optional<Link> update(Link link) {
        cleanExpiredLinks();
        links.put(link.getId(),link);
        shortLinkIndex.put(link.getShortlink(),link.getId());
        return Optional.of(link);
    }

    private void cleanExpiredLinks(){
        links.entrySet().removeIf(entry -> {
            if(entry.getValue().getExpirationDate().isBefore(LocalDateTime.now())){
                System.out.println("Ссылка " + entry.getValue().getShortlink() + " удалена, по причине истечения срока действия");
                shortLinkIndex.entrySet().removeIf(shortEntry -> entry.getKey().equals(shortEntry.getValue()));
                return true;
            }
            return false;
        });
    }
}

