/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.providers.jackson2.objectmapper.resources;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class User implements IdEntry {

    private long id;
    private String name;
    private String email;
    private String phoneNumber;
    // This is the ISO_INSTANT format the Jakarta JSON Binding expects. It's easiest to just use this format
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant created;
    // This is the ISO_INSTANT format the Jakarta JSON Binding expects. It's easiest to just use this format
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
    private Instant modified;

    @Override
    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Optional<String> getPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(final Instant created) {
        this.created = created;
    }

    public Optional<Instant> getModified() {
        return Optional.ofNullable(modified);
    }

    public void setModified(final Instant modified) {
        this.modified = modified;
    }

    @Override
    public int compareTo(final IdEntry o) {
        return Long.compare(id, o.getId());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User[id=" + id + ", name=" + name + ", email=" + email + ", phoneNumber=" + phoneNumber + ", created=" + created
                + ", modified=" + modified
                + "]";
    }
}
