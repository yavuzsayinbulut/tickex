package com.tickex.authservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_retail_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRetailProfileEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;      // user_master.id

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "nationality", length = 3)
    private String nationality; // ISO3 country code gibi düşünebiliriz, örn: TUR

    @Column(name = "gender", length = 20)
    private String gender; // ileride ENUM'a çeviririz, şimdilik esnek bırakalım
}
