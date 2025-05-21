package com.CRUD.firstApp.courses;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Embeddable
public class CourseMetaData {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer duration;

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private List<String> objectives;

}
