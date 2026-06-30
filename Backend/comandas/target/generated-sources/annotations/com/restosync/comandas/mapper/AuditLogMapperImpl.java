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
    date = "2026-06-29T18:51:10-0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
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
        auditLogResponse.id( auditLog.getId() );
        auditLogResponse.action( auditLog.getAction() );
        Map<String, Object> map = auditLog.getDetails();
        if ( map != null ) {
            auditLogResponse.details( new LinkedHashMap<String, Object>( map ) );
        }
        auditLogResponse.createdAt( auditLog.getCreatedAt() );

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
