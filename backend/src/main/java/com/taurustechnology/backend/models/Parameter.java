package com.taurustechnology.backend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="parameters")
@Setter @Getter @Builder @AllArgsConstructor
@NoArgsConstructor
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    private String type;
}
