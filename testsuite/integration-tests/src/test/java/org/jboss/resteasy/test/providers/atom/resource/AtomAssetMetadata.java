package org.jboss.resteasy.test.providers.atom.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.Date;

@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({AtomComplexModelCategories.class})
public class AtomAssetMetadata {

    @XmlElement
    private AtomComplexModelUuid uuid;
    @XmlElement
    private AtomComplexModelCategories categories;
    @XmlElement
    private AtomComplexModelNote note;
    @XmlElement
    private AtomComplexModelCreated created;
    @XmlElement
    private AtomComplexModelFormat format;
    @XmlElement
    private AtomComplexModelDisabled disabled;
    @XmlElement
    private AtomComplexModelState state;
    @XmlElement
    private AtomComplexModelVersionNumber versionNumber;
    @XmlElement
    private AtomComplexModelCheckinComment checkinComment;
    @XmlElement
    private AtomComplexModelArchived archived;

    public String getUuid() {
        return uuid != null ? uuid.getValue() : null;
    }

    public void setUuid(String uuid) {
        if (this.uuid == null) {
            this.uuid = new AtomComplexModelUuid();
        }
        this.uuid.setValue(uuid);
    }

    public String[] getCategories() {
        return categories != null ? categories.getValues() : null;
    }

    public void setCategories(String[] categories) {
        if (this.categories == null ) {
            this.categories = new AtomComplexModelCategories();
        }
        this.categories.setValue(categories);
    }

    public String getNote() {
        return note != null ? note.getValue() : null;
    }

    public void setNote(String note) {
        if (this.note == null) {
            this.note = new AtomComplexModelNote();
        }
        this.note.setValue(note);
    }

    public Date getCreated() {
        return created != null ? created.getValue() : null;
    }

    public void setCreated(Date created) {
        if (this.created == null) {
            this.created = new AtomComplexModelCreated();
        }
        this.created.setValue(created);
    }

    public String getFormat() {
        return format != null ? format.getValue() : null;
    }

    public void setFormat(String format) {
        if (this.format == null) {
            this.format = new AtomComplexModelFormat();
        }
        this.format.setValue(format);
    }

    public boolean getDisabled() {
        return disabled != null ? disabled.getValue() : false;
    }

    public void setDisabled(boolean disabled) {
        if (this.disabled == null) {
            this.disabled = new AtomComplexModelDisabled();
        }
        this.disabled.setValue(disabled);
    }

    public String getState() {
        return state != null ? state.getValue() : null;
    }

    public void setState(String state) {
        if (this.state == null) {
            this.state = new AtomComplexModelState();
        }
        this.state.setValue(state);
    }

    public long getVersionNumber() {
        return versionNumber != null ? versionNumber.getValue() : -1L;
    }

    public void setVersionNumber(long versionNumber) {
        if (this.versionNumber == null) {
            this.versionNumber = new AtomComplexModelVersionNumber();
        }
        this.versionNumber.setValue(versionNumber);
    }

    public String getCheckinComment() {
        return checkinComment != null ? checkinComment.getValue() : null;
    }

    public void setCheckinComment(String checkinComment) {
        if (this.checkinComment == null ) {
            this.checkinComment = new AtomComplexModelCheckinComment();
        }
        this.checkinComment.setValue(checkinComment);
    }

    public boolean isArchived() {
        return archived != null ? archived.getValue() : false;
    }

    public void setArchived(boolean archived) {
        if (this.archived == null) {
            this.archived = new AtomComplexModelArchived();
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
