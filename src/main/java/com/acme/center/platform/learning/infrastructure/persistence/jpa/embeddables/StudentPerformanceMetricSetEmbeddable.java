package com.acme.center.platform.learning.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class StudentPerformanceMetricSetEmbeddable {
    @Column(name = "total_completed_courses", nullable = false)
    private Integer totalCompletedCourses;

    @Column(name = "total_completed_tutorials", nullable = false)
    private Integer totalCompletedTutorials;

    public StudentPerformanceMetricSetEmbeddable(Integer courses, Integer tutorials) {
        this.totalCompletedCourses = courses;
        this.totalCompletedTutorials = tutorials;
    }
}
