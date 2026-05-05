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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Parameter> parameters = new ArrayList<>();

}