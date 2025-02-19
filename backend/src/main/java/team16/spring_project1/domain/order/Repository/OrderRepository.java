package team16.spring_project1.domain.order.Repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import team16.spring_project1.domain.order.Entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET status = :newStatus WHERE status = :currentStatus", nativeQuery = true)
    void updateStatusByCurrentStatus(@Param("currentStatus") String currentStatus,
                                     @Param("newStatus") String newStatus);

    @Transactional
    @Modifying
    @Query(value = "UPDATE orders SET status = :newStatus", nativeQuery = true)
    void resetAllStatuses(@Param("newStatus") String newStatus);

}
