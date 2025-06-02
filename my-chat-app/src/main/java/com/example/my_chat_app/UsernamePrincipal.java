package com.example.my_chat_app;

import java.security.Principal;
import java.util.Objects;

// This class wraps a simple String username into a Principal object,
// which Spring's WebSocket infrastructure expects for user identification.
public class UsernamePrincipal implements Principal {
    private final String name;

    public UsernamePrincipal(String name) {
        this.name = Objects.requireNonNull(name, "Username cannot be null");
    }

    @Override
    public String getName() {
        return name;
    }

    // Optional: for better logging/debugging
    @Override
    public String toString() {
        return "UsernamePrincipal{" +
                "name='" + name + '\'' +
                '}';
    }

    // Optional: for proper equality checks if store these in collections
    @Override
    public  boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsernamePrincipal that = (UsernamePrincipal) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
