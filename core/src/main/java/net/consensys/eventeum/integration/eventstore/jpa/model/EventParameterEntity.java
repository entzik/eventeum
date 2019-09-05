package net.consensys.eventeum.integration.eventstore.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.consensys.eventeum.integration.eventstore.jpa.JPAConstants;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.persistence.*;

@Data
@EqualsAndHashCode
@Entity
@Table(name = "contract_event_parameters")
//@ConditionalOnProperty(name = JPAConstants.EVENT_STORE_TYPE_PROPERTY_NAME, havingValue = JPAConstants.EVENT_STORE_TYPE_JPA)
public class EventParameterEntity {
    @Id
    @GeneratedValue(
            generator = "UUID"
    )
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {@Parameter(
                    name = "uuid_gen_strategy_class",
                    value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
            )}
    )
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            length = 64
    )
    private String id;

    @Version
    @Column(
            name = "version"
    )
    private int version;

    @Column(name = "indexed")
    private boolean indexed;

    @Column(name = "type")
    private String type;

    @Column(name = "string_value")
    private String stringValue;

    @Column(name = "long_value")
    private long intValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="owner_id")
    private ContractEventDetailsEntity owner;
}
