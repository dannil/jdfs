package com.github.dannil.jdfs.model;

import java.time.OffsetDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "files")
public class FileModel {

    // TODO THIS DOES NOT WORK
    // Id is null in database
    // @GeneratedValue only works on fields also annotated with @Id, but id is in this
    // case not the primary key
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "path", unique = true)
    @NotNull
    private String path;

    @Column(name = "last_changed")
    @NotNull
    private OffsetDateTime lastChanged;

    @Column(name = "hash")
    @NotNull
    private String hash;

    protected FileModel() {
        super();
    }

    public FileModel(String path, OffsetDateTime lastChanged, String hash) {
        this();
        this.path = Objects.requireNonNull(path);
        this.lastChanged = Objects.requireNonNull(lastChanged);
        this.hash = Objects.requireNonNull(hash);
    }

    // public File(Integer id, String path, ZonedDateTime lastChanged) {
    // this(path, lastChanged);
    // this.id = id;
    // }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public OffsetDateTime getLastChanged() {
        return this.lastChanged;
    }

    public void setLastChanged(OffsetDateTime lastChanged) {
        this.lastChanged = lastChanged;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.path, this.lastChanged);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FileModel)) {
            return false;
        }
        FileModel f = (FileModel) obj;
        return super.equals(f) && Objects.equals(this.path, f.path);
    }

    @Override
    public String toString() {
        return "FileModel [id=" + this.id + ", path=" + this.path + ", lastChanged=" + this.lastChanged + ", hash="
                + this.hash + "]";
    }

}
