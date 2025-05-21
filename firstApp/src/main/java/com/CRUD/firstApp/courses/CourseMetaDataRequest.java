package com.CRUD.firstApp.courses;

import java.util.List;

public record CourseMetaDataRequest(Integer duration,
                                    List<String> tags,
                                    List<String> objectives
) {
}
