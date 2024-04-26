package com.harmonylink.harmonylink.models.user.userprofile;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("available_educations")
public class Education {

    @Id
    private String id;
    @Field
    private String name;


    public Education() {}

    public Education(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

}
