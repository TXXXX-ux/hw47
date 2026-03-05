package kg.attractor.java.handler;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import kg.attractor.java.server.Utils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Lesson45 extends Lesson44Server {

    public Lesson45(String host, int port) throws IOException {
        super(host, port);
        registerGet("/register", this::registerGetHandler);
        registerPost("/register", this::registerPostHandler);
        registerGet("/login", this::loginGetHandler);
        registerPost("/login", this::loginPostHandler);
        registerGet("/profile", this::profileGetHandler);

        registerGet("/index", this::indexHandler);
    }

    private void indexHandler(HttpExchange exchange) {
        renderTemplate(exchange, "index.html", null);
    }

    protected static final Map<String, UserInfo> users = new HashMap<>();

    static {
        users.put("example@gmail.com", new UserInfo("Админ", "example@gmail.com", "123"));
        users.put("test@gmail.com", new UserInfo("Тестер", "test@gmail.com", "123"));
    }

    private void registerGetHandler(HttpExchange exchange) {
        renderTemplate(exchange, "register.html", null);
    }

    private void registerPostHandler(HttpExchange exchange) {
        Map<String, String> data = Utils.parseUrlEncoded(getBody(exchange), "&");
        String email = data.get("email");
        if (users.containsKey(email)) {
            renderTemplate(exchange, "register.html", Map.of("error", "Уже есть такой юзер!"));
        } else {
            users.put(email, new UserInfo(data.get("name"), email, data.get("password")));
            redirect303(exchange, "/login");
        }
    }

    private void loginGetHandler(HttpExchange exchange) {
        renderTemplate(exchange, "login.html", null);
    }

    private void loginPostHandler(HttpExchange exchange) {
        Map<String, String> data = Utils.parseUrlEncoded(getBody(exchange), "&");
        UserInfo user = users.get(data.get("email"));
        if (user != null && user.getPassword().equals(data.get("password"))) {
            redirect303(exchange, "/profile?email=" + user.getEmail());
        } else {
            renderTemplate(exchange, "login.html", Map.of("error", "Неверный логин или пароль"));
        }
    }

    private void profileGetHandler(HttpExchange exchange) {
        String email = exchange.getRequestURI().getQuery();
        UserInfo user = (email != null) ? users.get(email.replace("email=", "")) : null;
        renderTemplate(exchange, "profile.html", user != null ? user : new UserInfo("Гость", "none", ""));
    }

    public static class UserInfo {
        private String name, email, password;
        public UserInfo(String n, String e, String p) { name=n; email=e; password=p; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }
}

