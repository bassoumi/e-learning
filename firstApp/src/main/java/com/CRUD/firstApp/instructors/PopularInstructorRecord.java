package com.CRUD.firstApp.instructors;

public record PopularInstructorRecord (
        Integer  instructorId,
        String instructorName,
        Long subscriptionCount
) {
}
