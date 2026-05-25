package com.restosync.comandas.mapper;

import com.restosync.comandas.dto.response.AuditLogResponse;
import com.restosync.comandas.entity.AuditLog;
import com.restosync.comandas.entity.Order;
import com.restosync.comandas.entity.User;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-25T13:59:36-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class AuditLogMapperImpl implements AuditLogMapper {

    @Override
    public AuditLogResponse toResponse(AuditLog auditLog) {
        if ( auditLog == null ) {
            return null;
        }

        AuditLogResponse.AuditLogResponseBuilder auditLogResponse = AuditLogResponse.builder();

        auditLogResponse.userId( auditLogUserId( auditLog ) );
        auditLogResponse.userName( auditLogUserName( auditLog ) );
        auditLogResponse.orderId( auditLogOrderId( auditLog ) );
        auditLogResponse.action( auditLog.getAction() );
        auditLogResponse.createdAt( auditLog.getCreatedAt() );
        Map<String, Object> map = auditLog.getDetails();
        if ( map != null ) {
            auditLogResponse.details( new LinkedHashMap<String, Object>( map ) );
        }
        auditLogResponse.id( auditLog.getId() );

        return auditLogResponse.build();
    }

    @Override
    public List<AuditLogResponse> toResponseList(List<AuditLog> logs) {
        if ( logs == null ) {
            return null;
        }

        List<AuditLogResponse> list = new ArrayList<AuditLogResponse>( logs.size() );
        for ( AuditLog auditLog : logs ) {
            list.add( toResponse( auditLog ) );
        }

        return list;
    }

    private Long auditLogUserId(AuditLog auditLog) {
        User user = auditLog.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private String auditLogUserName(AuditLog auditLog) {
        User user = auditLog.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getName();
    }

    private Long auditLogOrderId(AuditLog auditLog) {
        Order order = auditLog.getOrder();
        if ( order == null ) {
            return null;
        }
        return order.getId();
    }
}
