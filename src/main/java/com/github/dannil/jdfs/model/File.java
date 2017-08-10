package com.github.dannil.jdfs.model;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "files")
public class File {

    // TODO THIS DOES NOT WORK
    // @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Id
    @NotNull
    private String path;

    @NotNull
    private ZonedDateTime lastChanged;

    @NotNull
    private String hash;

    public File() {
        super();
    }

    public File(String path, ZonedDateTime lastChanged) {
        this();
        this.path = path;
        this.lastChanged = lastChanged;
    }

    // public File(Integer id, String path, ZonedDateTime lastChanged) {
    // this(path, lastChanged);
    // this.id = id;
    // }

    public Integer getId() {
        return id;
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

    public ZonedDateTime getLastChanged() {
        return this.lastChanged;
    }

    public void setLastChanged(ZonedDateTime lastChanged) {
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
        return Objects.hash(this.id, this.path, this.lastChanged);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof File)) {
            return false;
        }
        File f = (File) obj;
        return super.equals(f) && Objects.equals(this.path, f.path);
    }

    @Override
    public String toString() {
        return "File [id=" + id + ", path=" + path + ", lastChanged=" + lastChanged + "]";
    }

}
