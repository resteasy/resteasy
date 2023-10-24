package org.jboss.resteasy.links.test;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class BookClassProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(BookStore.class, "json"),
                Arguments.of(BookStore.class, "xml"),
                Arguments.of(BookStoreMinimal.class, "json"),
                Arguments.of(BookStoreMinimal.class, "xml"));
    }
}
