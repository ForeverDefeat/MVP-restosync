package com.restosync.comandas;

import com.restosync.comandas.entity.Product;
import com.restosync.comandas.entity.User;
import com.restosync.comandas.enums.ProductCategory;
import com.restosync.comandas.enums.UserRole;
import com.restosync.comandas.repository.AuditLogRepository;
import com.restosync.comandas.repository.OrderRepository;
import com.restosync.comandas.repository.ProductRepository;
import com.restosync.comandas.repository.UserRepository;
import com.restosync.comandas.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersMySqlConfig.class)
@RequiresDocker
public abstract class IntegrationTestSupport {

    protected static final String RAW_PASSWORD = "123456";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected AuditLogRepository auditLogRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtUtil jwtUtil;

    protected User admin;
    protected User waiter;
    protected User cook;
    protected User bartender;
    protected User inactiveWaiter;
    protected Product plate;
    protected Product drink;
    protected Product unavailablePlate;

    @BeforeEach
    void resetData() {
        auditLogRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        String encodedPassword = passwordEncoder.encode(RAW_PASSWORD);
        admin = userRepository.save(TestDataFactory.user("admin@test.local", UserRole.ADMINISTRADOR, encodedPassword));
        waiter = userRepository.save(TestDataFactory.user("mesero@test.local", UserRole.MESERO, encodedPassword));
        cook = userRepository.save(TestDataFactory.user("cocina@test.local", UserRole.COCINERO, encodedPassword));
        bartender = userRepository.save(TestDataFactory.user("bar@test.local", UserRole.BARTENDER, encodedPassword));
        inactiveWaiter = TestDataFactory.user("inactive@test.local", UserRole.MESERO, encodedPassword);
        inactiveWaiter.setActive(false);
        inactiveWaiter = userRepository.save(inactiveWaiter);

        plate = productRepository.save(TestDataFactory.product("Lomo saltado", ProductCategory.PLATO, true));
        drink = productRepository.save(TestDataFactory.product("Chicha morada", ProductCategory.BEBIDA, true));
        unavailablePlate = productRepository.save(TestDataFactory.product("Ceviche agotado", ProductCategory.PLATO, false));
    }

    protected String bearer(User user) {
        return "Bearer " + jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
    }
}
