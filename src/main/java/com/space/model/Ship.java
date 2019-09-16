package com.space.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Validated
@Table(name = "ship")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ship implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "name")
    private String name;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "planet")
    private String planet;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ShipType shipType;

    @NotNull
    @FutureOrPresent
    @Column(name = "prodDate")
    private Date prodDate;

    @Column(name = "isUsed")
    private Boolean isUsed;

    @NotNull()
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "0.99")
    @Column(name = "speed")
    private Double speed;

    @Min(value = 1)
    @Max(value = 9999)
    @Column(name = "crewSize")
    private Integer crewSize;

    @Column(name = "rating")
    private Double rating;
}

