package team16.spring_project1.global.jpa.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import lombok.AccessLevel
import lombok.EqualsAndHashCode
import lombok.Getter
import lombok.Setter

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
open class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @EqualsAndHashCode.Include
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    var id: Long? = null
    constructor()
}
