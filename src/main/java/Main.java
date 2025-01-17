import controller.NotificationServiceImpl;
import controller.ShortLinkGeneratorImpl;
import controller.UserServiceImpl;
import entity.Link;
import repository.LinkRepository;
import repository.impl.LinkRepositoryImpl;
import service.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private final LinkRepository linkRepository;
    private final UserService userService;
    private final ShortLinkGenerator shortLinkGenerator;
    private final NotificationService notificationService;


    public Main(LinkRepository linkRepository, UserService userService, ShortLinkGenerator shortLinkGenerator, NotificationService notificationService) {
        this.linkRepository = linkRepository;
        this.userService = userService;
        this.shortLinkGenerator = shortLinkGenerator;
        this.notificationService = notificationService;
    }

    public static void main(String[] args) {
        // Инициализация сервисов
        LinkRepository linkRepository = new LinkRepositoryImpl();
        UserService userService = new UserServiceImpl();
        ShortLinkGenerator shortLinkGenerator = new ShortLinkGeneratorImpl();
        NotificationService notificationService = new NotificationServiceImpl();
        Main main = new Main(linkRepository, userService, shortLinkGenerator, notificationService);
        Scanner scanner = new Scanner(System.in);
        cleanExpiredLinks(linkRepository, notificationService);

        while (true) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Создать короткую ссылку");
            System.out.println("2. Перейти по короткой ссылке");
            System.out.println("3. Удалить свою ссылку");
            System.out.println("4. Редактировать свою ссылку");
            System.out.println("5. Выйти");
            System.out.print("Введите номер действия: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    // 1. Создание ссылки
                    System.out.print("Введите длинную ссылку: ");
                    String longUrl = scanner.nextLine();
                    System.out.print("Введите UUID пользователя (оставьте пустым для генерации нового): ");
                    String headerUUID = scanner.nextLine().trim();
                    UUID userUUID = main.userService.getUserUUID(headerUUID);
                    String shortLink = main.shortLinkGenerator.generateShortLink(longUrl, userUUID);
                    System.out.print("Введите лимит переходов: ");
                    int clicksLeft = Integer.parseInt(scanner.nextLine());

                    Link link = Link.builder()
                            .id(UUID.randomUUID())
                            .longlink(longUrl)
                            .shortlink(shortLink)
                            .userUUID(userUUID)
                            .clicksleft(clicksLeft)
                            .expirationDate(LocalDateTime.now().plusMinutes(Link.DEFAULT_EXPIRATION_MINUTES))
                            .active(true)
                            .build();
                    main.linkRepository.save(link);
                    System.out.println("Создана короткая ссылка: " + shortLink + ", для пользователя: " + userUUID);
                    break;
                case "2":
                    // 2. Перейти по короткой ссылке
                    System.out.print("Введите короткую ссылку для перехода: ");
                    String shortLinkForRedirect = scanner.nextLine();
                    Optional<Link> optionalLink = main.linkRepository.getByShortLink(shortLinkForRedirect);
                    if (optionalLink.isPresent()) {
                        Link linkToFollow = optionalLink.get();
                        System.out.println("Вы перешли по ссылке: " + linkToFollow.getShortlink());
                        linkToFollow.setClicksleft(linkToFollow.getClicksleft() - 1);
                        if (linkToFollow.getClicksleft() <= 0) {
                            linkToFollow.setActive(false);
                            main.notificationService.sendNotification("Лимит переходов исчерпан", linkToFollow.getUserUUID(), linkToFollow.getShortlink());
                        }
                        if (linkToFollow.getExpirationDate().isBefore(LocalDateTime.now())) {
                            linkToFollow.setActive(false);
                            main.notificationService.sendNotification("Срок действия ссылки истек", linkToFollow.getUserUUID(), linkToFollow.getShortlink());
                        }
                        if (linkToFollow.isActive()) {
                            System.out.println("Вы перенаправлены на: " + linkToFollow.getLonglink());
                        } else {
                            System.out.println("Ссылка недействительна.");
                        }
                        main.linkRepository.save(linkToFollow);
                    } else {
                        System.out.println("Ссылка не найдена.");
                    }
                    break;
                case "3":
                    // 3. Удалить ссылку
                    System.out.print("Введите UUID пользователя: ");
                    String headerUUIDForDelete = scanner.nextLine().trim();
                    UUID userUUIDForDelete = main.userService.getUserUUID(headerUUIDForDelete);
                    System.out.print("Введите короткую ссылку для удаления: ");
                    String shortLinkForDelete = scanner.nextLine();
                    Optional<Link> optionalLinkForDelete = main.linkRepository.getByShortLink(shortLinkForDelete);
                    if (optionalLinkForDelete.isPresent()) {
                        Link linkToDelete = optionalLinkForDelete.get();
                        if (linkToDelete.getUserUUID().equals(userUUIDForDelete)) {
                            System.out.println("Ссылка " + linkToDelete.getShortlink() + " будет удалена");
                            main.linkRepository.delete(linkToDelete.getId());
                            System.out.println("Ссылка удалена");
                        } else {
                            System.out.println("Вы не являетесь владельцем этой ссылки.");
                        }
                    } else {
                        System.out.println("Ссылка не найдена.");
                    }
                    break;
                case "4":
                    // 4. Редактировать ссылку
                    System.out.print("Введите UUID пользователя: ");
                    String headerUUIDForUpdate = scanner.nextLine().trim();
                    UUID userUUIDForUpdate = main.userService.getUserUUID(headerUUIDForUpdate);
                    System.out.print("Введите короткую ссылку для редактирования: ");
                    String shortLinkForUpdate = scanner.nextLine();
                    Optional<Link> optionalLinkForUpdate = main.linkRepository.getByShortLink(shortLinkForUpdate);
                    if (optionalLinkForUpdate.isPresent()) {
                        Link linkToUpdate = optionalLinkForUpdate.get();
                        if (linkToUpdate.getUserUUID().equals(userUUIDForUpdate)) {
                            System.out.print("Введите новый лимит переходов: ");
                            int newClicksLeft = Integer.parseInt(scanner.nextLine());
                            System.out.print("Введите новый срок действия(в минутах): ");
                            int newExpirationMinutes = Integer.parseInt(scanner.nextLine());
                            linkToUpdate.setClicksleft(newClicksLeft);
                            linkToUpdate.setExpirationDate(LocalDateTime.now().plusMinutes(newExpirationMinutes));
                            main.linkRepository.update(linkToUpdate);
                            System.out.println("Ссылка обновлена.");
                        } else {
                            System.out.println("Вы не являетесь владельцем этой ссылки.");
                        }
                    } else {
                        System.out.println("Ссылка не найдена.");
                    }
                    break;
                case "5":
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный ввод, попробуйте снова.");
            }
        }
    }

    private static void cleanExpiredLinks(LinkRepository linkRepository, NotificationService notificationService) {
        List<Link> allLinks = linkRepository.getAll();
        for (Link link : allLinks) {
            if (link.getExpirationDate().isBefore(LocalDateTime.now())) {
                notificationService.sendNotification("Срок действия ссылки истек", link.getUserUUID(), link.getShortlink());
                linkRepository.delete(link.getId());
                System.out.println("Ссылка " + link.getShortlink() + " удалена, по причине истечения срока действия");
            }
        }
    }
}