package net.consensys.eventeum.integration.eventstore.jpa.repository;

import net.consensys.eventeum.integration.eventstore.jpa.JPAConstants;
import net.consensys.eventeum.integration.eventstore.jpa.model.ContractEventDetailsEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnProperty(name = JPAConstants.EVENT_STORE_TYPE_PROPERTY_NAME, havingValue = JPAConstants.EVENT_STORE_TYPE_JPA)
public interface ContractEventDetailsJpaRepository extends JpaRepository<ContractEventDetailsEntity, String> {
    int countByTransactionHashAndBlockHashAndBlockNumberAndLogIndex(String transactionHash, String blockHash, long blockNumber, long logIndex);

    Page<ContractEventDetailsEntity> findByEventSpecificationSignature(String signature, Pageable pagination);

    List<ContractEventDetailsEntity> findByEventSpecificationSignature(String signature);
}