package com.harmonylink.harmonylink.models.user.userprofile;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("available_hobbies")
public class Hobby {

    @Id
    private String id;
    @Field
    private String name;


    public Hobby() {}

    public Hobby(String name) {
        this.name = name;
    }


    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Hobby{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
