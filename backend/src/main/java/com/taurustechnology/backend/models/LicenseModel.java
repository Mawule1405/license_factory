package com.taurustechnology.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "license_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseModel extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference
    private Project project;

    @ElementCollection
    @CollectionTable(name = "model_parameters", joinColumns = @JoinColumn(name = "model_id"))
    @Column(name = "parameter_name")
    @Builder.Default
    private List<String> parameters = new ArrayList<>();

}