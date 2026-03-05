package kg.attractor.java.handler;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.server.Utils;
import java.io.IOException;
import java.util.*;

public class Lesson47 extends Lesson46 {
    private static final Map<String, List<Book>> userHistory = new HashMap<>();

    public Lesson47(String host, int port) throws IOException {
        super(host, port);
        registerGet("/book", this::bookDetailsHandler);
    }


    @Override
    protected void borrowBookHandler(HttpExchange exchange) {
        String email = getAuthEmail(exchange);
        Map<String, String> data = Utils.parseUrlEncoded(getBody(exchange), "&");
        String bookIdStr = data.get("bookId");

        if (email != null && bookIdStr != null) {
            int id = Integer.parseInt(bookIdStr);
            Book book = books.stream()
                    .filter(b -> b.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (book != null) {
                userHistory.computeIfAbsent(email, k -> new ArrayList<>()).add(book);
            }
        }
        super.borrowBookHandler(exchange);
    }

    @Override
    protected void profileGetAuthHandler(HttpExchange exchange) {
        String email = getAuthEmail(exchange);
        if (email == null) { redirect303(exchange, "/login"); return; }

        UserInfo user = users.get(email);
        List<Book> history = userHistory.getOrDefault(email, List.of());

        renderTemplate(exchange, "profile.html", Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "history", history
        ));
    }

    private void bookDetailsHandler(HttpExchange exchange) {
        Map<String, String> params = Utils.parseUrlEncoded(getQueryParams(exchange), "&");
        String idStr = params.get("id");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            Book book = books.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
            if (book != null) {
                renderTemplate(exchange, "book_info.html", Map.of("book", book));
                return;
            }
        }
        redirect303(exchange, "/books");
    }
}