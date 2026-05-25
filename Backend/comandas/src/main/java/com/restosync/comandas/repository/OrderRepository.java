package com.restosync.comandas.repository;

import com.restosync.comandas.entity.Order;
import com.restosync.comandas.enums.OrderStatus;
import com.restosync.comandas.enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.time.LocalDateTime;
import java.util.List;
 
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
 
    // ── Consultas para paneles en tiempo real ────────────────────────────────
 
    /**
     * Devuelve pedidos activos (PENDIENTE o EN_PREPARACION) que contengan
     * al menos un ítem de la categoría indicada.
     * Usado por el panel de Cocina (PLATO) y el panel de Bar (BEBIDA).
     */
    @Query("""
            SELECT DISTINCT o FROM Order o
            JOIN o.items i
            WHERE o.status IN (:statuses)
              AND i.category = :category
            ORDER BY o.createdAt ASC
            """)
    List<Order> findActiveByItemCategory(
            @Param("statuses")  List<OrderStatus> statuses,
            @Param("category")  ProductCategory category
    );
 
    /**
     * Pedidos activos de un mesero específico.
     * Usado en la vista "Mis Comandas" del Mesero.
     */
    @Query("""
            SELECT o FROM Order o
            WHERE o.waiter.id = :waiterId
              AND o.status NOT IN ('ENTREGADO', 'CANCELADO')
            ORDER BY o.createdAt DESC
            """)
    List<Order> findActiveByWaiter(@Param("waiterId") Long waiterId);

    /**
     * Todos los pedidos activos. Usado por ADMINISTRADOR cuando entra
     * a la vista operativa de mesero.
     */
    @Query("""
            SELECT o FROM Order o
            WHERE o.status NOT IN ('ENTREGADO', 'CANCELADO')
            ORDER BY o.createdAt DESC
            """)
    List<Order> findActive();
 
    // ── Historial paginado con filtros opcionales ────────────────────────────
 
    /**
     * Historial de pedidos finalizados (ENTREGADO o CANCELADO) con filtros
     * opcionales por fecha, mesa, mesero y estado.
     * Parámetros nulos se ignoran automáticamente.
     */
    @Query("""
            SELECT o FROM Order o
            WHERE o.status IN ('ENTREGADO', 'CANCELADO')
              AND (:startDate      IS NULL OR o.createdAt    >= :startDate)
              AND (:endDate        IS NULL OR o.createdAt    <= :endDate)
              AND (:tableOrReg     IS NULL OR o.tableOrRegister = :tableOrReg)
              AND (:waiterId       IS NULL OR o.waiter.id    = :waiterId)
              AND (:status         IS NULL OR o.status       = :status)
            ORDER BY o.createdAt DESC
            """)
    Page<Order> findHistory(
            @Param("startDate")  LocalDateTime startDate,
            @Param("endDate")    LocalDateTime endDate,
            @Param("tableOrReg") String tableOrRegister,
            @Param("waiterId")   Long waiterId,
            @Param("status")     OrderStatus status,
            Pageable pageable
    );
 
    // ── Utilitarios ──────────────────────────────────────────────────────────
 
    boolean existsByTicketNumber(String ticketNumber);
 
    List<Order> findAllByStatusOrderByCreatedAtAsc(OrderStatus status);
 
    @Query("""
            SELECT COUNT(o) FROM Order o
            WHERE o.status = :status
            """)
    long countByStatus(@Param("status") OrderStatus status);
}
