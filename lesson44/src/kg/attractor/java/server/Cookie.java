package kg.attractor.java.server;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cookie<V> {
  private final String name;
  private final V value;
  private Integer maxAge;
  private boolean httpOnly;

  public Cookie(String name, V value) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(value);
    this.name = name.strip();
    this.value = value;
  }

  public static <V> Cookie<V> make(String name, V value) {
    return new Cookie<>(name, value);
  }

  public void setMaxAge(Integer maxAgeInSeconds) {
    this.maxAge = maxAgeInSeconds;
  }

  public void setHttpOnly(boolean httpOnly) {
    this.httpOnly = httpOnly;
  }

  private V getValue() { return value; }
  private Integer getMaxAge() { return maxAge; }
  private String getName() { return name; }
  private boolean isHttpOnly() { return httpOnly; }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    Charset utf8 = StandardCharsets.UTF_8;
    String encodeName = URLEncoder.encode(getName().strip(), utf8);
    String strValue = getValue().toString();
    String encodeValue = URLEncoder.encode(strValue.strip(), utf8);

    sb.append(String.format("%s=%s", encodeName, encodeValue));

    if (getMaxAge() != null) {
      sb.append(String.format("; Max-Age=%s", getMaxAge()));
    }
    if (isHttpOnly()) {
      sb.append("; HttpOnly");
    }
    return sb.toString();
  }

  public static Map<String, String> parse(String cookieString) {
    Map<String, String> result = new HashMap<>();
    if (cookieString == null || cookieString.isBlank()) return result;

    String[] pairs = cookieString.split(";");
    for (String pair : pairs) {
      String[] kv = pair.split("=", 2);
      if (kv.length == 2) {
        String key = URLDecoder.decode(kv[0].strip(), StandardCharsets.UTF_8);
        String value = URLDecoder.decode(kv[1].strip(), StandardCharsets.UTF_8);
        result.put(key, value);
      }
    }
    return result;
  }
}