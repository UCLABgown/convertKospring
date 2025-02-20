package team16.spring_project1.domain.order.Repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import team16.spring_project1.domain.order.Entity.Order

interface OrderRepository : JpaRepository<Order, Long> {
    fun findByEmail(email: String): List<Order>

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET status = :newStatus WHERE status = :currentStatus", nativeQuery = true)
    fun updateStatusByCurrentStatus(
        @Param("currentStatus") currentStatus: String,
        @Param("newStatus") newStatus: String
    )

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET status = :newStatus", nativeQuery = true)
    fun resetAllStatuses(@Param("newStatus") newStatus: String)
}

