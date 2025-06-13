package com.gym.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "training_types")
@Builder
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_type_name", nullable = false, unique = true)
    private String trainingTypeName;

    @OneToMany(mappedBy = "trainingType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Training> trainings = new HashSet<>();
}