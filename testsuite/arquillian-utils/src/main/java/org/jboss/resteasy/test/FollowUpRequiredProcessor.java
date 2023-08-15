/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.jboss.resteasy.test.annotations.FollowUpRequired;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@SupportedOptions({
        "dev.resteasy.test.follow.up.level"
})
public class FollowUpRequiredProcessor extends AbstractProcessor {
    private final Set<String> supportedAnnotations;

    public FollowUpRequiredProcessor() {
        supportedAnnotations = Set.of(FollowUpRequired.class.getName());
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver() && !annotations.isEmpty()) {
            final Messager messager = processingEnv.getMessager();
            final String kindLevel = processingEnv.getOptions().getOrDefault("dev.resteasy.test.follow.up.level", "WARNING");
            Diagnostic.Kind kind;
            try {
                kind = Diagnostic.Kind.valueOf(kindLevel);
            } catch (IllegalArgumentException e) {
                kind = Diagnostic.Kind.WARNING;
                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                        "Failed to parse follow-up level " + kindLevel + ". Defaulting to " + kind + ".");
            }
            for (TypeElement annotation : annotations) {
                final Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element e : annotated) {
                    // Get the message for the annotation
                    final var msg = e.getAnnotation(FollowUpRequired.class).value();
                    // Print a warning message since this requires some kind of action
                    messager.printMessage(kind, "Follow up required: " + msg, e);
                }
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
