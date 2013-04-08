package org.jboss.resteasy.test.providers.atom.jaxb.extended.resources;

/**
 * 10 16 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */

import javax.xml.bind.annotation.*;
import java.util.Date;

@XmlRootElement(name="metadata")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Categories.class})
public class AtomAssetMetadata {

    @XmlElement
    private Uuid uuid;
    @XmlElement
    private Categories categories;
    @XmlElement
    private Note note;
    @XmlElement
    private Created created;
    @XmlElement
    private Format format;
    @XmlElement
    private Disabled disabled;
    @XmlElement
    private State state;
    @XmlElement
    private VersionNumber versionNumber;
    @XmlElement
    private CheckinComment checkinComment;
    @XmlElement
    private Archived archived;

    public String getUuid() {
        return uuid != null?uuid.getValue():null;
    }

    public void setUuid(String uuid) {
        if (this.uuid == null) {
            this.uuid = new Uuid();
        }
        this.uuid.setValue(uuid);
    }

    public String[] getCategories() {
        return categories != null ? categories.getValues() : null;
    }

    public void setCategories(String[] categories) {
        if (this.categories == null ) {
            this.categories = new Categories();
        }
        this.categories.setValue(categories);
    }

    public String getNote() {
        return note != null ? note.getValue() : null;
    }

    public void setNote(String note) {
        if (this.note == null) {
            this.note = new Note();
        }
        this.note.setValue(note);
    }

    public Date getCreated() {
        return created != null ?created.getValue():null;
    }

    public void setCreated(Date created) {
        if (this.created == null) {
            this.created = new Created();
        }
        this.created.setValue(created);
    }

    public String getFormat() {
        return format != null ? format.getValue() : null;
    }

    public void setFormat(String format) {
        if (this.format == null) {
            this.format = new Format();
        }
        this.format.setValue(format);
    }

    public boolean getDisabled() {
        return disabled != null ? disabled.getValue() : false;
    }

    public void setDisabled(boolean disabled) {
        if (this.disabled == null) {
            this.disabled = new Disabled();
        }
        this.disabled.setValue(disabled);
    }

    public String getState() {
        return state != null ? state.getValue() : null;
    }

    public void setState(String state) {
        if (this.state == null) {
            this.state = new State();
        }
        this.state.setValue(state);
    }

    public long getVersionNumber() {
        return versionNumber != null ? versionNumber.getValue() : -1L;
    }

    public void setVersionNumber(long versionNumber) {
        if (this.versionNumber == null) {
            this.versionNumber = new VersionNumber();
        }
        this.versionNumber.setValue(versionNumber);
    }

    public String getCheckinComment() {
        return checkinComment != null ? checkinComment.getValue() : null;
    }

    public void setCheckinComment(String checkinComment) {
        if (this.checkinComment == null ) {
            this.checkinComment = new CheckinComment();
        }
        this.checkinComment.setValue(checkinComment);
    }

    public boolean isArchived() {
        return archived != null ? archived.getValue() : false;
    }

    public void setArchived(boolean archived) {
        if (this.archived == null) {
            this.archived = new Archived();
        }
        this.archived.setValue(archived);
    }

    @Override
    public String toString() {
        return "AtomAssetMetadata{" +
                "uuid=" + uuid +
                ", categories=" + categories +
                ", note=" + note +
                ", created=" + created +
                ", format=" + format +
                ", disabled=" + disabled +
                ", state=" + state +
                ", versionNumber=" + versionNumber +
                ", checkinComment=" + checkinComment +
                ", archived=" + archived +
                '}';
    }

}
