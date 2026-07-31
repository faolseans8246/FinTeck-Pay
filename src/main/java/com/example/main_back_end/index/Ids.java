package com.example.main_back_end.index;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@SuperBuilder
@EntityListeners(ArithmeticException.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Ids implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "UUID", nullable = false, unique = true, updatable = false)
    private UUID id;

    @CreationTimestamp
    @Column(name = "Create_At")
    private Timestamp createAt;

    @UpdateTimestamp
    @Column(name = "Update_At")
    private Timestamp updateAt;


    @PrePersist
    public void prePersist() {
        if (id == null)
            id = java.util.UUID.randomUUID();
    }
}
