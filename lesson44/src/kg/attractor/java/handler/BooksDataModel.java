package kg.attractor.java.handler;

import java.util.ArrayList;
import java.util.List;

public class BooksDataModel {
    private List<Book> books = new ArrayList<>();

    public BooksDataModel() {
        String icon = "images/coding-book.png";

        books.add(new Book(1, "Java: The Complete Reference", "Herbert Schildt", icon, "AVAILABLE",
                "Классическая энциклопедия по Java. Содержит исчерпывающую информацию по синтаксису и основным библиотекам."));

        books.add(new Book(2, "First Code", "Robert Martin", icon, "AVAILABLE",
                "Легендарная книга 'Чистый код'. Учит писать читаемый, поддерживаемый и надежный код на реальных примерах."));

        books.add(new Book(3, "Effective Java", "Joshua Bloch", icon, "AVAILABLE",
                "Сборник лучших практик и паттернов программирования на Java от создателя многих API платформы."));

        books.add(new Book(4, "Head First Java", "Kathy Sierra", icon, "AVAILABLE",
                "Идеальное введение в Java для новичков. Уникальная визуальная подача материала помогает легко усваивать сложные концепции."));

        books.add(new Book(5, "Design Patterns", "Erich Gamma", icon, "AVAILABLE",
                "Библия шаблонов проектирования от 'Банды четырех' (GoF). Обязательно к прочтению для уверенных разработчиков."));

        books.add(new Book(6, "The Pragmatic Programmer", "Andrew Hunt", icon, "AVAILABLE",
                "Сборник советов по повышению продуктивности, профессиональному росту и правильному мышлению разработчика."));
    }

    public List<Book> getBooks() {
        return books;
    }
}