package team16.spring_project1.global.jpa.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import lombok.AccessLevel
import lombok.Getter
import lombok.Setter
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Getter
@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
open class BaseTime : BaseEntity {
    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    var createDate: LocalDateTime? = null

    @LastModifiedDate
    @Setter(AccessLevel.PRIVATE)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    var modifyDate: LocalDateTime? = null

    constructor()
}