package com.acme.center.platform.learning.infrastructure.persistence.jpa.entities;

import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.domain.model.valueobjects.ProfileId;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.embeddables.StudentPerformanceMetricSetEmbeddable;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.converters.AcmeStudentRecordIdPersistenceConverter;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.converters.ProfileIdPersistenceConverter;
import com.acme.center.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA persistence entity for students.
 */
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class StudentPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = AcmeStudentRecordIdPersistenceConverter.class)
    @Column(name = "acme_student_id", nullable = false, unique = true)
    private AcmeStudentRecordId acmeStudentRecordId;

    @Convert(converter = ProfileIdPersistenceConverter.class)
    @Column(name = "profile_id", nullable = false, unique = true)
    private ProfileId profileId;

    @Embedded
    private StudentPerformanceMetricSetEmbeddable performanceMetricSet;
}

