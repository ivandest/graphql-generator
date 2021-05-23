package ru.destinyman.parsers;

public class Entity {

    String code;
    String caption;
    String dataType;
    String isNullable;
    String reference;
    String comment;


    public Entity() {
    }

    public Entity(String code, String caption, String dataType, String isNullable, String reference, String comment) {
        this.code = code;
        this.caption = caption;
        this.dataType = dataType;
        this.isNullable = isNullable;
        this.reference = reference;
        this.comment = comment;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
