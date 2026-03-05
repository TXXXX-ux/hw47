package kg.attractor.java.handler;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.server.Cookie;
import kg.attractor.java.server.Utils;

import java.io.IOException;
import java.util.*;

public abstract class Lesson46 extends Lesson45 {

    private static final Map<String, String> sessions = new HashMap<>();
    protected static final List<Book> books = new BooksDataModel().getBooks();
    private static final Map<String, Integer> userBooksCount = new HashMap<>();
    private static final Map<String, String> borrowedBooks = new HashMap<>();

    private static final String SESSION_COOKIE_NAME = "session_id";

    public Lesson46(String host, int port) throws IOException {
        super(host, port);

        registerPost("/login", this::loginPostAuthHandler);
        registerGet("/profile", this::profileGetAuthHandler);
        registerGet("/books", this::booksGetAuthHandler);
        registerGet("/logout", this::logoutHandler);
        registerPost("/books/return", this::returnBookHandler);
        registerGet("/books/borrow", exchange -> redirect303(exchange, "/books"));
    }

    private void borrowSuccessPageHandler(HttpExchange exchange) {
        redirect303(exchange, "/books");
    }

    private String getCookiesStr(HttpExchange exchange) {
        return exchange.getRequestHeaders().getOrDefault("Cookie", List.of("")).get(0);
    }

    private void setCookie(HttpExchange exchange, Cookie<?> cookie) {
        exchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
    }

    protected String getAuthEmail(HttpExchange exchange) {
        String cookiesStr = getCookiesStr(exchange);
        Map<String, String> cookies = Cookie.parse(cookiesStr);
        String sessionId = cookies.get(SESSION_COOKIE_NAME);

        if (sessionId == null) {
            return null;
        }
        return sessions.get(sessionId);
    }


    private void loginPostAuthHandler(HttpExchange exchange) {
        Map<String, String> data = Utils.parseUrlEncoded(getBody(exchange), "&");
        String email = data.get("email");
        String password = data.get("password");

        UserInfo user = users.get(email);

        if (user != null && user.getPassword().equals(password)) {
            String sessionId = UUID.randomUUID().toString();

            sessions.put(sessionId, email);

            Cookie<String> cookie = Cookie.make(SESSION_COOKIE_NAME, sessionId);
            cookie.setMaxAge(3600);
            cookie.setHttpOnly(true);

            setCookie(exchange, cookie);
            redirect303(exchange, "/books");
        } else {
            renderTemplate(exchange, "login.html", Map.of("error", "Неверный логин или пароль"));
        }
    }

    private void logoutHandler(HttpExchange exchange) {
        String cookiesStr = getCookiesStr(exchange);
        Map<String, String> cookies = Cookie.parse(cookiesStr);
        String sessionId = cookies.get(SESSION_COOKIE_NAME);

        if (sessionId != null) {
            sessions.remove(sessionId);
        }

        Cookie<String> cookie = Cookie.make(SESSION_COOKIE_NAME, "");
        cookie.setMaxAge(0);
        setCookie(exchange, cookie);

        redirect303(exchange, "/login");
    }

    protected void profileGetAuthHandler(HttpExchange exchange) {
        String email = getAuthEmail(exchange);

        if (email == null) {
            redirect303(exchange, "/login");
            return;
        }

        UserInfo user = users.get(email);
        renderTemplate(exchange, "profile.html", user);
    }

    protected void booksGetAuthHandler(HttpExchange exchange) {
        String email = getAuthEmail(exchange);

        if (email == null) {
            redirect303(exchange, "/login");
            return;
        }
        renderTemplate(exchange, "books.html", Map.of("books", books));
    }

    protected void borrowBookHandler(HttpExchange exchange) {
        String email = getAuthEmail(exchange);
        if (email == null) { redirect303(exchange, "/login"); return; }

        Map<String, String> data = Utils.parseUrlEncoded(getBody(exchange), "&");
        String bookIdStr = data.get("bookId");

        if (bookIdStr != null) {
            int bookId = Integer.parseInt(bookIdStr);
            Book targetBook = null;

            for (Book book : books) {
                if (book.getId() == bookId && "AVAILABLE".equals(book.getStatus())) {
                    book.setStatus("BORROWED");
                    targetBook = book;
                    break;
                }
            }

            if (targetBook != null) {
                renderTemplate(exchange, "borrow_success.html", Map.of("book", targetBook));
                return;
            }
        }
        redirect303(exchange, "/books");
    }

    private void returnBookHandler(HttpExchange exchange) {
        String email = getAuthEmail(exchange);
        if (email == null) {
            redirect303(exchange, "/login");
            return;
        }

        Map<String, String> data = Utils.parseUrlEncoded(getBody(exchange), "&");
        String bookIdStr = data.get("bookId");
        if (bookIdStr == null) {
            redirect303(exchange, "/books");
            return;
        }
        int bookId = Integer.parseInt(bookIdStr);

        if (email.equals(borrowedBooks.get(bookIdStr))) {
            for (Book book : books) {
                if (book.getId() == bookId) {
                    book.setStatus("AVAILABLE");
                    borrowedBooks.remove(bookIdStr);
                    userBooksCount.put(email, userBooksCount.getOrDefault(email, 1) - 1);
                    break;
                }
            }
        }
        redirect303(exchange, "/books");
    }
}
