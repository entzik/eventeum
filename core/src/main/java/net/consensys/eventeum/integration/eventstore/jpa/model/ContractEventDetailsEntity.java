package net.consensys.eventeum.integration.eventstore.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.consensys.eventeum.dto.event.ContractEventStatus;
import net.consensys.eventeum.integration.eventstore.jpa.JPAConstants;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.persistence.*;
import java.util.List;

/**
 * Represents the details of an emitted Ethereum smart contract event.
 *
 * @author Craig Williams <craig.williams@consensys.net>
 */
@Entity
@Data
@EqualsAndHashCode
@Table(
        name = "contract_event_details",
        indexes = {
                @Index(name = "ev_biz_id", columnList = "transaction_hash,block_hash,block_number,log_index")
        }
)
@ConditionalOnProperty(name = JPAConstants.EVENT_STORE_TYPE_PROPERTY_NAME, havingValue = JPAConstants.EVENT_STORE_TYPE_JPA)
public class ContractEventDetailsEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {@org.hibernate.annotations.Parameter(
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
    @Column(name = "version")
    private int version;

    @Column(name = "name")
    private String name;

    @Column(name = "filter_id")
    private String filterId;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<EventParameterEntity> parameters;

    @Column(name = "transaction_hash")
    private String transactionHash;

    @Column(name = "log_index")
    private long logIndex;

    @Column(name = "block_number")
    private long blockNumber;

    @Column(name = "block_hash")
    private String blockHash;

    @Column(name = "address")
    private String address;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractEventStatus status = ContractEventStatus.UNCONFIRMED;

    @Column(name = "signature")
    private String eventSpecificationSignature;
}
