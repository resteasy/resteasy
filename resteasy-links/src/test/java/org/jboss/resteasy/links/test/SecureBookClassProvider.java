package org.jboss.resteasy.links.test;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class SecureBookClassProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(SecureBookStore.class, "admin"),
                Arguments.of(SecureBookStoreMinimal.class, "admin"),
                Arguments.of(SecureBookStore.class, "power-user"),
                Arguments.of(SecureBookStoreMinimal.class, "power-user"),
                Arguments.of(SecureBookStore.class, "user"),
                Arguments.of(SecureBookStoreMinimal.class, "user"));
    }
}
