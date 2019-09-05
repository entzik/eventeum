package net.consensys.eventeum.integration.eventstore.jpa;

import net.consensys.eventeum.dto.event.ContractEventDetails;
import net.consensys.eventeum.dto.event.parameter.EventParameter;
import net.consensys.eventeum.dto.event.parameter.NumberParameter;
import net.consensys.eventeum.dto.event.parameter.StringParameter;
import net.consensys.eventeum.integration.eventstore.SaveableEventStore;
import net.consensys.eventeum.integration.eventstore.jpa.model.ContractEventDetailsEntity;
import net.consensys.eventeum.integration.eventstore.jpa.model.EventParameterEntity;
import net.consensys.eventeum.integration.eventstore.jpa.repository.ContractEventDetailsJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.collect.Streams.*;
import static java.util.stream.Collectors.toList;

@Component
@ConditionalOnProperty(name = JPAConstants.EVENT_STORE_TYPE_PROPERTY_NAME, havingValue = JPAConstants.EVENT_STORE_TYPE_JPA)
public class JpaEventStore implements SaveableEventStore {
    static Logger LOGGER = LoggerFactory.getLogger(JpaEventStore.class);

    @Autowired
    ContractEventDetailsJpaRepository repository;

    private Predicate<EventParameterEntity> indexedEventParameterEntityPredicate = EventParameterEntity::isIndexed;

    @Override
    public void save(ContractEventDetails contractEventDetails) {
        LOGGER.info("saving event to database: " + contractEventDetails.toString());
        int count = repository.countByTransactionHashAndBlockHashAndBlockNumberAndLogIndex(
                contractEventDetails.getTransactionHash(),
                contractEventDetails.getBlockHash(),
                contractEventDetails.getBlockNumber().longValue(),
                contractEventDetails.getLogIndex().longValue()
        );
        if (count == 0)
            repository.save(toContractEventDetailsEntity(contractEventDetails));
        else
            LOGGER.info("event already saved, ignoring");
    }

    @Override
    public Page<ContractEventDetails> getContractEventsForSignature(String eventSignature, PageRequest pagination) {
        return repository.findByEventSpecificationSignature(eventSignature, pagination).map(this::toContractEventDetails);
    }

    @Override
    public boolean isPagingZeroIndexed() {
        return true;
    }

    ContractEventDetailsEntity toContractEventDetailsEntity(ContractEventDetails contractEventDetails) {
        ContractEventDetailsEntity entity = new ContractEventDetailsEntity();
        entity.setName(contractEventDetails.getName());
        entity.setStatus(contractEventDetails.getStatus());
        entity.setAddress(contractEventDetails.getAddress());
        entity.setBlockHash(contractEventDetails.getBlockHash());
        entity.setBlockNumber(contractEventDetails.getBlockNumber().longValue());
        entity.setTransactionHash(contractEventDetails.getTransactionHash());
        entity.setLogIndex(contractEventDetails.getLogIndex().longValue());
        entity.setEventSpecificationSignature(contractEventDetails.getEventSpecificationSignature());
        entity.setFilterId(contractEventDetails.getFilterId());
        entity.setParameters(toEntityParameters(contractEventDetails.getIndexedParameters(), contractEventDetails.getNonIndexedParameters()));
        return entity;
    }

    public List<EventParameterEntity> toEntityParameters(List<EventParameter> indexedParameters, List<EventParameter> nonIndexedParameters) {
        Stream<EventParameterEntity> indexedParamsStream = indexedParameters.stream().map(ep -> toEntityParameter(ep, true));
        Stream<EventParameterEntity> nonindexedParamsStream = indexedParameters.stream().map(ep -> toEntityParameter(ep, false));
        return concat(indexedParamsStream, nonindexedParamsStream).collect(toList());
    }

    EventParameterEntity toEntityParameter(EventParameter ep, boolean indexed) {
        EventParameterEntity eventParameterEntity = new EventParameterEntity();
        eventParameterEntity.setIndexed(indexed);
        eventParameterEntity.setType(ep.getType());
        if (ep instanceof StringParameter) {
            eventParameterEntity.setStringValue(((StringParameter) ep).getValue());
            eventParameterEntity.setIntValue(0);
        } else {
            eventParameterEntity.setStringValue(null);
            eventParameterEntity.setIntValue(((NumberParameter) ep).getValue().longValue());
        }
        return eventParameterEntity;
    }

    ContractEventDetails toContractEventDetails(ContractEventDetailsEntity eventDetailsEntity) {
        ContractEventDetails ret = new ContractEventDetails();
        ret.setName(eventDetailsEntity.getName());
        ret.setFilterId(eventDetailsEntity.getFilterId());
        ret.setIndexedParameters(toIndexedventParameters(eventDetailsEntity.getParameters()));
        ret.setNonIndexedParameters(toNonIndexedventParameters(eventDetailsEntity.getParameters()));
        ret.setTransactionHash(eventDetailsEntity.getTransactionHash());
        ret.setLogIndex(BigInteger.valueOf(eventDetailsEntity.getLogIndex()));
        ret.setBlockNumber(BigInteger.valueOf(eventDetailsEntity.getBlockNumber()));
        ret.setBlockHash(eventDetailsEntity.getBlockHash());
        ret.setAddress(eventDetailsEntity.getAddress());
        ret.setStatus(eventDetailsEntity.getStatus());
        ret.setEventSpecificationSignature(eventDetailsEntity.getEventSpecificationSignature());
        return ret;
    }

    List<EventParameter> toIndexedventParameters(List<EventParameterEntity> entities) {
        return entities.stream().filter(indexedEventParameterEntityPredicate).map(this::toEventParameter).collect(toList());
    }

    List<EventParameter> toNonIndexedventParameters(List<EventParameterEntity> entities) {
        return entities.stream().filter(indexedEventParameterEntityPredicate.negate()).map(this::toEventParameter).collect(toList());
    }

    EventParameter toEventParameter(EventParameterEntity e) {
        if (e.getStringValue() != null) {
            StringParameter stringParameter = new StringParameter();
            stringParameter.setType(e.getType());
            stringParameter.setValue(e.getStringValue());
            return stringParameter;
        } else {
            NumberParameter numberParameter = new NumberParameter();
            numberParameter.setType(e.getType());
            numberParameter.setValue(BigInteger.valueOf(e.getIntValue()));
            return numberParameter;
        }
    }
}
